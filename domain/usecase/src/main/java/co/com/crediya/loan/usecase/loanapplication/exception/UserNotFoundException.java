package co.com.crediya.loan.usecase.loanapplication.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email, String documentId) {
        super("User with email = "+email+" and document id = "+documentId+" was not found.");
    }
}
