package com.deliverypf.fleet.telemetry.order.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTrackDynamoDBRepository<T extends TrackEntity> implements TrackRepository<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTrackDynamoDBRepository.class);

    protected final AmazonDynamoDBAsync ddb;

    protected final DynamoDBMapper mapper;

    protected final String tableName;

    public AbstractTrackDynamoDBRepository(final AmazonDynamoDBAsync ddb,
            @Value("${fleet.telemetry.order.dynamodb.order-table}") final String tableName) {
        this.ddb = ddb;
        this.tableName = tableName;
        this.mapper = new DynamoDBMapper(ddb);
    }

    public abstract List<T> findByIdTenant(final String idTenant);

    public List<T> findByIdTenant(final Class<T> clazz, final String idTenant) {
        final Map<String, AttributeValue> key = Collections.singletonMap(":id_tenant", new AttributeValue(idTenant));

        final DynamoDBQueryExpression<T> expression =
            new DynamoDBQueryExpression<T>().withKeyConditionExpression("id_tenant = :id_tenant")
                    .withExpressionAttributeValues(key);

        return mapper.query(clazz, expression);
    }

    public T findByIdTenantAndTrackDate(final Class<T> clazz, final String idTenant, final String trackDate) {
        return mapper.load(clazz, idTenant, trackDate);
    }

    public List<T> findByTwoTrackDate(final Class<T> clazz, final ZonedDateTime initialTrackDate,
                                      final ZonedDateTime finalTrackDate) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

        final Map<String, AttributeValue> keys = new HashMap<>();
        keys.put(":initialTrackDate", new AttributeValue().withS(dateFormatter.format(initialTrackDate)));
        keys.put(":finalTrackDate", new AttributeValue().withS(dateFormatter.format(finalTrackDate)));

        final DynamoDBScanExpression scan = new DynamoDBScanExpression();
        scan.withFilterExpression("track_date between :initialTrackDate and :finalTrackDate");
        scan.withExpressionAttributeValues(keys);
        return mapper.scan(clazz, scan);

    }

    public void delete(final T track) {
        mapper.delete(track);
    }

    public void batchDelete(final Collection<T> tracks) {
        LOGGER.debug("Deleting {} tracks in batch on {}.", tracks.size(), tableName);
        mapper.batchDelete(tracks);
    }

}
