package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public interface ConsolidationService {

    enum Type {
        ORDER, ROUTE
    }

    void consolidate(Type type, TrackKey trackKey, List<TrackEntity> tracks);

    void trackConsolidationDateTime(ConsolidationService.Type consolidationType, ZonedDateTime startDate,
                                    ZonedDateTime finalDate);

    void trackConsolidationIdTenant(ConsolidationService.Type consolidationType, String idTenant);

    Map<String, List<TrackEntity>> getAsMap(List<? extends TrackEntity> scan);

}
