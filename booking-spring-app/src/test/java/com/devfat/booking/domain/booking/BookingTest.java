package com.devfat.booking.domain.booking;

import com.devfat.booking.domain.seat.Seat;
import com.devfat.booking.domain.seat.SeatStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho Booking Aggregate Root.
 * Test thuần domain logic — KHÔNG cần Spring context.
 */
class BookingTest {

    @Test
    void confirm_should_reserve_all_seats() {
        // Given
        Seat seat1 = new Seat("A1");
        Seat seat2 = new Seat("A2");
        Booking booking = new Booking(
                BookingId.generate(), "user-1", "showtime-1", List.of(seat1, seat2));

        // When
        booking.confirm();

        // Then
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(SeatStatus.RESERVED, seat1.getStatus());
        assertEquals(SeatStatus.RESERVED, seat2.getStatus());
    }

    @Test
    void confirm_should_fail_if_seat_already_reserved() {
        // Given
        Seat seat1 = new Seat("A1");
        seat1.reserve(); // đã đặt trước
        Seat seat2 = new Seat("A2");
        Booking booking = new Booking(
                BookingId.generate(), "user-1", "showtime-1", List.of(seat1, seat2));

        // When & Then
        assertThrows(IllegalStateException.class, booking::confirm,
                "Không thể confirm booking khi ghế đã reserved");
    }

    @Test
    void cancel_should_release_all_seats() {
        // Given
        Seat seat1 = new Seat("A1");
        Seat seat2 = new Seat("A2");
        Booking booking = new Booking(
                BookingId.generate(), "user-1", "showtime-1", List.of(seat1, seat2));
        booking.confirm(); // reserve trước

        // When
        booking.cancel();

        // Then
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(SeatStatus.AVAILABLE, seat1.getStatus());
        assertEquals(SeatStatus.AVAILABLE, seat2.getStatus());
    }

    @Test
    void booking_must_have_at_least_one_seat() {
        assertThrows(IllegalArgumentException.class, () ->
                new Booking(BookingId.generate(), "user-1", "showtime-1", List.of()));
    }
}
