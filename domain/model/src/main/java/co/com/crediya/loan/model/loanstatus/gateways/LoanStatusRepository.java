package co.com.crediya.loan.model.loanstatus.gateways;

import reactor.core.publisher.Mono;

public interface LoanStatusRepository {

    Mono<Boolean> existsById(String statusId);

}
