package com.deliverypf.fleet.telemetry.order.dto.order;

import org.junit.Assert;
import org.junit.Test;

public class OrderStateDTOTest {

    @Test
    public void isCanceledTest() {
        final OrderStateDTO orderState =
            new OrderStateDTO(new OrderStateParameters("1", OrderStateType.CANCELLED));

        Assert.assertTrue(orderState.isCancelled());
    }

    @Test
    public void isCompletedTest() {
        final OrderStateDTO orderState =
            new OrderStateDTO(new OrderStateParameters("1", OrderStateType.COMPLETED));

        Assert.assertTrue(orderState.isCompleted());
    }

    @Test
    public void isNeedConsolidateTest() {
        final OrderStateDTO completed =
            new OrderStateDTO(new OrderStateParameters("1", OrderStateType.COMPLETED));
        Assert.assertTrue(completed.isNeedConsolidate());


        final OrderStateDTO cancelled =
            new OrderStateDTO(new OrderStateParameters("1", OrderStateType.CANCELLED));
        Assert.assertTrue(cancelled.isNeedConsolidate());
    }
}
