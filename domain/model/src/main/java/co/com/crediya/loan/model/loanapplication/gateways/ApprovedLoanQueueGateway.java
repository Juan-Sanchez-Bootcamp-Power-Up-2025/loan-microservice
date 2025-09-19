package co.com.crediya.loan.model.loanapplication.gateways;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface ApprovedLoanQueueGateway {

    Mono<Void> publishLoanApplication(LoanApplication loanApplication);

}
