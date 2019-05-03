package com.deliverypf.fleet.telemetry.order.repository.dynamodb.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@DynamoDBTable(tableName = "fleet_order_tracking")
public class OrderTrackEntity implements TrackEntity {

    private String idTenant;

    private String trackDate;

    private Double latitude;

    private Double longitude;

    /**
     * Constructor mandatory by AWS-SDK for entity creation.
     * 
     * @See DynamoDBMapperTableModel.unconvert
     */
    public OrderTrackEntity() {
    }

    public OrderTrackEntity(final String idTenant, final String trackDate, final Double latitude,
            final Double longitude) {
        this.idTenant = idTenant;
        this.trackDate = trackDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @DynamoDBHashKey(attributeName = "id_tenant")
    public String getIdTenant() {
        return idTenant;
    }

    @DynamoDBRangeKey(attributeName = "track_date")
    public String getTrackDate() {
        return trackDate;
    }

    @DynamoDBAttribute(attributeName = "latitude")
    public Double getLatitude() {
        return latitude;
    }

    @DynamoDBAttribute(attributeName = "longitude")
    public Double getLongitude() {
        return longitude;
    }

    public OrderTrackEntity idTenant(final String idTenant) {
        this.idTenant = idTenant;
        return this;
    }

    public OrderTrackEntity trackDate(final String trackDate) {
        this.trackDate = trackDate;
        return this;
    }

    public OrderTrackEntity latitude(final Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public OrderTrackEntity longitude(final Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setIdTenant(final String idTenant) {
        this.idTenant = idTenant;
    }

    public void setTrackDate(final String trackDate) {
        this.trackDate = trackDate;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
