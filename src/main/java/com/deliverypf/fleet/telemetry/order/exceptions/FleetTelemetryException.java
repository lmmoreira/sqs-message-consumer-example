package com.deliverypf.fleet.telemetry.order.exceptions;

public class FleetTelemetryException extends RuntimeException {

    public FleetTelemetryException(final String message) {
        super(message);
    }

    public FleetTelemetryException(final Exception e) {
        super(e);
    }
}
