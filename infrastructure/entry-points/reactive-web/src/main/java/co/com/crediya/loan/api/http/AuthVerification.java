package co.com.crediya.loan.api.http;

import co.com.crediya.loan.model.user.User;
import co.com.crediya.loan.model.user.gateways.UserGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthVerification implements UserGateway {

    private final WebClient authWebClient;

    public AuthVerification(@Qualifier("authWebClient") WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    public Mono<User> validateUserByDocumentId(String email, String documentId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> !authentication.getPrincipal().equals(email)
                        ? Mono.error(new RuntimeException("Can't apply for a loan for user " + email))
                        : authWebClient.get()
                        .uri(uri -> uri.path("/api/v1/users/validate")
                                .queryParam("email", email)
                                .queryParam("documentId", documentId)
                                .build())
                        .headers(header -> header.setBearerAuth(authentication.getDetails().toString()))
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, response -> {
                            log.debug("Authentication service 4xx");
                            return Mono.error(new RuntimeException("User with id " + documentId + " was not found"));
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, response -> {
                            log.debug("Authentication service 5xx");
                            return Mono.error(new RuntimeException("Authentication service not available"));
                        })
                        .bodyToMono(User.class)
                )
                .doOnSuccess(ok -> log.debug("User found with documentId={}", documentId));
    }

}

