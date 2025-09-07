package co.com.crediya.loan.model.user.gateways;

import co.com.crediya.loan.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> validateUserByDocumentId(String email, String documentId);

}
