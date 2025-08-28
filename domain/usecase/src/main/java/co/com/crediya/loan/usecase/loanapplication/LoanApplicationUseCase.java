package co.com.crediya.loan.usecase.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        return loanTypeRepository.existsById(loanApplication.getType())
                .flatMap(type -> type ? loanApplicationRepository.saveLoanApplication(loanApplication)
                        : Mono.error(new LoanTypeNotFoundException(loanApplication.getType())));
    }

}
