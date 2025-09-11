package co.com.crediya.loan.usecase.loanapplication.exception;

public class LoanStatusNotFoundException extends RuntimeException {

    public LoanStatusNotFoundException(String status) {
        super("Loan status " + status + " not found in data base. Please check the status.");
    }

}
