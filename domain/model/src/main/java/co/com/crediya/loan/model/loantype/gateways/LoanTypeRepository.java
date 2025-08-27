package co.com.crediya.loan.model.loantype.gateways;

import reactor.core.publisher.Mono;

public interface LoanTypeRepository {

    Mono<Boolean> existsById(String typeId);

}
