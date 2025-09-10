package co.com.crediya.loan.model.loanapplication.gateways;

import co.com.crediya.loan.model.loanapplication.SQSMessage;
import reactor.core.publisher.Mono;

public interface NotificationQueueGateway {

    Mono<Void> publishLoanApplicationStatusChanged(SQSMessage sqsMessage);

}
