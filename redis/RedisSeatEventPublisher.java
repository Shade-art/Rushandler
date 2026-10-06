package org.rushandler.rushandler.redis;

import org.rushandler.rushandler.event.SeatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
public class RedisSeatEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RedisSeatEventPublisher.class);
    public static final String SEAT_EVENTS_CHANNEL = "seat-events";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisSeatEventPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSeatBooked(Long eventId, Long seatId) {
        SeatEvent event = SeatEvent.builder()
                .eventId(eventId)
                .seatId(seatId)
                .eventType("SEAT_BOOKED")
                .timestamp(LocalDateTime.now())
                .build();

        publish(event);
    }

    public void publish(SeatEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(SEAT_EVENTS_CHANNEL, message);
            log.info("Published seat event: eventId={}, seatId={}, eventType={}",
                    event.getEventId(), event.getSeatId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish seat event for eventId={}, seatId={}: {}",
                    event.getEventId(), event.getSeatId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish seat event", e);
        }
    }
}
