package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.dto.TrackDTO;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.AbstractTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.OrderTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.RouteTrackDynamoDBRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.FleetTelemetryS3Repository;
import com.deliverypf.fleet.telemetry.order.repository.s3.OrderS3RepositoryImpl;
import com.deliverypf.fleet.telemetry.order.repository.s3.RouteS3RepositoryImpl;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DefaultConsolidationService implements ConsolidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConsolidationService.class);

    private final ObjectMapper jacksonObjectMapper;

    private final Map<Type, FleetTelemetryS3Repository> s3Repositories = new HashMap<>();
    private final Map<Type, AbstractTrackDynamoDBRepository> trackRepositories = new HashMap<>();

    @Autowired
    public DefaultConsolidationService(final OrderS3RepositoryImpl orderS3RepositoryImpl,
                                       final RouteS3RepositoryImpl routeS3RepositoryImpl, final OrderTrackDynamoDBRepository orderTrackRepository,
                                       final RouteTrackDynamoDBRepository routeTrackRepository, final ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;

        s3Repositories.put(Type.ORDER, orderS3RepositoryImpl);
        s3Repositories.put(Type.ROUTE, routeS3RepositoryImpl);

        trackRepositories.put(Type.ORDER, orderTrackRepository);
        trackRepositories.put(Type.ROUTE, routeTrackRepository);
    }

    @Override
    public void consolidate(final Type type, final TrackKey trackKey, final List<TrackEntity> tracks) {

        try {

            final List<TrackDTO> tracksToS3 = tracks.stream()
                    .map(d -> new TrackDTO(d.getTrackDate(), d.getLatitude(), d.getLongitude()))
                    .collect(Collectors.toList());

            try {
                LOGGER.debug("Find a previous file at bucket {} ", trackKey);
                final String s3File = s3Repositories.get(type).findByKey(trackKey);

                if (StringUtils.isNoneBlank(s3File)) {
                    final List<TrackDTO> telemetryList = jacksonObjectMapper.readValue(s3File,
                        jacksonObjectMapper.getTypeFactory().constructCollectionType(List.class, TrackDTO.class));
                    tracksToS3.addAll(telemetryList);
                }
            } catch (final FleetTelemetryException e) {
                LOGGER.debug("File does not exist in S3 yet.", e);
            }

            s3Repositories.get(type).save(trackKey, jacksonObjectMapper.writeValueAsString(tracksToS3));

            LOGGER.debug("Wrote at bucket {} ", tracks);

            tracks.forEach(trackRepositories.get(type)::delete);

            LOGGER.debug("Deleted from DynamoDB {} ", tracks);

        } catch (final Exception e) {
            LOGGER.error("Error on writing tracks in S3. {}", e.getMessage());
        }
    }

    @Override
    public void trackConsolidationDateTime(final ConsolidationService.Type consolidationType, final ZonedDateTime startDate,
                                           final ZonedDateTime finalDate) {
        LOGGER.info("Start track consolidation");
        final Map<String, List<TrackEntity>> tracksMap =
            getAsMap(trackRepositories.get(consolidationType).findByTwoTrackDate(startDate, finalDate));
        LOGGER.debug("Found {} keys to consolidate", tracksMap.size());
        tracksMap.forEach((idTenant, list) -> consolidate(consolidationType, new TrackKey(idTenant), list));
        LOGGER.info("End track consolidation");
    }

    @Override
    public void trackConsolidationIdTenant(final ConsolidationService.Type consolidationType, final String idTenant) {
        LOGGER.info("Start track consolidation");
        final Map<String, List<TrackEntity>> tracksMap =
            getAsMap(trackRepositories.get(consolidationType).findByIdTenant(idTenant));
        LOGGER.debug("Found {} keys to consolidate", tracksMap.size());
        tracksMap.forEach((idTenantKey, list) -> consolidate(consolidationType, new TrackKey(idTenantKey), list));
        LOGGER.info("End track consolidation");
    }

    @Override
    public Map<String, List<TrackEntity>> getAsMap(final List<? extends TrackEntity> scan) {
        final Map<String, List<TrackEntity>> scanMap = new HashMap<>();

        scan.forEach(item -> {
            final List<TrackEntity> trackList =
                scanMap.get(item.getIdTenant()) == null ? new ArrayList<>() : scanMap.get(item.getIdTenant());
            trackList.add(item);
            scanMap.put(item.getIdTenant(), trackList);
        });

        return scanMap;
    }

}
