package com.deliverypf.fleet.telemetry.order.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum OrderStateType {
    CANCELLED, COMPLETED
}
