package com.deliverypf.fleet.telemetry.order;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.lorem.file.FileRepositoryService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@EnableAutoConfiguration
public class TestConfig {

    @Bean
    public AmazonSQSAsync amazonSQS() {
        final ReceiveMessageResult result = mock(ReceiveMessageResult.class);
        when(result.getMessages()).thenReturn(Collections.emptyList());
        final AmazonSQSAsync sqs = mock(AmazonSQSAsync.class);
        when(sqs.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(result);
        return sqs;
    }

    @Bean
    public AmazonDynamoDBAsync amazonDynamoDB() {
        return mock(AmazonDynamoDBAsync.class);
    }

    @Bean
    public FileRepositoryService s3FileRepository() {
        return mock(FileRepositoryService.class);
    }
}
