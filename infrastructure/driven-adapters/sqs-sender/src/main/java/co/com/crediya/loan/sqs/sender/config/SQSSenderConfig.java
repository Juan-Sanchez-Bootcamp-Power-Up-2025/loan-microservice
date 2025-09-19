package co.com.crediya.loan.sqs.sender.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@ConditionalOnMissingBean(SqsAsyncClient.class)
public class SQSSenderConfig {

    @Bean
    public SqsAsyncClient configSqsNotifications(SQSNotificationsSenderProperties properties, MetricPublisher publisher) {
        return SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                        .create(properties.accessKeyId(), properties.secretAccessKey())))
                .build();
    }

    @Bean
    public SqsAsyncClient configSqsApprovedLoans(SQSApprovedLoansSenderProperties properties, MetricPublisher publisher) {
        return SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                        .create(properties.accessKeyId(), properties.secretAccessKey())))
                .build();
    }

}
