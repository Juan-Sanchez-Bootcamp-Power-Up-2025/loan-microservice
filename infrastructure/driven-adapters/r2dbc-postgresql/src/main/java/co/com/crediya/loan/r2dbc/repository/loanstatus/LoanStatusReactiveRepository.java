package co.com.crediya.loan.r2dbc.repository.loanstatus;

import co.com.crediya.loan.r2dbc.entity.LoanStatusEntity;
import co.com.crediya.loan.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanStatusReactiveRepository extends ReactiveCrudRepository<LoanStatusEntity, String>, ReactiveQueryByExampleExecutor<LoanStatusEntity> {

}
