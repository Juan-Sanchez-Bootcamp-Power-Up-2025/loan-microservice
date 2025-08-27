package co.com.crediya.loan.model.loanapplication;

import co.com.crediya.loan.model.loanstatus.LoanStatus;
import co.com.crediya.loan.model.loantype.LoanType;
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

    private BigDecimal amount;

    private int term;

    private String email;

    private String status;

    private String type;

    private String documentId;

}
