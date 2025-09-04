package co.com.crediya.loan.usecase.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.IdentityVerificationGateway;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    private final LoanTypeRepository loanTypeRepository;

    private final IdentityVerificationGateway identityVerificationGateway;

    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        return identityVerificationGateway.validateUserWithEmailAndDocument(loanApplication.getEmail(),
                        loanApplication.getDocumentId())
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new UserNotFoundException(loanApplication.getEmail(),
                                loanApplication.getDocumentId()));
                    }
                    return loanTypeRepository.findById(loanApplication.getType())
                            .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(loanApplication.getType())))
                            .flatMap(loanType -> {
                                loanApplication.setStatus("PENDING");
                                loanApplication.setMonthlyFee(calculateMonthlyFee(loanApplication.getAmount(),
                                        loanApplication.getTerm(), loanType.getInterestRate()));
                                return loanApplicationRepository.saveLoanApplication(loanApplication);
                            });
                });
    }

    private BigDecimal calculateMonthlyFee(BigDecimal amount, BigDecimal term, BigDecimal interestRate) {
        BigDecimal monthlyInterestRate = interestRate.divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);
        double i = monthlyInterestRate.doubleValue();
        double n = term.doubleValue();
        double m = amount.doubleValue();
        double fee = (m * i) / (1 - Math.pow(1 + i, -n));
        return BigDecimal.valueOf(fee).setScale(2, RoundingMode.HALF_UP);
    }

}
