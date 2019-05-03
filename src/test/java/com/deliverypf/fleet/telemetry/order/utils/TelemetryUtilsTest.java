package com.deliverypf.fleet.telemetry.order.utils;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.deliverypf.fleet.telemetry.order.dto.TelemetryDTO;
import com.deliverypf.fleet.telemetry.order.dto.Tenant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TelemetryUtilsTest {

    @Value("${fleet.telemetry.order.dynamodb.ttl-hours}")
    private int ttlInHours;

    @Test
    public void shouldCreateIdItem() {
        assertThat(TelemetryUtils.createIdTenant(1L, "br"), equalTo("1-br"));
    }

    @Test
    public void shouldCreateIdItemLowercase() {
        assertThat(TelemetryUtils.createIdTenant(65L, "MX"), equalTo("65-mx"));
    }

    @Test
    public void shouldCreateItemMap() {
        final String trackDate = "2018-01-01";
        final double latitude = 1D;
        final double longitude = 2D;
        final long id = 1L;
        final Tenant tenant = Tenant.BR;
        final TelemetryDTO telemetry = new TelemetryDTO(latitude, longitude, trackDate, Collections.emptySet(), 666L, tenant);
        final Long ttl = Instant.now().plusSeconds(ttlInHours * 3600).getEpochSecond();
        final Map<String, AttributeValue> itemMap = TelemetryUtils.createItemMap(id, telemetry, ttl);

        assertThat(itemMap.size(), equalTo(5));
        assertThat(itemMap.get("id_tenant").getS(), equalTo(TelemetryUtils.createIdTenant(id, tenant.toString())));
        assertThat(itemMap.get("track_date").getS(), equalTo(trackDate));
        assertThat(itemMap.get("latitude").getN(), equalTo(Double.toString(latitude)));
        assertThat(itemMap.get("longitude").getN(), equalTo(Double.toString(longitude)));
        assertThat(itemMap.get("expiration").getN(), equalTo(Long.toString(ttl)));
    }

}
