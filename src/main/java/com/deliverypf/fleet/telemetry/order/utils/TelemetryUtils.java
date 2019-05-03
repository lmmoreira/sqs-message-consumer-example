package com.deliverypf.fleet.telemetry.order.utils;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.deliverypf.fleet.telemetry.order.dto.TelemetryDTO;

import java.util.HashMap;
import java.util.Map;

public class TelemetryUtils {

    private TelemetryUtils() {
    }

    public static String createIdTenant(final String id, final String tenant) {
        return String.format("%s-%s", id, tenant.toLowerCase());
    }

    public static String createIdTenant(final Long id, final String tenant) {
        return createIdTenant(id.toString(), tenant);
    }

    public static Map<String, AttributeValue> createItemMap(final Long id, final TelemetryDTO telemetryDTO,
                                                            final Long ttlEpochSeconds) {
        final String idTenant = createIdTenant(id, telemetryDTO.getTenant().toString());

        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("id_tenant", new AttributeValue(idTenant));
        item.put("track_date", new AttributeValue(telemetryDTO.getTrackDate()));
        item.put("latitude", new AttributeValue().withN(telemetryDTO.getLatitude().toString()));
        item.put("longitude", new AttributeValue().withN(telemetryDTO.getLongitude().toString()));
        item.put("expiration", new AttributeValue().withN(String.valueOf(ttlEpochSeconds)));
        return item;
    }

}
