package co.com.crediya.loan.model.loanapplication;

import lombok.Builder;

@Builder
public record SQSMessage(

        String to,

        String subject,

        String body

){}
