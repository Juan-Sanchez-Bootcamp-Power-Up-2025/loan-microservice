package co.com.crediya.loan.model.validation.gateways;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.validation.CapacityRequest;
import co.com.crediya.loan.model.validation.Validation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ValidationGateway {

    Mono<Validation> calculateAutomaticValidation(CapacityRequest capacityRequest);

}
