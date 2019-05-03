package com.deliverypf.fleet.telemetry.order.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TrackDTO {

    private String trackDate;
    private Double latitude;
    private Double longitude;

    public TrackDTO() {
    }

    public TrackDTO(final String trackDate, final Double latitude, final Double longitude) {
        this.trackDate = trackDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTrackDate() {
        return trackDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
