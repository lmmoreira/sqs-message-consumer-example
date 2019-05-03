package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TelemetryDTOConverterTest {

    @Test
    public void shouldConvertFromJsonString() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString(getPayloadSample());
    }

    @Test(expected = FleetTelemetryException.class)
    public void shouldThrowExceptionWhenJsonStringIsEmpty() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenJsonStringIsNull() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString(null);
    }

    @Test(expected = FleetTelemetryException.class)
    public void shouldThrowExceptionWhenNoLatitude() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString("{\n" +
            "  \"longitude\": -45.90458773076534,\n" +
            "  \"routeId\": 10098025,\n" +
            "  \"tenant\": \"BR\",\n" +
            "  \"trackDate\": \"2019-02-28T10:59:14.001-03:00\"\n" +
            "}");
    }

    @Test(expected = FleetTelemetryException.class)
    public void shouldThrowExceptionWhenNoLongitude() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString("{\n" +
            "  \"latitude\": -23.23754159733653,\n" +
            "  \"routeId\": 10098025,\n" +
            "  \"tenant\": \"BR\",\n" +
            "  \"trackDate\": \"2019-02-28T10:59:14.001-03:00\"\n" +
            "}");
    }

    @Test(expected = FleetTelemetryException.class)
    public void shouldThrowExceptionWhenNoTenant() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString("{\n" +
            "  \"latitude\": -23.23754159733653,\n" +
            "  \"longitude\": -45.90458773076534,\n" +
            "  \"routeId\": 10098025,\n" +
            "  \"trackDate\": \"2019-02-28T10:59:14.001-03:00\"\n" +
            "}");
    }

    @Test(expected = FleetTelemetryException.class)
    public void shouldThrowExceptionWhenNoTrackDate() {
        final TelemetryDTOConverter converter = new TelemetryDTOConverter();

        converter.fromString("{\n" +
            "  \"latitude\": -23.23754159733653,\n" +
            "  \"longitude\": -45.90458773076534,\n" +
            "  \"routeId\": 10098025,\n" +
            "  \"tenant\": \"BR\",\n" +
            "}");
    }

    private String getPayloadSample() {
        try {
            final File file = ResourceUtils.getFile("classpath:data/payload-sample.json");
            return new String(Files.readAllBytes(file.toPath()));
        } catch (final IOException e) {
            throw new RuntimeException("Cannot get a payload sample");
        }
    }
}
