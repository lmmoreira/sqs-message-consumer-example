package com.deliverypf.fleet.telemetry.order.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.RouteTrackEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Qualifier("routeRepository")
public class RouteTrackDynamoDBRepository extends AbstractTrackDynamoDBRepository<RouteTrackEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteTrackDynamoDBRepository.class);

    public RouteTrackDynamoDBRepository(final AmazonDynamoDBAsync ddb,
            @Value("${fleet.telemetry.order.dynamodb.route-table}") final String tableName) {
        super(ddb, tableName);
    }

    @Override
    public List<RouteTrackEntity> findByIdTenant(final String idTenant) {
        return findByIdTenant(RouteTrackEntity.class, idTenant);
    }

    @Override
    public RouteTrackEntity findByIdTenantAndTrackDate(final String idTenant, final String trackDate) {
        return findByIdTenantAndTrackDate(RouteTrackEntity.class, idTenant, trackDate);
    }

    @Override
    public List<RouteTrackEntity> findByTwoTrackDate(final ZonedDateTime initialTrackDate,
                                                     final ZonedDateTime finalTrackDate) {
        return findByTwoTrackDate(RouteTrackEntity.class, initialTrackDate, finalTrackDate);
    }
}
