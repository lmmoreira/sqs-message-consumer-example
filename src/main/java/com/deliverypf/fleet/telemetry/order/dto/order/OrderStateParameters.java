package com.deliverypf.fleet.telemetry.order.dto.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderStateParameters {

    private final String orderId;
    private final OrderStateType currentOrderState;

    @JsonCreator
    public OrderStateParameters(@JsonProperty("ORDER_ID") final String orderId,
            @JsonProperty("CURRENT_ORDER_STATE") final OrderStateType currentOrderState) {
        this.orderId = orderId;
        this.currentOrderState = currentOrderState;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getOrderIdAsLong() {
        return Long.valueOf(orderId);
    }

    public OrderStateType getCurrentOrderState() {
        return currentOrderState;
    }

}
