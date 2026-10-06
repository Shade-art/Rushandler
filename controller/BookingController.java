package org.rushandler.rushandler.controller;

import jakarta.validation.Valid;
import org.rushandler.rushandler.dto.BookingRequest;
import org.rushandler.rushandler.dto.BookingResponse;
import org.rushandler.rushandler.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/seats/{seatId}/book")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bookSeat(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @Valid @RequestBody BookingRequest request) {
        return bookingService.bookSeat(eventId, seatId, request.getUserId());
    }
}
