package co.com.crediya.loan.model.loanapplication.gateways;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication);

}
