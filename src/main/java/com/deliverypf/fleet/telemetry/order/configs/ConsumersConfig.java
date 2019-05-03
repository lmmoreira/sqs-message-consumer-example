package com.deliverypf.fleet.telemetry.order.configs;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.sqs.AmazonSQS;
import com.deliverypf.fleet.telemetry.order.consumers.handlers.GenericConsolidateHandler;
import com.deliverypf.fleet.telemetry.order.consumers.handlers.OrderTrackingHandler;
import com.deliverypf.fleet.telemetry.order.dto.converters.OrderStateDTOConverter;
import com.deliverypf.fleet.telemetry.order.dto.converters.RouteStateDTOConverter;
import com.deliverypf.fleet.telemetry.order.dto.converters.TelemetryDTOConverter;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.OrderTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.RouteTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.s3.OrderS3RepositoryImpl;
import com.deliverypf.fleet.telemetry.order.repository.s3.RouteS3RepositoryImpl;
import com.lorem.logistics.event.Consumer;
import com.lorem.logistics.event.amazon.sqs.AmazonSQSConsumer;
import com.lorem.logistics.event.amazon.sqs.configuration.AmazonSQSProperties;
import com.lorem.logistics.event.amazon.sqs.configuration.GenericAmazonSQSProperties;
import com.lorem.logistics.event.amazon.sqs.handler.AmazonSQSMessageHandler;
import com.lorem.logistics.event.metrics.ConsumerMetricsLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableScheduling
public class ConsumersConfig implements SchedulingConfigurer {

    private static final Duration INTERVAL_LOG_METRICS = Duration.ofSeconds(60);

    @Autowired
    private List<Consumer> consumers;

    /*
     * Beans related to the consumer of Order Tracks
     */

    @Bean(value = "orderTrackingProperties")
    @ConfigurationProperties(prefix = "fleet.telemetry.order.tracking.consumer")
    public GenericAmazonSQSProperties orderTrackingProperties() {
        return new GenericAmazonSQSProperties();
    }

    @Bean("orderTrackingHandler")
    public OrderTrackingHandler orderTrackingHandler(final AmazonDynamoDBAsync amazonDynamoDB,
                                                     @Value("${fleet.telemetry.order.dynamodb.ttl-hours:48}") final long ttlInHours,
                                                     @Value("${fleet.telemetry.order.dynamodb.order-table:fleet_route_tracking}")
                                                     final String orderTableName,
                                                     @Value("${fleet.telemetry.order.dynamodb.route-table:fleet_order_tracking}")
                                                     final String routeTableName,
                                                     @Value("${fleet.telemetry.order.dynamodb.write-batch-size:25}")
                                                     final int writeBatchSize) {
        return new OrderTrackingHandler(amazonDynamoDB, new TelemetryDTOConverter(), orderTableName, routeTableName,
            ttlInHours, writeBatchSize);
    }

    @Bean(name = "orderTrackingConsumer", destroyMethod = "stop")
    public Consumer orderTrackingConsumer(final AmazonSQS amazonSQS, //
                                          @Qualifier("orderTrackingProperties") //
                                          final AmazonSQSProperties orderTrackingProperties,
                                          @Qualifier("orderTrackingHandler") //
                                          final AmazonSQSMessageHandler orderTrackingHandler) {
        return new AmazonSQSConsumer(amazonSQS, orderTrackingProperties, orderTrackingHandler);
    }

    /*
     * Beans related to the consumer of Order Consolidation
     */

    @Bean(value = "orderConsolidateProperties")
    @ConfigurationProperties(prefix = "fleet.telemetry.order.consolidate.order.consumer")
    public GenericAmazonSQSProperties orderConsolidateProperties() {
        return new GenericAmazonSQSProperties();
    }

    @Bean("orderConsolidateHandler")
    public AmazonSQSMessageHandler orderConsolidateHandler(@Qualifier("orderRepository") final OrderTrackDynamoDBRepository ddbRepository,
                                                           @Qualifier("orderS3Repository") final OrderS3RepositoryImpl s3Repository) {
        return new GenericConsolidateHandler<>(new OrderStateDTOConverter(), ddbRepository, s3Repository);
    }

    @Bean(value = "orderConsolidateConsumer", destroyMethod = "stop")
    public Consumer orderConsolidateConsumer(final AmazonSQS amazonSQS, //
                                             @Qualifier("orderConsolidateProperties") //
                                             final AmazonSQSProperties orderConsolidateProperties,
                                             @Qualifier("orderConsolidateHandler") //
                                             final AmazonSQSMessageHandler orderConsolidateHandler) {
        return new AmazonSQSConsumer(amazonSQS, orderConsolidateProperties, orderConsolidateHandler);
    }

    /*
     * Beans related to the consumer of Route Consolidation
     */

    @Bean(value = "routeConsolidateProperties")
    @ConfigurationProperties(prefix = "fleet.telemetry.order.consolidate.route.consumer")
    public GenericAmazonSQSProperties routeConsolidateProperties() {
        return new GenericAmazonSQSProperties();
    }

    @Bean("routeConsolidateHandler")
    public AmazonSQSMessageHandler routeConsolidateHandler(@Qualifier("routeRepository") final RouteTrackDynamoDBRepository ddbRepository,
                                                           @Qualifier("routeS3Repository") final RouteS3RepositoryImpl s3Repository) {
        return new GenericConsolidateHandler<>(new RouteStateDTOConverter(), ddbRepository, s3Repository);
    }

    @Bean(name = "routeConsolidateConsumer", destroyMethod = "stop")
    public Consumer routeConsolidateConsumer(final AmazonSQS amazonSQS, //
                                             @Qualifier("routeConsolidateProperties") //
                                             final AmazonSQSProperties orderConsolidateProperties,
                                             @Qualifier("routeConsolidateHandler") //
                                             final AmazonSQSMessageHandler orderConsolidateHandler) {
        return new AmazonSQSConsumer(amazonSQS, orderConsolidateProperties, orderConsolidateHandler);
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedDelayTask(new ConsumerMetricsLogger(consumers), INTERVAL_LOG_METRICS.toMillis());
    }
}
