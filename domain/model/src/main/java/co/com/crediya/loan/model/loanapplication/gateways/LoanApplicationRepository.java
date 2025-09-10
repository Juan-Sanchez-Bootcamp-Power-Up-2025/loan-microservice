package co.com.crediya.loan.model.loanapplication.gateways;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationRepository {

    Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication);

    Flux<LoanApplication> getLoanApplicationsWhereStatusNotApproved();

    Mono<LoanApplication> findByLoanApplicationId(UUID loanApplicationId);

    Mono<LoanApplication> updateStatusLoanApplication(UUID loanApplicationId, String status);

}
