package com.deliverypf.fleet.telemetry.order.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class OrderIdDTO {

    private final Long orderId;

    private final String externalId;

    private final String uuid;

    @JsonCreator
    public OrderIdDTO(@JsonProperty("orderId") final Long orderId, @JsonProperty("externalId") final String externalId,
            @JsonProperty("uuid") final String uuid) {
        this.orderId = orderId;
        this.externalId = externalId;
        this.uuid = uuid;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
