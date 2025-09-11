package co.com.crediya.loan.usecase.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.LoanApplicationReview;
import co.com.crediya.loan.model.loanapplication.SQSMessage;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.model.loanapplication.gateways.NotificationQueueGateway;
import co.com.crediya.loan.model.loanapplication.pagination.PageResult;
import co.com.crediya.loan.model.loanstatus.gateways.LoanStatusRepository;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.model.user.gateways.UserRepository;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanApplicationNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanStatusNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    private final LoanTypeRepository loanTypeRepository;

    private final LoanStatusRepository loanStatusRepository;

    private final UserRepository userRepository;

    private final NotificationQueueGateway notificationQueueGateway;

    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        return userRepository.validateUserByDocumentId(loanApplication.getEmail(),
                        loanApplication.getDocumentId())
                .switchIfEmpty(Mono.error(new UserNotFoundException(loanApplication.getDocumentId())))
                .flatMap(user -> loanTypeRepository.findByTypeId(loanApplication.getType())
                        .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(loanApplication.getType())))
                        .flatMap(loanType -> {
                            loanApplication.setClientName(user.getName());
                            loanApplication.setBaseSalary(user.getBaseSalary());
                            loanApplication.setStatus("PENDING");
                            loanApplication.setMonthlyDebt(calculateMonthlyFee(loanApplication.getAmount(),
                                    loanApplication.getTerm(), loanType.getInterestRate()));
                            return loanApplicationRepository.saveLoanApplication(loanApplication);
                        }));
    }

    private BigDecimal calculateMonthlyFee(BigDecimal amount, BigDecimal term, BigDecimal interestRate) {
        BigDecimal monthlyInterestRate = interestRate.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        double i = monthlyInterestRate.doubleValue();
        double n = term.doubleValue();
        double m = amount.doubleValue();
        double debt = (m * i) / (1 - Math.pow(1 + i, -n));
        return BigDecimal.valueOf(debt).setScale(2, RoundingMode.HALF_UP);
    }

    public Flux<LoanApplicationReview> listLoanApplicationsForConsultant() {
        return loanApplicationRepository.getLoanApplicationsWhereStatusNotApproved()
                .flatMap(loanApplication ->
                        loanTypeRepository.findByTypeId(loanApplication.getType())
                                .map(loanType -> toReview(loanApplication, loanType.getInterestRate())));
    }

    public Mono<PageResult> listLoanApplicationsForConsultantPaginate(int page, int size) {
        return loanApplicationRepository.getLoanApplicationsWhereStatusNotApproved()
                .flatMap(loanApplication ->
                        loanTypeRepository.findByTypeId(loanApplication.getType())
                                .map(loanType -> toReview(loanApplication, loanType.getInterestRate())))
                .collectList()
                .map(list -> {
                    int total = list.size();
                    int start = (page - 1) * size;
                    List<LoanApplicationReview> loanApplicationReviews = list.stream().skip(start).limit(size).toList();
                    return new PageResult(loanApplicationReviews, total, page, size);
                });
    }

    private LoanApplicationReview toReview(LoanApplication loanApplication, BigDecimal interestRate) {
        return new LoanApplicationReview(
                loanApplication.getClientName(),
                loanApplication.getEmail(),
                loanApplication.getDocumentId(),
                loanApplication.getStatus(),
                loanApplication.getType(),
                interestRate,
                loanApplication.getAmount(),
                loanApplication.getTerm(),
                loanApplication.getBaseSalary(),
                loanApplication.getMonthlyDebt()
        );
    }

    public Mono<LoanApplication> updateStatusLoanApplication(UUID loanApplicationId, String status) {
        return loanApplicationRepository.findByLoanApplicationId(loanApplicationId)
                .switchIfEmpty(Mono.error(new LoanApplicationNotFoundException()))
                .flatMap(loanApplication -> loanStatusRepository.existsById(status)
                                .flatMap(valid -> valid
                                        ? loanApplicationRepository.updateStatusLoanApplication(loanApplicationId, status)
                                        .switchIfEmpty(Mono.error(new IllegalStateException("The loan application was not updated")))
                                        .flatMap(updatedloanApplication ->
                                                notificationQueueGateway.publishLoanApplicationStatusChanged(createSQSMessage(updatedloanApplication, loanApplicationId))
                                                .doOnError(error -> System.out.println("Error trying to send SQS message"))
                                                .thenReturn(updatedloanApplication))
                                        : Mono.error(new LoanStatusNotFoundException(status))
                                )
                );
    }

    private SQSMessage createSQSMessage(LoanApplication loanApplication, UUID loanApplicationId) {
        return SQSMessage.builder()
                .to(loanApplication.getEmail())
                .subject("Your loan application has been " + loanApplication.getStatus().toLowerCase())
                .body(
                        "Dear " + loanApplication.getClientName() + ". \n\n" +
                                "Your loan application " + loanApplicationId.toString() + " has been " + loanApplication.getStatus().toLowerCase() + ". \n" +
                                "Here are the details of the loan application:\n" +
                                "Amount: " + loanApplication.getAmount() + "\n" +
                                "Term: " + loanApplication.getTerm() + "\n" +
                                "Monthly debt: " + loanApplication.getMonthlyDebt().toString() + "\n\n" +
                                "Please do not respond to this email."
                )
                .build();
    }

}
