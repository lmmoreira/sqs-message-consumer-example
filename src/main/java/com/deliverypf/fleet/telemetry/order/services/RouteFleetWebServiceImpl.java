package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.repository.dynamodb.TrackRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.RouteTrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.S3Repository;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("routeFleetWebService")
public class RouteFleetWebServiceImpl extends AbstractFleetWebService<RouteTrackEntity> {

    public RouteFleetWebServiceImpl(
            @Qualifier("routeRepository") final TrackRepository<RouteTrackEntity> trackRepository,
            @Qualifier("routeS3Repository") final S3Repository<TrackKey, String> s3Repository,
            @Qualifier("defaultConsolidationService") final ConsolidationService defaultConsolidationService) {
        super(trackRepository, s3Repository, defaultConsolidationService);
    }

}
