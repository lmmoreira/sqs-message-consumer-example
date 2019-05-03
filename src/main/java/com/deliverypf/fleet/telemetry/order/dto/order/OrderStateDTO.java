package com.deliverypf.fleet.telemetry.order.dto.order;

import static com.deliverypf.fleet.telemetry.order.utils.TelemetryUtils.createIdTenant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/**
 * <pre>
 {
     "eventType": "ORDER_STATE_CHANGE",
     "parameters":{
         "ORDER_ID":"1234",
         "CURRENT_ORDER_STATE": "CANCELLED"
     }
 }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderStateDTO implements BaseTenantEntity {

    private final OrderStateParameters parameters;

    private String tenant;

    @JsonCreator
    public OrderStateDTO(@JsonProperty("parameters") final OrderStateParameters parameters) {
        this.parameters = parameters;
    }

    public String getIdTenant() {
        return createIdTenant(getParameters().getOrderId(), getTenant());
    }

    public Long getIdAsLong() {
        return parameters.getOrderIdAsLong();
    }

    public String getTenant() {
        return tenant;
    }

    public OrderStateDTO tenant(final String tenant) {
        this.tenant = tenant;
        return this;
    }

    public OrderStateParameters getParameters() {
        return parameters;
    }

    public Boolean isCompleted() {
        if (Objects.isNull(parameters)) {
            return false;
        }
        return OrderStateType.COMPLETED.equals(parameters.getCurrentOrderState());
    }

    public Boolean isNeedConsolidate() {
        return isCompleted() || isCancelled();
    }

    public Boolean isCancelled() {
        if (Objects.isNull(parameters)) {
            return false;
        }
        return OrderStateType.CANCELLED.equals(parameters.getCurrentOrderState());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
