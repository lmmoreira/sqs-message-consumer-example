package com.deliverypf.fleet.telemetry.order.repository.s3;

import com.deliverypf.fleet.telemetry.order.utils.TelemetryUtils;
import org.springframework.util.Assert;

public class TrackKey {

    private final long id;
    private final String tenant;

    public TrackKey(final long id, final String tenant) {
        Assert.hasText(tenant, "tenant should not be null or empty");
        this.id = id;
        this.tenant = tenant;
    }

    public TrackKey(final String idTenant) {
        Assert.hasText(idTenant, "idTenant should not be null or empty");
        this.id = Long.valueOf(idTenant.split("-")[0]);
        this.tenant = idTenant.split("-")[1];
    }

    public long getId() {
        return id;
    }

    public String getTenant() {
        return tenant;
    }

    public String getKeyName() {
        return TelemetryUtils.createIdTenant(id, tenant);
    }

    @Override
    public String toString() {
        return "TrackKey{" + "id='" + id + '\'' + ", tenant='" + tenant + '\'' + '}';
    }

}
