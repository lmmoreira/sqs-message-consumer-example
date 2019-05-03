package com.deliverypf.fleet.telemetry.order.services;

import com.deliverypf.fleet.telemetry.order.repository.dynamodb.TrackRepository;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.OrderTrackEntity;
import com.deliverypf.fleet.telemetry.order.repository.s3.S3Repository;
import com.deliverypf.fleet.telemetry.order.repository.s3.TrackKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("orderFleetWebService")
public class OrderFleetWebServiceImpl extends AbstractFleetWebService<OrderTrackEntity> {

    public OrderFleetWebServiceImpl(
            @Qualifier("orderRepository") final TrackRepository<OrderTrackEntity> trackRepository,
            @Qualifier("orderS3Repository") final S3Repository<TrackKey, String> s3Repository,
            @Qualifier("defaultConsolidationService") final ConsolidationService defaultConsolidationService) {
        super(trackRepository, s3Repository, defaultConsolidationService);
    }
}
