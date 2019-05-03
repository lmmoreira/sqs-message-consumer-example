package com.deliverypf.fleet.telemetry.order.utils.backoff;

import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExponentialBackOffTest {

    @Test
    public void shouldThrowExceptionWhenNotExpectedAndExecuteOnce() {
        final ExponentialBackOffFunction<String> function = mock(ExponentialBackOffFunction.class);
        when(function.execute()).thenThrow(IllegalArgumentException.class);
        try {
            ExponentialBackOff.execute(function);
            fail("Should fail");
        } catch (final FleetTelemetryException ex) {
            verify(function, times(1)).execute();
        }
    }

    @Test
    public void shouldThrowExceptionAfterExceedLimitOfTime() {
        final ExponentialBackOffFunction<String> function = mock(ExponentialBackOffFunction.class);
        when(function.execute()).thenThrow(ProvisionedThroughputExceededException.class);
        try {
            ExponentialBackOff.execute(function);
            fail("Should fail");
        } catch (final FleetTelemetryException ex) {
            verify(function, times(5)).execute();
        }
    }

    @Test
    public void shouldShouldExecute() {
        ExponentialBackOff.execute(() -> true);
    }
}
