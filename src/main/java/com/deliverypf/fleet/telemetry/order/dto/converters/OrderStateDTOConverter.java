package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;
import com.deliverypf.fleet.telemetry.order.dto.order.OrderStateDTO;
import com.deliverypf.fleet.telemetry.order.exceptions.FleetTelemetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderStateDTOConverter extends AbstractConverter<OrderStateDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStateDTOConverter.class);


    public OrderStateDTOConverter() {
        super();
    }

    public OrderStateDTO fromString(final String telemetryLiteral) {
        try {
            return mapper.readValue(telemetryLiteral, OrderStateDTO.class);
        } catch (final IOException e) {
            final String msg = "Cannot convert payload to OrderStateDTO, error message: " + e.getMessage();
            LOGGER.error(msg);
            throw new FleetTelemetryException(msg);
        }
    }

    @Override
    public OrderStateDTO fromSQSMessage(final Message sqsMessage) {
        final OrderStateDTO dto = super.fromSQSMessage(sqsMessage);
        return dto.tenant(tenantIdentifierFromMessageAttributes(sqsMessage));
    }


    public List<OrderStateDTO> fromSQSMessages(final List<Message> messages) {
        return messages.stream()
                .map(this::fromSQSMessage)
                .filter(OrderStateDTO::isNeedConsolidate)
                .collect(Collectors.toList());
    }

}
