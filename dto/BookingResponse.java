package org.rushandler.rushandler.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.rushandler.rushandler.entity.Booking;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long eventId;
    private Long seatId;
    private String seatNumber;
    private String userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookedAt;

    public static BookingResponse fromEntity(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEvent() != null ? booking.getEvent().getId() : null)
                .seatId(booking.getSeat() != null ? booking.getSeat().getId() : null)
                .seatNumber(booking.getSeat() != null ? booking.getSeat().getSeatNumber() : null)
                .userId(booking.getUserId())
                .bookedAt(booking.getBookedAt())
                .build();
    }
}
