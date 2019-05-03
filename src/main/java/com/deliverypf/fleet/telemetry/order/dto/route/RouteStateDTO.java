package com.deliverypf.fleet.telemetry.order.dto.route;

import com.deliverypf.fleet.telemetry.order.dto.order.BaseTenantEntity;
import com.deliverypf.fleet.telemetry.order.utils.TelemetryUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <pre>
    {
        "eventType": "ROUTE_COMPLETED",
        "parameters":{
            "ROUTE_ID":"123456789"
        }
    }
 * </pre>
 */
public class RouteStateDTO implements BaseTenantEntity {
    private String eventType;
    private RouteParameters parameters;
    private String tenant;


    @JsonCreator
    public RouteStateDTO(@JsonProperty("parameters") final RouteParameters parameters) {
        this.parameters = parameters;
    }

    public String getTenant() {
        return tenant;
    }

    public RouteStateDTO tenant(final String tenant) {
        this.tenant = tenant;
        return this;
    }

    public Long getIdAsLong() {
        return parameters.getRouteIdAsLong();
    }

    public String getIdTenant() {
        return TelemetryUtils.createIdTenant(getParameters().getRouteId(), getTenant());
    }

    public RouteParameters getParameters() {
        return parameters;
    }

    public void setParameters(final RouteParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

