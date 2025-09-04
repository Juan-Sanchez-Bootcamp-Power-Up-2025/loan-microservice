package co.com.crediya.loan.model.loanapplication;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationReview {

    private String email;

    private String documentId;

    private String status;

    private String type;

    private BigDecimal amount;

    private BigDecimal term;

    private BigDecimal totalMonthlyDebtApprovedLoans;

}
