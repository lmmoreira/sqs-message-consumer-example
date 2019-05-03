package com.deliverypf.fleet.telemetry.order.repository.dynamodb;

import com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity.TrackEntity;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface TrackRepository<T extends TrackEntity> {

    List<T> findByIdTenant(final String idTenant);

    T findByIdTenantAndTrackDate(final String idTenant, final String trackDate);

    List<T> findByTwoTrackDate(final ZonedDateTime initialTrackDate, final ZonedDateTime finalTrackDate);

    void delete(T track);

    void batchDelete(final Collection<T> tracks);

}
