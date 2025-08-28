package co.com.crediya.loan.r2dbc.repository.loanapplication;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.loan.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.loan.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

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
        return super.save(loanApplication);
    }

}
