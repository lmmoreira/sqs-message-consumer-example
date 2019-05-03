package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.dto.TrackDTO;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;

public interface FleetWebService {

    Flux<TrackDTO> getTracks(Long id, String tenant);

    void batchDeleteTracks(ZonedDateTime startDate, ZonedDateTime finalDate);

    void trackConsolidation(ConsolidationService.Type consolidationType, ZonedDateTime startDate,
                            ZonedDateTime finalDate);

    void trackConsolidationIdTenant(ConsolidationService.Type consolidationType, String idTenant);

}
