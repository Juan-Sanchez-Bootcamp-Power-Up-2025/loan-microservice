package co.com.crediya.loan.model.loanapplication.gateways;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface LoanApplicationRepository {

    Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication);

    Mono<BigDecimal> getUserTotalSumLoanApplicationsApproved(String documentId);

    Flux<LoanApplication> getLoanApplicationsWhereStatusNotApproved();

}
