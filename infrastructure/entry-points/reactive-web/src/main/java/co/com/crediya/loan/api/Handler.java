package co.com.crediya.loan.api;

import co.com.crediya.loan.api.dto.LoanApplicationRequestDto;
import co.com.crediya.loan.api.mapper.LoanApplicationMapper;
import co.com.crediya.loan.usecase.loanapplication.LoanApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanApplicationUseCase loanApplicationUseCase;

    private final ErrorHandler errorHandler;

    private final Validator validator;

    public Mono<ServerResponse> listenSaveLoan(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .flatMap(dto -> {
                    Set<ConstraintViolation<LoanApplicationRequestDto>> violations = validator.validate(dto);
                    return violations.isEmpty() ? Mono.just(dto) : Mono.error(new ConstraintViolationException(violations));
                })
                .map(LoanApplicationMapper::toDomain)
                .flatMap(loanApplicationUseCase::saveLoanApplication)
                .flatMap(savedLoanApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedLoanApplication))
                .onErrorResume(errorHandler::handle);
    }

}
