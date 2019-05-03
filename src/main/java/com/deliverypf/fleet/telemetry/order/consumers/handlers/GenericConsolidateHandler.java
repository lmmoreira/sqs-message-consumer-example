package com.deliverypf.fleet.telemetry.order.consumers.handlers;

import com.amazonaws.services.sqs.model.Message;
import com.deliverypf.fleet.telemetry.order.dto.TrackDTO;
import com.deliverypf.fleet.telemetry.order.dto.converters.Converter;
import com.deliverypf.fleet.telemetry.order.dto.order.BaseTenantEntity;
import com.deliverypf.fleet.telemetry.order.exceptions.WorkerException;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.TrackRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.FleetTelemetryS3Repository;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import com.deliverypf.fleet.telemetry.order.utils.backoff.ExponentialBackOff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lorem.logistics.event.amazon.sqs.handler.AbstractMultipleAmazonSQSMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GenericConsolidateHandler<T extends TrackEntity> extends AbstractMultipleAmazonSQSMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericConsolidateHandler.class);

    private final Converter<? extends BaseTenantEntity> converter;
    private final TrackRepository<T> ddbRepository;
    private final FleetTelemetryS3Repository s3Repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenericConsolidateHandler(final Converter<? extends BaseTenantEntity> converter,
            final TrackRepository<T> ddbRepository, final FleetTelemetryS3Repository s3Repository) {
        LOGGER.debug("Creating GenericConsolidateHandler by converter: {}, ddb repository: {}, s3 repository: {}",
            converter,
            ddbRepository,
            s3Repository);
        this.converter = converter;
        this.ddbRepository = ddbRepository;
        this.s3Repository = s3Repository;
    }

    @Override
    protected Mono<Void> handleMessages(final List<Message> messages) {
        LOGGER.debug("{} message was received to perform the consolidation.", messages.size());
        return Mono.fromRunnable(() -> ExponentialBackOff.execute(() -> doProcess(messages)));
    }

    private List<Message> doProcess(final List<Message> messages) {
        LOGGER.debug("Start processing {} messages on consolidation.", messages.size());
        for (final BaseTenantEntity dto : converter.fromSQSMessages(messages)) {
            final List<T> trackEntities = ddbRepository.findByIdTenant(dto.getIdTenant());
            LOGGER.debug("{} tracks found of key {} to be consolidate.", trackEntities.size(), dto.getIdTenant());
            if (!trackEntities.isEmpty()) {
                final TrackKey trackKey = new TrackKey(dto.getIdAsLong(), dto.getTenant());
                sendToS3(trackKey, trackEntities);
                ddbRepository.batchDelete(trackEntities);
            }
        }
        return messages;
    }

    private void sendToS3(final TrackKey trackKey,
                          final Collection<? extends TrackEntity> orders) throws WorkerException {
        try {
            final List<TrackDTO> tracks = orders.stream()
                    .map(d -> new TrackDTO(d.getTrackDate(), d.getLatitude(), d.getLongitude()))
                    .collect(Collectors.toList());
            final String json = objectMapper.writeValueAsString(tracks);
            LOGGER.debug("Sending to S3 {}", json);
            s3Repository.save(trackKey, json);
        } catch (final JsonProcessingException e) {
            throw new WorkerException(e);
        }
    }
}
