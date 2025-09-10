package co.com.crediya.loan.r2dbc.repository.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.loan.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

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
    public Flux<LoanApplication> getLoanApplicationsWhereStatusNotApproved() {
        log.debug("Querying loan applications with status different approved");
        return repository.getLoanApplicationsWhereStatusNotApproved();
    }

    @Override
    public Mono<LoanApplication> findByLoanApplicationId(UUID loanApplicationId) {
        log.debug("Querying loan application by id");
        return repository.findByLoanApplicationId(loanApplicationId);
    }

    @Override
    public Mono<Integer> updateStatusLoanApplication(UUID loanApplicationId, String status) {
        log.debug("Updating loan application {} with status {}", loanApplicationId, status);
        return repository.updateStatusLoanApplication(loanApplicationId, status);
    }

}
