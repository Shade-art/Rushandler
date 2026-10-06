package org.rushandler.rushandler.redis;

import org.rushandler.rushandler.event.SeatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RedisSeatEventSubscriber implements MessageListener  {//This class is capable of receiving Redis messages.

    private static final Logger log = LoggerFactory.getLogger(RedisSeatEventSubscriber.class);

    private final ObjectMapper objectMapper;

    //Redis messages can arrive from different threads.This list is designed to be safe when multiple threads access it.
    private final List<SeatEvent> receivedEvents = new CopyOnWriteArrayList<>();

    public RedisSeatEventSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);//Redis gives us the message as bytes. we convert it in string
            SeatEvent event = objectMapper.readValue(messageBody, SeatEvent.class);//desearialize
            receivedEvents.add(event);
            log.info("Received seat event: eventId={}, seatId={}, eventType={}",
                    event.getEventId(), event.getSeatId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to deserialize received seat event: {}", e.getMessage(), e);
        }
    }

    public List<SeatEvent> getReceivedEvents() {
        return Collections.unmodifiableList(receivedEvents);
    }//read only list to not modify the events list

    public void clearReceivedEvents() {
        receivedEvents.clear();
    }
}
