package co.com.crediya.loan.sqs.sender;

import co.com.crediya.loan.model.loanapplication.SQSMessage;
import co.com.crediya.loan.model.loanapplication.gateways.NotificationQueueGateway;
import co.com.crediya.loan.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationQueueGateway {

    private final SQSSenderProperties properties;

    private final SqsAsyncClient client;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publishLoanApplicationStatusChanged(SQSMessage sqsMessage) {
        return send(sqsMessage);
    }

    private Mono<Void> send(SQSMessage sqsMessage) {
        return Mono.fromCallable(() -> buildRequest(sqsMessage))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .doOnError(error -> log.error("SQS message failed: {}", error.getMessage()))
                .then();
    }

    private SendMessageRequest buildRequest(SQSMessage sqsMessage) throws JsonProcessingException {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(toJson(sqsMessage))
                .build();
    }

    private String toJson(SQSMessage sqsMessage) throws JsonProcessingException {
        return objectMapper.writeValueAsString(sqsMessage);
    }

}
