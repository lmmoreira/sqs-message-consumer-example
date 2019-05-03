package com.deliverypf.fleet.telemetry.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import javax.validation.constraints.NotNull;

public class TelemetryDTO {

    @NotNull
    private final Long routeId;

    @NotNull
    private final Double latitude;

    @NotNull
    private final Double longitude;

    @NotNull
    private final String trackDate;

    private final Set<OrderIdDTO> orders;

    @NotNull
    private final Tenant tenant;

    @JsonCreator
    public TelemetryDTO( //
            @JsonProperty(value = "latitude", required = true) final Double latitude,
            @JsonProperty(value = "longitude", required = true) final Double longitude,
            @JsonProperty(value = "trackDate", required = true) final String trackDate,
            @JsonProperty("orders") final Set<OrderIdDTO> orders, //
            @JsonProperty("routeId") final Long routeId,
            @JsonProperty(value = "tenant", required = true) final Tenant tenant) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.trackDate = trackDate;
        this.orders = MoreObjects.firstNonNull(orders, Collections.emptySet());
        this.routeId = routeId;
        this.tenant = tenant;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getTrackDate() {
        return trackDate;
    }

    public Set<OrderIdDTO> getOrders() {
        return orders;
    }

    public Long getRouteId() {
        return routeId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    @Override public String toString() {
        return new StringJoiner(", ", TelemetryDTO.class.getSimpleName() + "[", "]")
                .add("routeId=" + routeId)
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .add("trackDate='" + trackDate + "'")
                .add("orders=" + orders)
                .add("tenant=" + tenant)
                .toString();
    }
}

