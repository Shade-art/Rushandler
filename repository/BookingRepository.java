package org.rushandler.rushandler.repository;

import org.rushandler.rushandler.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByEventIdAndSeatId(Long eventId, Long seatId); //Is this seat already permanently booked for this event?
}