package co.com.crediya.loan.r2dbc.repository.loantype;

import co.com.crediya.loan.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, String>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {

}
