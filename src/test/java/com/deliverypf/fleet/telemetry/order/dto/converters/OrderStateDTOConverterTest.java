package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.deliverypf.fleet.telemetry.order.dto.order.OrderStateDTO;
import com.google.common.collect.Lists;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class OrderStateDTOConverterTest {

    public static final String BODY_CANCELLED =
        "{\"eventType\": \"ORDER_STATE_CHANGE\",\"parameters\":{\"ORDER_ID\":\"1234\",\"CURRENT_ORDER_STATE\": \"CANCELLED\"}}";

    public static final String BODY_COMPLETED =
        "{\"eventType\": \"ORDER_STATE_CHANGE\",\"parameters\":{\"ORDER_ID\":\"1234\",\"CURRENT_ORDER_STATE\": \"COMPLETED\"}}";

    public static final String BODY_NOT_IDENTIFIED =
        "{\"eventType\": \"ORDER_STATE_CHANGE\",\"parameters\":{\"ORDER_ID\":\"1234\",\"CURRENT_ORDER_STATE\": \"NOT_IDENTIFIED\"}}";

    @Test
    public void fromSQSMessageCanceledTest() {
        final Message message = buildMessageCancelled();
        final OrderStateDTOConverter converter = new OrderStateDTOConverter();

        final List<OrderStateDTO> orders = converter.fromSQSMessages(Lists.newArrayList(message));

        Assert.assertEquals(1, orders.size());
        Assert.assertEquals("br", orders.get(0).getTenant());
        Assert.assertEquals("1234-br", orders.get(0).getIdTenant());
        Assert.assertTrue(orders.get(0).isCancelled());
    }

    @Test
    public void fromSQSMessageCompletedTest() {
        final Message message = buildMessageCompleted();
        final OrderStateDTOConverter converter = new OrderStateDTOConverter();

        final List<OrderStateDTO> orders = converter.fromSQSMessages(Lists.newArrayList(message));

        Assert.assertEquals(1, orders.size());
        Assert.assertEquals("br", orders.get(0).getTenant());
        Assert.assertEquals("1234-br", orders.get(0).getIdTenant());
        Assert.assertTrue(orders.get(0).isCompleted());
    }

    @Test
    public void fromSQSMessageFilterNotNeedCompletedTest() {
        final OrderStateDTOConverter converter = new OrderStateDTOConverter();

        final List<OrderStateDTO> orders = converter.fromSQSMessages(
            Lists.newArrayList(buildMessageCompleted(), buildMessageCancelled(), buildMessageNotIdentified()));

        Assert.assertEquals(2, orders.size());
    }


    private Message buildMessageNotIdentified() {
        final Map<String, MessageAttributeValue> tenantMap = buildMessageAttribute();
        return new Message().withBody(BODY_NOT_IDENTIFIED).withMessageAttributes(tenantMap);
    }


    private Message buildMessageCompleted() {
        final Map<String, MessageAttributeValue> tenantMap = buildMessageAttribute();
        return new Message().withBody(BODY_COMPLETED).withMessageAttributes(tenantMap);
    }


    private Message buildMessageCancelled() {
        final Map<String, MessageAttributeValue> tenantMap = buildMessageAttribute();
        return new Message().withBody(BODY_CANCELLED).withMessageAttributes(tenantMap);
    }

    private Map<String, MessageAttributeValue> buildMessageAttribute() {
        final MessageAttributeValue tenant = new MessageAttributeValue().withDataType("String").withStringValue("br");
        return Maps.newHashMap(AbstractConverter.TENANT_IDENTIFIER, tenant);
    }
}
