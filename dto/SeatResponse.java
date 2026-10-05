package org.rushandler.rushandler.dto;

import lombok.*;
import org.rushandler.rushandler.entity.Seat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {

    private Long id;
    private String seatNumber;
    private Long eventId;

    public static SeatResponse fromEntity(Seat seat) {
        if (seat == null) {
            return null;
        }
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .eventId(seat.getEvent() != null ? seat.getEvent().getId() : null)
                .build();
    }
}
