package org.rushandler.rushandler.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "seat_id"}) // last option if redis fails both column shoulf be unique
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime bookedAt;
}