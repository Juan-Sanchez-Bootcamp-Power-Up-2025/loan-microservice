package co.com.crediya.loan.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface IdentityVerificationGateway {

    Mono<Boolean> validateUserByDocumentId(String email, String documentId);

}
