package co.com.crediya.loan.api;

import co.com.crediya.loan.usecase.loanapplication.exception.LoanTypeNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ErrorHandler {

    public Mono<ServerResponse> handle(Throwable ex) {
        if (ex instanceof ConstraintViolationException constraintViolationException) {
            return handleConstraintViolationException(constraintViolationException);
        } else if (ex instanceof LoanTypeNotFoundException loanTypeNotFoundExceptionException) {
            return handleLoanTypeNotFoundExceptionException(loanTypeNotFoundExceptionException);
        } else {
            return handleException(ex);
        }
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

    private Mono<ServerResponse> handleLoanTypeNotFoundExceptionException(LoanTypeNotFoundException ex) {
        return ServerResponse.badRequest().bodyValue(Map.of(
                "error", "Loan Type error",
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
