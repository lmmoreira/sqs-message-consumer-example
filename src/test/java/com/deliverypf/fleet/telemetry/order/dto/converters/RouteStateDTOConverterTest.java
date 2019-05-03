package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.deliverypf.fleet.telemetry.order.dto.route.RouteStateDTO;
import com.google.common.collect.Lists;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class RouteStateDTOConverterTest {

    public static final String BODY_COMPLETED =
        "{\"eventType\": \"ROUTE_COMPLETED\", \"parameters\":{ \"ROUTE_ID\":\"123456789\"}}";

    @Test
    public void fromSQSMessageCanceledTest() {
        final Message message = buildMessageCancelled();
        final RouteStateDTOConverter converter = new RouteStateDTOConverter();

        final List<RouteStateDTO> routes = converter.fromSQSMessages(Lists.newArrayList(message));

        Assert.assertEquals(1, routes.size());
    }


    private Message buildMessageCancelled() {
        final Map<String, MessageAttributeValue> tenantMap = buildMessageAttribute();
        return new Message().withBody(BODY_COMPLETED).withMessageAttributes(tenantMap);
    }

    private Map<String, MessageAttributeValue> buildMessageAttribute() {
        final MessageAttributeValue tenant = new MessageAttributeValue().withDataType("String").withStringValue("br");
        return Maps.newHashMap(AbstractConverter.TENANT_IDENTIFIER, tenant);
    }
}
