package com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity;

public interface TrackEntity {

    String getIdTenant();

    String getTrackDate();

    Double getLatitude();

    Double getLongitude();

}
