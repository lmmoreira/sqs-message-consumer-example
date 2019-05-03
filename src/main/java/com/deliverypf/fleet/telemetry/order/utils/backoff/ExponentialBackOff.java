package com.deliverypf.fleet.telemetry.order.utils.backoff;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import com.deliverypf.fleet.telemetry.order.exceptions.ThroughputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public final class ExponentialBackOff {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExponentialBackOff.class);

    private static final int[] FIBONACCI = new int[] {1, 1, 2, 3, 5};
    private static final List<Class<? extends Exception>> EXPECTED_COMMUNICATION_ERRORS =
        Collections.singletonList(ProvisionedThroughputExceededException.class);

    private ExponentialBackOff() {

    }

    public static <T> T execute(final ExponentialBackOffFunction<T> fn) {
        for (int attempt = 0; attempt < FIBONACCI.length; attempt++) {
            try {
                return fn.execute();
            } catch (final Exception e) {
                handleFailure(attempt, e);
            }
        }
        throw new ThroughputException("Throughput detected, slept 12 seconds. Messages are going back to the queue.");
    }

    private static void handleFailure(final int attempt, final Exception e) {
        if (!EXPECTED_COMMUNICATION_ERRORS.contains(e.getClass())) {
            throw new FleetTelemetryException(e);
        }
        doWait(attempt);
    }

    private static void doWait(final int attempt) {
        try {
            LOGGER.debug("Throughput detected, waiting {}ms", FIBONACCI[attempt] * 1000);
            Thread.sleep(FIBONACCI[attempt] * 1000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
