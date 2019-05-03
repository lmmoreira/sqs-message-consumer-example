package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractConverter<T> implements Converter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConverter.class);

    public static final String TENANT_IDENTIFIER = "tenant-identifier";

    protected final ObjectMapper mapper;

    public AbstractConverter() {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    public T fromSQSMessage(final Message sqsMessage) {
        return fromString(sqsMessage.getBody());
    }


    /**
     * Get tenant-identifier attribute, you could make this message attribute through aws cli (See
     * example below)
     *
     * <PRE>
     aws sqs send-message \
     --endpoint-url http://localhost:4576 \
     --queue-url http://localhost:4576/queue/DEV_FLEET_TELEMETRY_ORDER_CONSOLIDATE  \
     --message-body 
        '{"eventType": "ORDER_STATE_CHANGE","parameters":{"ORDER_ID":"1234","CURRENT_ORDER_STATE": "CANCELLED"}}' \
     --message-attributes '{"tenant-identifier":{"DataType": "String","StringValue": "br"}}'
     * </PRE>
     **/
    protected String tenantIdentifierFromMessageAttributes(final Message sqsMessage) {
        for (final Map.Entry<String, MessageAttributeValue> entry : sqsMessage.getMessageAttributes().entrySet()) {
            if (TENANT_IDENTIFIER.equals(entry.getKey())) {
                return entry.getValue().getStringValue();
            }
        }
        LOGGER.warn("Cannot identify the tenant on message: {}, returning an empty tenant value.", sqsMessage);
        return "";
    }

}
