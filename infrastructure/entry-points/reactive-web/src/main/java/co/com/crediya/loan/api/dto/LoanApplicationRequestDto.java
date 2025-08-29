package co.com.crediya.loan.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LoanApplicationRequestDto (

    @DecimalMin(value = "0.0", inclusive = false, message = "amount must be greater than 0")
    BigDecimal amount,

    @NotNull(message = "term is mandatory")
    @Min(value = 1, message = "term must be greater than 1")
    int term,

    @NotBlank(message = "email is mandatory")
    @Email(message = "email format is not valid", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    String email,

    @NotBlank(message = "type is mandatory")
    String type,

    @NotBlank(message = "document id is mandatory")
    String documentId

){}
