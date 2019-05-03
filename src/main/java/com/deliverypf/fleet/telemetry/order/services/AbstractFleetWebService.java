package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.dto.TrackDTO;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.TrackRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.S3Repository;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class AbstractFleetWebService<T extends TrackEntity> implements FleetWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFleetWebService.class);

    private final TrackRepository<T> trackRepository;
    private final S3Repository<TrackKey, String> s3Repository;
    private final ConsolidationService defaultConsolidationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AbstractFleetWebService(final TrackRepository<T> trackRepository,
            final S3Repository<TrackKey, String> s3Repository, final ConsolidationService defaultConsolidationService) {
        this.trackRepository = trackRepository;
        this.s3Repository = s3Repository;
        this.defaultConsolidationService = defaultConsolidationService;
    }

    @Override
    public Flux<TrackDTO> getTracks(final Long id, final String tenant) {
        return Flux.from(findOnDynamoDB(id, tenant)).switchIfEmpty(Flux.defer(() -> findOnS3(id, tenant)));
    }

    @Override
    public void batchDeleteTracks(final ZonedDateTime startDate, final ZonedDateTime finalDate) {
        LOGGER.info("Start batch delete");
        final List<T> trackEntities = trackRepository.findByTwoTrackDate(startDate, finalDate);
        LOGGER.debug("Found {} registers to delete", trackEntities.size());
        trackEntities.forEach(item -> {
            trackRepository.delete(item);
            LOGGER.debug("Deleting item {}", item);
        });
        LOGGER.info("End batch delete");
    }

    @Override
    public void trackConsolidation(final ConsolidationService.Type consolidationType, final ZonedDateTime startDate,
                                   final ZonedDateTime finalDate) {
        defaultConsolidationService.trackConsolidationDateTime(consolidationType, startDate, finalDate);
    }

    @Override
    public void trackConsolidationIdTenant(final ConsolidationService.Type consolidationType, final String idTenant) {
        defaultConsolidationService.trackConsolidationIdTenant(consolidationType, idTenant);
    }

    private Flux<TrackDTO> findOnDynamoDB(final Long id, final String tenant) {
        final CompletableFuture<List<TrackDTO>> completableFuture =
            CompletableFuture.supplyAsync(() -> trackRepository.findByIdTenant(new TrackKey(id, tenant).getKeyName())
                    .stream()
                    .map(t -> new TrackDTO(t.getTrackDate(), t.getLatitude(), t.getLongitude()))
                    .collect(Collectors.toList()));
        return Mono.fromFuture(completableFuture).flatMapMany(Flux::fromIterable);
    }

    private Flux<TrackDTO> findOnS3(final Long id, final String tenant) {
        final CompletableFuture<List<TrackDTO>> completableFuture = CompletableFuture.<List<TrackDTO>>supplyAsync(() -> {
            String tracksLiteral = s3Repository.findByKey(new TrackKey(id, tenant));
            return convertTracksToJSon(tracksLiteral);
        }).exceptionally(t -> {
            LOGGER.warn("Cannot find any file on S3 for tenant: {} and id: {}, will return an empty list", tenant, id);
            return Collections.emptyList();
        });
        return Flux.from(Mono.fromFuture(completableFuture).flatMapMany(Flux::fromIterable));
    }

    private List<TrackDTO> convertTracksToJSon(final String tracksLiteral) {
        try {
            final TypeReference<List<TrackDTO>> tRef = new TypeReference<>() {
            };
            return objectMapper.readValue(tracksLiteral, tRef);
        } catch (final IOException e) {
            LOGGER.error("Cannot convert Tracks from S3");
            throw new FleetTelemetryException(e);
        }
    }
}
