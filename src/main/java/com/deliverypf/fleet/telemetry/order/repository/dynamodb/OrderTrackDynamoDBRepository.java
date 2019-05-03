package com.deliverypf.fleet.telemetry.order.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.OrderTrackEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
@Qualifier("orderRepository")
public class OrderTrackDynamoDBRepository extends AbstractTrackDynamoDBRepository<OrderTrackEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderTrackDynamoDBRepository.class);

    public OrderTrackDynamoDBRepository(final AmazonDynamoDBAsync ddb,
            @Value("${fleet.telemetry.order.dynamodb.order-table}") final String tableName) {
        super(ddb, tableName);
    }

    @Override
    public List<OrderTrackEntity> findByIdTenant(final String idTenant) {
        return findByIdTenant(OrderTrackEntity.class, idTenant);
    }

    @Override
    public OrderTrackEntity findByIdTenantAndTrackDate(final String idTenant, final String trackDate) {
        return findByIdTenantAndTrackDate(OrderTrackEntity.class, idTenant, trackDate);
    }

    @Override
    public List<OrderTrackEntity> findByTwoTrackDate(final ZonedDateTime initialTrackDate,
                                                     final ZonedDateTime finalTrackDate) {
        return findByTwoTrackDate(OrderTrackEntity.class, initialTrackDate, finalTrackDate);
    }

}
