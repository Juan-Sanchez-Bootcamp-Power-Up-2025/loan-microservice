package co.com.crediya.loan.model.loantype.gateways;

import co.com.crediya.loan.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {

    Mono<LoanType> findByTypeId(String typeId);

}
