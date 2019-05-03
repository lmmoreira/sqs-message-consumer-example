package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;
import com.deliverypf.fleet.telemetry.order.dto.TelemetryDTO;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TelemetryDTOConverter extends AbstractConverter<TelemetryDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryDTOConverter.class);

    public TelemetryDTOConverter() {
        super();
    }

    public TelemetryDTO fromString(final String telemetryLiteral) {
        try {
            return mapper.readValue(telemetryLiteral, TelemetryDTO.class);
        } catch (final IOException e) {
            final String msg = "Cannot convert payload to TelemetryDTO, error message: " + e.getMessage();
            LOGGER.error(msg);
            throw new FleetTelemetryException(msg);
        }
    }

    @Override
    public TelemetryDTO fromSQSMessage(final Message sqsMessage) {
        return fromString(sqsMessage.getBody());
    }

    public List<TelemetryDTO> fromSQSMessages(final List<Message> messages) {
        return messages.stream().map(message -> fromSQSMessage(message)).collect(Collectors.toList());
    }

}
