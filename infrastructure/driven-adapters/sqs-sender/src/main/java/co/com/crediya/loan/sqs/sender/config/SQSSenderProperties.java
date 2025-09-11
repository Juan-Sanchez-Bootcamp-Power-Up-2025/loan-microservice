package co.com.crediya.loan.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.sqs")
public record SQSSenderProperties(
     String region,
     String queueUrl,
     String endpoint,
     String accessKeyId,
     String secretAccessKey
){}
