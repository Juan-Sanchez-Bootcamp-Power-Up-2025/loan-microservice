package co.com.crediya.loan.r2dbc.repository.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.loan.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        String,
        LoanApplicationReactiveRepository
>  implements LoanApplicationRepository {

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanApplication.class));
    }

    @Override
    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        log.debug("Saving loan in the database");
        return super.save(loanApplication);
    }

    @Override
    public Mono<BigDecimal> getUserTotalSumLoanApplicationsApproved(String documentId) {
        log.debug("Getting sum of approved monthly fees by document id");
        return repository.sumApprovedFeesByDocumentId(documentId);
    }

    @Override
    public Flux<LoanApplication> getLoanApplicationsWhereStatusNotApproved() {
        log.debug("Getting loan applications with status different approved");
        return repository.getLoanApplicationsWhereStatusNotApproved();
    }

}
