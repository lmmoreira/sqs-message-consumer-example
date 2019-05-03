package com.deliverypf.fleet.telemetry.order.repository.s3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrackKeyTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyTenant() {
        new TrackKey(0, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTenant() {
        new TrackKey(0, null);
    }

    @Test
    public void shouldGetKeyName() {
        assertEquals("1-br", new TrackKey(1, "br").getKeyName());
    }

}
