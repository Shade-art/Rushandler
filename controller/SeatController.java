package org.rushandler.rushandler.controller;

import jakarta.validation.Valid;
import org.rushandler.rushandler.dto.SeatRequest;
import org.rushandler.rushandler.dto.SeatResponse;
import org.rushandler.rushandler.service.SeatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeatResponse createSeat(
            @PathVariable Long eventId,
            @Valid @RequestBody SeatRequest request) {
        return seatService.createSeat(eventId, request);
    }

    @GetMapping
    public List<SeatResponse> getSeatsByEvent(@PathVariable Long eventId) {
        return seatService.getSeatsByEventId(eventId);
    }

    @GetMapping("/{seatId}")
    public SeatResponse getSeatById(
            @PathVariable Long eventId,
            @PathVariable Long seatId) {
        return seatService.getSeatById(eventId, seatId);
    }
}
