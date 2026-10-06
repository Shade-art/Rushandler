package org.rushandler.rushandler.service;

import org.rushandler.rushandler.dto.BookingResponse;
import org.rushandler.rushandler.entity.Booking;
import org.rushandler.rushandler.entity.Event;
import org.rushandler.rushandler.entity.Seat;
import org.rushandler.rushandler.exception.ResourceConflictException;
import org.rushandler.rushandler.exception.ResourceNotFoundException;
import org.rushandler.rushandler.redis.LockResult;
import org.rushandler.rushandler.redis.RedisLockService;
import org.rushandler.rushandler.repository.BookingRepository;
import org.rushandler.rushandler.repository.EventRepository;
import org.rushandler.rushandler.repository.SeatRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final RedisLockService redisLockService;
    private final TransactionTemplate transactionTemplate;

    public BookingService(
            EventRepository eventRepository,
            SeatRepository seatRepository,
            BookingRepository bookingRepository,
            RedisLockService redisLockService,
            PlatformTransactionManager transactionManager) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.redisLockService = redisLockService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public BookingResponse bookSeat(Long eventId, Long seatId, String userId) {
        // 1. Verify that the event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        // 2. Verify that the seat exists AND belongs to that event
        Seat seat = seatRepository.findByIdAndEventId(seatId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat not found with id: " + seatId + " for event id: " + eventId));

        // 3. Attempt to acquire the Redis lock
        LockResult lockResult = redisLockService.acquireLock(eventId, seatId);
        if (!lockResult.isAcquired()) {
            throw new ResourceConflictException("Seat is currently locked by another request. Please try again.");
        }

        try {
            // 4. In a short database transaction: check whether already booked and create Booking
            //Everything inside this block should happen as ONE database transaction
            Booking booking = transactionTemplate.execute(status -> {
                if (bookingRepository.findByEventIdAndSeatId(eventId, seatId).isPresent()) {
                    throw new ResourceConflictException("Seat has already been booked for this event.");
                }

                Booking newBooking = Booking.builder()
                        .event(event)
                        .seat(seat)
                        .userId(userId)
                        .bookedAt(LocalDateTime.now())
                        .build();

                return bookingRepository.save(newBooking);
            });

            return BookingResponse.fromEntity(booking);
            //last effort to stop duplication
        } catch (DataIntegrityViolationException ex) {
            // Final database-level unique constraint safety net
            throw new ResourceConflictException("Seat has already been booked for this event.");
        } finally {
            // 5. Release Redis lock safely using the owned token
            redisLockService.releaseLock(eventId, seatId, lockResult.getToken());
        }
    }
}
