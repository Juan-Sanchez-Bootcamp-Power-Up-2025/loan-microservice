package co.com.crediya.loan.api;

import co.com.crediya.loan.usecase.loanapplication.exception.LoanApplicationNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import co.com.crediya.loan.usecase.loanapplication.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ErrorHandler {

    public Mono<ServerResponse> handle(Throwable ex) {
        return switch (ex) {
            case ConstraintViolationException constraintViolationException ->
                    handleConstraintViolationException(constraintViolationException);
            case LoanTypeNotFoundException loanTypeNotFoundExceptionException ->
                    handleLoanTypeNotFoundException(loanTypeNotFoundExceptionException);
            case UserNotFoundException userNotFoundException -> handleUserNotFoundException(userNotFoundException);
            case LoanApplicationNotFoundException loanApplicationNotFoundException ->
                    handleLoanApplicationNotFoundException(loanApplicationNotFoundException);
            case null, default -> {
                assert ex != null;
                yield handleException(ex);
            }
        };
    }

    private Mono<ServerResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
                .map(violation -> Map.of("field", violation.getPropertyPath().toString(), "message", violation.getMessage()))
                .toList();
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Invalid data",
                "violations", violations
        ));
    }

    private Mono<ServerResponse> handleLoanTypeNotFoundException(LoanTypeNotFoundException ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Loan Type error",
                "violations", ex.getMessage()
        ));
    }

    private Mono<ServerResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "User error",
                "violations", ex.getMessage()
        ));
    }

    private Mono<ServerResponse> handleLoanApplicationNotFoundException(LoanApplicationNotFoundException ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Loan Application error",
                "violations", ex.getMessage()
        ));
    }

    private Mono<ServerResponse> handleException(Throwable ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Internal error",
                "violations", ex.getMessage()
        ));
    }

}
