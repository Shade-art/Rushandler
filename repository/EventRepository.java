package org.rushandler.rushandler.repository;

import org.rushandler.rushandler.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}