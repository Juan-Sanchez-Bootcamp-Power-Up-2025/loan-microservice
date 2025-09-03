package co.com.crediya.loan.api;

import co.com.crediya.loan.api.dto.LoanApplicationRequestDto;
import co.com.crediya.loan.api.mapper.LoanApplicationMapper;
import co.com.crediya.loan.usecase.loanapplication.LoanApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanApplicationUseCase loanApplicationUseCase;

    private final ErrorHandler errorHandler;

    private final Validator validator;

    private final TransactionalOperator transactionalOperator;

    @PreAuthorize("hasAuthority('CLIENT')")
    public Mono<ServerResponse> listenSaveLoan(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .doOnSubscribe(subscription -> log.debug(">> POST /api/v1/loans - start"))
                .flatMap(dto -> {
                    Set<ConstraintViolation<LoanApplicationRequestDto>> violations = validator.validate(dto);
                    return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                })
                .map(LoanApplicationMapper::toDomain)
                .flatMap(loanApplicationUseCase::saveLoanApplication)
                .doOnSuccess(success -> log.info("Loan created in the database"))
                .doOnError(error -> log.error("Loan creation failed: {}", error.getMessage()))
                .flatMap(savedLoanApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedLoanApplication))
                .as(transactionalOperator::transactional)
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< POST /api/v1/loans - end"));
    }

}
