package co.com.crediya.loan.r2dbc.repository.loanstatus;

import co.com.crediya.loan.model.loanstatus.LoanStatus;
import co.com.crediya.loan.model.loanstatus.gateways.LoanStatusRepository;
import co.com.crediya.loan.model.loantype.LoanType;
import co.com.crediya.loan.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.loan.r2dbc.entity.LoanStatusEntity;
import co.com.crediya.loan.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.loan.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class LoanStatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanStatus,
        LoanStatusEntity,
        String,
        LoanStatusReactiveRepository
>  implements LoanStatusRepository {

    public LoanStatusReactiveRepositoryAdapter(LoanStatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanStatus.class));
    }

    @Override
    public Mono<Boolean> existsById(String statusId) {
        log.debug("Querying loan status by id");
        return repository.existsById(statusId);
    }
}
