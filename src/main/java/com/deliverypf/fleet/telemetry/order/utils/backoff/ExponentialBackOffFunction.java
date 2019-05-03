package com.deliverypf.fleet.telemetry.order.utils.backoff;

@FunctionalInterface
public interface ExponentialBackOffFunction<T> {
    T execute();
}
