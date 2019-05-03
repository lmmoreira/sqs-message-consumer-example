package com.deliverypf.fleet.telemetry.order.dto.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RouteParameters {
    private String routeId;

    @JsonCreator
    public RouteParameters(@JsonProperty("ROUTE_ID") final String routeId) {
        this.routeId = routeId;
    }

    public String getRouteId() {
        return routeId;
    }

    public Long getRouteIdAsLong() {
        return Long.valueOf(routeId);
    }

    public void setRouteId(final String routeId) {
        this.routeId = routeId;
    }
}
