package co.com.crediya.loan.api;

import co.com.crediya.loan.api.dto.LoanApplicationRequestDto;
import co.com.crediya.loan.api.dto.StatusRequestDto;
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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    @PreAuthorize("hasAuthority('CONSULTANT')")
    public Mono<ServerResponse> listenGetLoansList(ServerRequest serverRequest) {
        return loanApplicationUseCase.listLoanApplicationsForConsultant()
                .collectList()
                .doOnSubscribe(subscription -> log.debug(">> GET /api/v1/loans - start"))
                .flatMap(loansList ->
                        ServerResponse.ok().header("Loans-list-pagination-enabled", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loansList))
                .doOnSuccess(success -> log.info("Got loans list for consultant user"))
                .doOnError(error -> log.error("Error trying to get loans list: {}", error.getMessage()))
                .doFinally(signalType -> log.debug("<< GET /api/v1/loans - end"));
    }

    @PreAuthorize("hasAuthority('CONSULTANT')")
    public Mono<ServerResponse> listenGetLoansListPaginate(ServerRequest serverRequest) {
        Optional<Integer> page = serverRequest.queryParam("page").map(Integer::valueOf);
        Optional<Integer> size = serverRequest.queryParam("size").map(Integer::valueOf);
        if (page.isPresent() && size.isPresent()) {
            return loanApplicationUseCase.listLoanApplicationsForConsultantPaginate(page.get(), size.get())
                    .doOnSubscribe(subscription -> log.debug(">> GET Paginate /api/v1/loans - start"))
                    .flatMap(loansList ->
                            ServerResponse.ok().header("Loans-list-pagination-enabled", "true")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(loansList))
                    .doOnSuccess(success -> log.info("Got loans list for consultant user paginate"))
                    .doOnError(error -> log.error("Error trying to get loans list paginate: {}", error.getMessage()))
                    .doFinally(signalType -> log.debug("<< GET Paginate /api/v1/loans - end"));
        }
        return listenGetLoansList(serverRequest);
    }

    @PreAuthorize("hasAuthority('CONSULTANT')")
    public Mono<ServerResponse> listenUpdateStatusLoanApplication(ServerRequest serverRequest) {
        UUID loanApplicationId = UUID.fromString(serverRequest.pathVariable("id"));
        return serverRequest.bodyToMono(StatusRequestDto.class)
                .doOnSubscribe(subscription -> log.debug(">> PUT /api/v1/loans/{id} - start"))
                .flatMap(dto -> {
                    Set<ConstraintViolation<StatusRequestDto>> violations = validator.validate(dto);
                    return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                })
                .flatMap(statusRequestDto ->
                        loanApplicationUseCase.updateStatusLoanApplication(loanApplicationId, statusRequestDto.status())
                ).doOnSuccess(success -> log.info("Loan updated in the database"))
                .doOnError(error -> log.error("Loan update failed: {}", error.getMessage()))
                .flatMap(updatedLoanApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedLoanApplication))
                .as(transactionalOperator::transactional)
                .onErrorResume(errorHandler::handle)
                .doFinally(signalType -> log.debug("<< PUT /api/v1/loans/{id} - end"));
    }

}
