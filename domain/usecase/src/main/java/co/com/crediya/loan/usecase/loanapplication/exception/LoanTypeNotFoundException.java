package co.com.crediya.loan.usecase.loanapplication.exception;

public class LoanTypeNotFoundException extends RuntimeException {

    public LoanTypeNotFoundException(String type) {
        super("Loan type " + type + " not found in data base. Please check the type.");
    }

}
