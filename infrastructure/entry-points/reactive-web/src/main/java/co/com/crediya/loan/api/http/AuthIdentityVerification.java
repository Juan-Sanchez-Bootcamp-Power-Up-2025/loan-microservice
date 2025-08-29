package co.com.crediya.loan.api.http;

import co.com.crediya.loan.api.dto.ValidateResponseDto;
import co.com.crediya.loan.model.loanapplication.gateways.IdentityVerificationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthIdentityVerification implements IdentityVerificationGateway {

    private final WebClient authWebClient;

    @Override
    public Mono<Boolean> validateUserWithEmailAndDocument(String email, String documentId) {
        return authWebClient.get()
                .uri(uri -> uri.path("/api/v1/users/validate")
                        .queryParam("email", email)
                        .queryParam("documentId", documentId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.debug("Authentication service 5xx");
                    return Mono.error(new RuntimeException("Authentication service not available"));
                })
                .bodyToMono(ValidateResponseDto.class)
                .map(ValidateResponseDto::valid)
                .doOnSuccess(ok -> log.debug("User found with email={} documentId={}  => {}", email, documentId, ok));
    }

}

