package co.com.crediya.loan.r2dbc.repository.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, String>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query("""
            SELECT SUM(monthly_fee)
            FROM loan_applications
            WHERE status = 'APPROVED'
                AND document_id = $1
            """)
    Mono<BigDecimal> sumApprovedFeesByDocumentId(String documentId);

    @Query("""
            SELECT email, document_id, status, type, amount, term
            FROM loan_applications
            WHERE status != 'APPROVED'
            ORDER BY status
            """)
    Flux<LoanApplication> getLoanApplicationsWhereStatusNotApproved();

    @Query("""
            SELECT email, document_id, status, type, amount, term
            FROM loan_applications
            WHERE status != 'APPROVED'
            ORDER BY status
            """)
    Flux<LoanApplication> getLoanApplicationsWhereStatusNotApprovedPaginate(Pageable pagination);

}
