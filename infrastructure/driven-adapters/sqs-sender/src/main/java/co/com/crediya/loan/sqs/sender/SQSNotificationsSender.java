package co.com.crediya.loan.sqs.sender;

import co.com.crediya.loan.model.loanapplication.SQSMessage;
import co.com.crediya.loan.model.loanapplication.gateways.NotificationQueueGateway;
import co.com.crediya.loan.sqs.sender.config.SQSNotificationsSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
public class SQSNotificationsSender implements NotificationQueueGateway {

    private final SQSNotificationsSenderProperties properties;

    private final SqsAsyncClient client;

    private final ObjectMapper objectMapper;

    public SQSNotificationsSender(SQSNotificationsSenderProperties properties, @Qualifier("configSqsNotifications") SqsAsyncClient client, ObjectMapper objectMapper) {
        this.properties = properties;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publishLoanApplicationStatusChanged(SQSMessage sqsMessage) {
        return send(sqsMessage);
    }

    private Mono<Void> send(SQSMessage sqsMessage) {
        return Mono.fromCallable(() -> buildRequest(sqsMessage))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message notification sent {}", response.messageId()))
                .doOnError(error -> log.error("SQS message notification failed: {}", error.getMessage()))
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
