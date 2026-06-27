package com.devfat.booking.presentation;

import com.devfat.booking.application.BookSeatUseCase;
import com.devfat.booking.domain.booking.Booking;
import com.devfat.booking.presentation.dto.BookSeatRequest;
import com.devfat.booking.presentation.dto.BookSeatResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Presentation layer: REST Controller.
 * Nhận HTTP request, delegate cho Application layer (Use Case).
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookSeatUseCase bookSeatUseCase;

    public BookingController(BookSeatUseCase bookSeatUseCase) {
        this.bookSeatUseCase = bookSeatUseCase;
    }

    /**
     * POST /api/bookings — Đặt ghế.
     */
    @PostMapping
    public ResponseEntity<BookSeatResponse> bookSeat(@Valid @RequestBody BookSeatRequest request) {
        Booking booking = bookSeatUseCase.execute(
                request.getUserId(),
                request.getShowtimeId(),
                request.getSeatIds()
        );

        BookSeatResponse response = new BookSeatResponse(
                booking.getId().getValue(),
                booking.getStatus().name(),
                booking.getSeats().size()
        );

        return ResponseEntity.ok(response);
    }
}
