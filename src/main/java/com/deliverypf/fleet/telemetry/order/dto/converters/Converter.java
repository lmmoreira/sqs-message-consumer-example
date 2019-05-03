package com.deliverypf.fleet.telemetry.order.dto.converters;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public interface Converter<T> {
    public T fromString(String json);

    public T fromSQSMessage(Message sqsMessage);

    public List<T> fromSQSMessages(List<Message> messages);
}

