package co.com.crediya.loan.usecase.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.IdentityVerificationGateway;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;

    private final LoanTypeRepository loanTypeRepository;

    private final IdentityVerificationGateway identityVerificationGateway;

    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        return identityVerificationGateway.validateUserWithEmailAndDocument(loanApplication.getEmail(), loanApplication.getDocumentId())
                .flatMap(valid -> {
                    if (!valid) return Mono.error(new UserNotFoundException(loanApplication.getEmail(), loanApplication.getDocumentId()));
                    return loanTypeRepository.existsById(loanApplication.getType());
                })
                .flatMap(typeExists -> typeExists ? loanApplicationRepository.saveLoanApplication(loanApplication)
                        : Mono.error(new LoanTypeNotFoundException(loanApplication.getType())));
    }

}
