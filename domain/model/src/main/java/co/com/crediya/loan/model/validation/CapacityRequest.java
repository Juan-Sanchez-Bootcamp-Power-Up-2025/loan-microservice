package co.com.crediya.loan.model.validation;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityRequest {

    LoanApplication loanApplication;

    List<LoanApplication> loanApplicationsApproved;

}
