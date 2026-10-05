package org.rushandler.rushandler.service;

import org.rushandler.rushandler.dto.SeatRequest;
import org.rushandler.rushandler.dto.SeatResponse;
import org.rushandler.rushandler.entity.Event;
import org.rushandler.rushandler.entity.Seat;
import org.rushandler.rushandler.exception.ResourceNotFoundException;
import org.rushandler.rushandler.repository.EventRepository;
import org.rushandler.rushandler.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatService(SeatRepository seatRepository, EventRepository eventRepository) {
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    public SeatResponse createSeat(Long eventId, SeatRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        Seat seat = Seat.builder()
                .seatNumber(request.getSeatNumber())
                .event(event)
                .build();

        Seat savedSeat = seatRepository.save(seat);
        return SeatResponse.fromEntity(savedSeat);
    }

    public List<SeatResponse> getSeatsByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        return seatRepository.findByEventId(eventId)
                .stream()
                .map(SeatResponse::fromEntity)
                .toList();
    }

    public SeatResponse getSeatById(Long eventId, Long seatId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        Seat seat = seatRepository.findByIdAndEventId(seatId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat not found with id: " + seatId + " for event id: " + eventId));
        return SeatResponse.fromEntity(seat);
    }
}
