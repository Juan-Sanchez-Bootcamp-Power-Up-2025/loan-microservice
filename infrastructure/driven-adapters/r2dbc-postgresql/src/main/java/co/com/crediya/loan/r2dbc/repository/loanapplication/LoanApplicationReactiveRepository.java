package co.com.crediya.loan.r2dbc.repository.loanapplication;

import co.com.crediya.loan.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, String>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

}
