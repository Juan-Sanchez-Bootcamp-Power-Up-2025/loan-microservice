package co.com.crediya.loan.r2dbc.repository.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, String>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query("""
            SELECT client_name, email, document_id, status, type, amount, term, base_salary, monthly_debt
            FROM loan_applications
            WHERE status != 'APPROVED'
            ORDER BY status
            """)
    Flux<LoanApplication> getLoanApplicationsWhereStatusNotApproved();

    @Query("""
            UPDATE loan_applications
            SET status = :status
            WHERE id = :loanApplicationId
            """)
    Mono<Integer> updateStatusLoanApplication(@Param("loanApplicationId") UUID loanApplicationId, @Param("status") String status);

    @Query("""
            SELECT client_name, email, document_id, status, type, amount, term, base_salary, monthly_debt
            FROM loan_applications
            WHERE id = :loanApplicationId
            """)
    Mono<LoanApplication> findByLoanApplicationId(@Param("loanApplicationId") UUID loanApplicationId);

}
