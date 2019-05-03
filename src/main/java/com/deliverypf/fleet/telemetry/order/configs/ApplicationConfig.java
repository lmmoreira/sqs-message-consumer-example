package com.deliverypf.fleet.telemetry.order.configs;

import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.lorem.file.AmazonS3ServiceImpl;
import com.lorem.file.FileRepositoryService;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.dynamodb.DynamoDBLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class ApplicationConfig {

    @Bean
    public AmazonSQSAsync amazonSQS() {
        return new AmazonSQSBufferedAsyncClient(AmazonSQSAsyncClientBuilder.defaultClient());
    }

    @Bean
    public AmazonDynamoDBAsync amazonDynamoDB() {
        return AmazonDynamoDBAsyncClientBuilder.defaultClient();
    }

    @Bean
    public FileRepositoryService s3FileRepository(@Value("${fleet.telemetry.order.s3.bucket-name}") final String bucketName) {

        // The last argument is null 'cause "jsonConverter" is not used
        return new AmazonS3ServiceImpl(AmazonS3ClientBuilder.standard().getCredentials(),
            new DefaultAwsRegionProviderChain().getRegion(), bucketName, null);
    }

    @Bean
    public LockProvider lockProvider(final AmazonDynamoDBAsync amazonDynamoDB,
                                     @Value("${fleet.telemetry.order.dynamodb.schedule-lock-table:fleet_schedule_lock}")
                                     final //
                                     String lockTableName) {
        return new DynamoDBLockProvider(new DynamoDB(amazonDynamoDB).getTable(lockTableName));
    }

}
