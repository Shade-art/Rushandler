package org.rushandler.rushandler.repository;

import org.rushandler.rushandler.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventId(Long eventId);//get all seats for an event

    Optional<Seat> findByIdAndEventId(Long id, Long eventId);// Get one specific seat, but only if it belongs to that event.
}