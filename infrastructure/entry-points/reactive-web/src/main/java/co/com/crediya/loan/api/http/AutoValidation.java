package co.com.crediya.loan.api.http;

import co.com.crediya.loan.model.validation.CapacityRequest;
import co.com.crediya.loan.model.validation.Validation;
import co.com.crediya.loan.model.validation.gateways.ValidationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoValidation implements ValidationGateway {

    private final WebClient autoValidationWebConfig;

    @Override
    public Mono<Validation> calculateAutomaticValidation(CapacityRequest capacityRequest) {
        return autoValidationWebConfig.post()
                .uri(uri -> uri.path("/api/v1/calculate-capacity")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(capacityRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.debug("Authentication service 4xx");
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new RuntimeException("Capacity 4xx: " + body)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.debug("Authentication service 5xx");
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new RuntimeException("Capacity 5xx: " + body)));
                })
                .bodyToMono(Validation.class)
                .doOnSubscribe(s -> log.trace("Calling Capacity Lambda"))
                .doOnSuccess(validation -> log.info("Capacity decision for ={} -> {}", capacityRequest, validation.getStatus()))
                .doOnError(e -> log.error("Capacity call failed: {}", e.toString()));
    }

}
