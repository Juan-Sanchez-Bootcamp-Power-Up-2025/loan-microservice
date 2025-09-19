package co.com.crediya.loan.sqs.sender;

import co.com.crediya.loan.model.loanapplication.LoanApplication;
import co.com.crediya.loan.model.loanapplication.gateways.ApprovedLoanQueueGateway;
import co.com.crediya.loan.sqs.sender.config.SQSApprovedLoansSenderProperties;
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
public class SQSApprovedLoansSender implements ApprovedLoanQueueGateway {

    private final SQSApprovedLoansSenderProperties properties;

    private final SqsAsyncClient client;

    private final ObjectMapper objectMapper;

    public SQSApprovedLoansSender(SQSApprovedLoansSenderProperties properties, @Qualifier("configSqsApprovedLoans") SqsAsyncClient client, ObjectMapper objectMapper) {
        this.properties = properties;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publishLoanApplication(LoanApplication loanApplication) {
        return send(loanApplication);
    }

    private Mono<Void> send(LoanApplication loanApplication) {
        return Mono.fromCallable(() -> buildRequest(loanApplication))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message loan application sent {}", response.messageId()))
                .doOnError(error -> log.error("SQS message loan application failed: {}", error.getMessage()))
                .then();
    }

    private SendMessageRequest buildRequest(LoanApplication loanApplication) throws JsonProcessingException {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(toJson(loanApplication))
                .build();
    }

    private String toJson(LoanApplication loanApplication) throws JsonProcessingException {
        return objectMapper.writeValueAsString(loanApplication);
    }


}
