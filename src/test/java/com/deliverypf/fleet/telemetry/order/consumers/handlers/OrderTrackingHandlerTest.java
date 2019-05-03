package com.deliverypf.fleet.telemetry.order.consumers.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.sqs.model.Message;
import com.deliverypf.fleet.telemetry.order.dto.converters.TelemetryDTOConverter;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import com.lorem.logistics.event.amazon.sqs.handler.MessageDecorator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.util.ResourceUtils;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderTrackingHandlerTest {

    private final String orderTable = "orderTable";
    private final String routeTable = "routeTable";
    private AmazonDynamoDBAsync ddb;
    private int writeBatchSize;
    private final long ttlInHours = 48;

    @Before
    public void before() {
        writeBatchSize = 5;
        ddb = mock(AmazonDynamoDBAsync.class);
        when(ddb.batchWriteItem(anyMap())).thenReturn(new BatchWriteItemResult());
    }

    @Test
    public void shouldAvoidDuplication() {
        final OrderTrackingHandler worker = new OrderTrackingHandler(ddb, new TelemetryDTOConverter(), orderTable, routeTable,
            ttlInHours, writeBatchSize);
        final List<Message> messages = new ArrayList<>();
        IntStream.range(0, 2).forEach(i -> messages.add(new Message().withBody(getPayloadSample())));

        worker.handle(messages).blockLast();

        final ArgumentCaptor<Map<String, List<WriteRequest>>> argument = ArgumentCaptor.forClass(Map.class);
        verify(ddb, times(2)).batchWriteItem(argument.capture());
        argument.getAllValues().forEach(map -> {
            if (map.containsKey(orderTable)) {
                Assert.assertEquals(5, map.get(orderTable).size());
            }
            if (map.containsKey(routeTable)) {
                Assert.assertEquals(1, map.get(routeTable).size());
            }
        });
    }

    @Test
    public void shouldSliceBeforeWriteOnDynamoDB() {
        writeBatchSize = 1;
        final OrderTrackingHandler worker = new OrderTrackingHandler(ddb, new TelemetryDTOConverter(), orderTable, routeTable,
            ttlInHours, writeBatchSize);
        final List<Message> messages = Collections.singletonList(new Message().withBody(getPayloadSample()));

        worker.handle(messages).blockLast();

        verify(ddb, times(6)).batchWriteItem(anyMap());
    }

    @Test
    public void shouldReturnMessageDecoratorWithFail() {
        when(ddb.batchWriteItem(anyMap())).thenThrow(FleetTelemetryException.class);
        final OrderTrackingHandler worker = new OrderTrackingHandler(ddb, new TelemetryDTOConverter(), orderTable, routeTable,
            ttlInHours, writeBatchSize);
        Message message = new Message().withBody(getPayloadSample());
        final List<Message> messages = Collections.singletonList(message);

        StepVerifier.create(worker.handle(messages))
                .expectNext(Collections.singletonList(MessageDecorator.withFail(message)))
                .expectComplete()
                .verifyThenAssertThat();
    }


    private String getPayloadSample() {
        try {
            final File file = ResourceUtils.getFile("classpath:data/payload-sample.json");
            return new String(Files.readAllBytes(file.toPath()));
        } catch (final IOException e) {
            throw new RuntimeException("Cannot get a payload sample");
        }
    }

}
