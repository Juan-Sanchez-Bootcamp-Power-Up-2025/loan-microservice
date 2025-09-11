package co.com.crediya.loan.usecase.loanapplication.exception;

public class LoanApplicationNotFoundException extends RuntimeException {

    public LoanApplicationNotFoundException() {
        super("Loan application not found.");
    }

}