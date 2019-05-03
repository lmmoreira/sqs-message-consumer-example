package com.deliverypf.fleet.telemetry.order.configs;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.lorem.file.AmazonS3ServiceImpl;
import com.lorem.file.FileRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("localstack")
public class LocalStackConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStackConfig.class);

    @Value("${cloud.aws.region.static}")
    private String amazonRegion;

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQS(@Value("${cloud.aws.sqs.endpoint}") final String amazonSqsEndpoint) {
        LOGGER.debug("Creating AmazonSQS, endpoint: {}, region: {}", amazonSqsEndpoint, amazonRegion);
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonSqsEndpoint, amazonRegion))
                .build();
    }

    @Bean
    @Primary
    public AmazonDynamoDBAsync amazonDynamoDB(@Value("${cloud.aws.ddb.endpoint}") final String amazonDdbEndpoint) {
        LOGGER.debug("Creating AmazonDynamoDB, endpoint: {}, region: {}", amazonDdbEndpoint, amazonRegion);
        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDdbEndpoint, amazonRegion))
                .build();
    }

    @Bean
    @Primary
    public AmazonS3 amazonS3(@Value("${cloud.aws.s3.endpoint}") final String amazonS3Endpoint) {
        LOGGER.debug("Creating AmazonS3, endpoint: {}, region: {}", amazonS3Endpoint, amazonRegion);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonS3Endpoint, amazonRegion))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Bean
    @Primary
    public FileRepositoryService s3FileRepository(@Value("${fleet.telemetry.order.s3.bucket-name}") final String bucketName,
                                                  @Value("${cloud.aws.s3.endpoint}") final String amazonS3Endpoint) {
        final AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withRegion(amazonRegion);
        // The last argument is null 'cause "jsonConverter" is not used
        return new AmazonS3ServiceImpl(builder.getCredentials(), builder.getRegion(), bucketName, null, amazonS3Endpoint);
    }

}
