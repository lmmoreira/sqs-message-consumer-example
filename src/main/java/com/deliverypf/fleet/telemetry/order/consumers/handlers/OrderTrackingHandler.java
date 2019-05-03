package com.deliverypf.fleet.telemetry.order.consumers.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.sqs.model.Message;
import com.deliverypf.fleet.telemetry.order.dto.TelemetryDTO;
import com.deliverypf.fleet.telemetry.order.dto.converters.TelemetryDTOConverter;
import com.deliverypf.fleet.telemetry.order.utils.TelemetryUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lorem.logistics.event.amazon.sqs.handler.AbstractMultipleAmazonSQSMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderTrackingHandler extends AbstractMultipleAmazonSQSMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderTrackingHandler.class);

    private final AmazonDynamoDBAsync ddb;
    private final TelemetryDTOConverter converter;
    private final String orderTableName;
    private final String routeTableName;
    private final long ttlInHours;
    private final int writeBatchSize;

    public OrderTrackingHandler(final AmazonDynamoDBAsync ddb, final TelemetryDTOConverter converter,
            final String orderTableName, final String routeTableName, final long ttlInHours, final int writeBatchSize) {
        this.ddb = ddb;
        this.converter = converter;
        this.orderTableName = orderTableName;
        this.routeTableName = routeTableName;
        this.ttlInHours = ttlInHours;
        this.writeBatchSize = writeBatchSize;
    }

    @Override
    protected Mono<Void> handleMessages(final List<Message> messages) {
        LOGGER.debug("Handling {} messages", messages.size());
        return Mono.fromRunnable(() -> {
            final Map<String, Set<Map<String, AttributeValue>>> itemsMap = createItemsMap(toTelemetries(messages));
            writeItemsOnDynamoDB(itemsMap.get(orderTableName), orderTableName);
            writeItemsOnDynamoDB(itemsMap.get(routeTableName), routeTableName);
        });
    }

    private List<TelemetryDTO> toTelemetries(final List<Message> messages) {
        return messages.stream() //
                .map(Message::getBody)
                .map(converter::fromString)
                .collect(Collectors.toList());
    }

    private void writeItemsOnDynamoDB(final Set<Map<String, AttributeValue>> items, final String tableName) {
        Lists.partition(Lists.newArrayList(items), writeBatchSize)
                .forEach(subItems -> ddb.batchWriteItem(Collections.singletonMap(tableName,
                    subItems.stream().map(i -> new WriteRequest(new PutRequest(i))).collect(Collectors.toList()))));
    }

    private Map<String, Set<Map<String, AttributeValue>>> createItemsMap(final List<TelemetryDTO> telemetries) {
        final Long ttlEpochSeconds = getTtlEpochSeconds();

        final Map<String, Set<Map<String, AttributeValue>>> itemsMap =
            ImmutableMap.of(routeTableName, Sets.newHashSet(), orderTableName, Sets.newHashSet());
        itemsMap.get(routeTableName)
                .addAll(telemetries.stream()
                        .filter(t -> Objects.nonNull(t.getRouteId()))
                        .map(t -> TelemetryUtils.createItemMap(t.getRouteId(), t, ttlEpochSeconds))
                        .collect(Collectors.toSet()));
        telemetries.forEach(telemetry -> itemsMap.get(orderTableName)
                .addAll(telemetry.getOrders()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(o -> Objects.nonNull(o.getOrderId()))
                        .map(o -> TelemetryUtils.createItemMap(o.getOrderId(), telemetry, ttlEpochSeconds))
                        .collect(Collectors.toSet())));
        return itemsMap;
    }

    private Long getTtlEpochSeconds() {
        return Instant.now().plus(ttlInHours, ChronoUnit.HOURS).getEpochSecond();
    }
}
