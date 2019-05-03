package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;
import com.deliverypf.fleet.telemetry.order.dto.route.RouteStateDTO;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RouteStateDTOConverter extends AbstractConverter<RouteStateDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteStateDTOConverter.class);

    public RouteStateDTOConverter() {
        super();
    }

    public RouteStateDTO fromString(String telemetryLiteral) {
        try {
            return mapper.readValue(telemetryLiteral, RouteStateDTO.class);
        } catch (IOException e) {
            String msg = "Cannot convert payload to RouteStateDTO, error message: " + e.getMessage();
            LOGGER.error(msg);
            throw new FleetTelemetryException(msg);
        }
    }

    @Override
    public RouteStateDTO fromSQSMessage(final Message sqsMessage) {
        final RouteStateDTO dto = super.fromSQSMessage(sqsMessage);
        return dto.tenant(tenantIdentifierFromMessageAttributes(sqsMessage));
    }

    public List<RouteStateDTO> fromSQSMessages(List<Message> messages) {
        return messages.stream().map(message -> fromSQSMessage(message)).collect(Collectors.toList());
    }
}
