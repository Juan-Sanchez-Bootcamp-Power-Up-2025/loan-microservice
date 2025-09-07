package co.com.crediya.loan.model.loanapplication;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {

    private String clientName;

    private String email;

    private String documentId;

    private BigDecimal baseSalary;

    private String status;

    private String type;

    private BigDecimal amount;

    private BigDecimal term;

    private BigDecimal monthlyDebt;

}
