package com.devfat.booking.application;

import com.devfat.booking.domain.booking.Booking;
import com.devfat.booking.domain.booking.BookingRepository;
import com.devfat.booking.infrastructure.persistence.JpaBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho BookSeatUseCase.
 * Dùng in-memory repository, KHÔNG cần Spring context.
 */
class BookSeatUseCaseTest {

    private BookSeatUseCase useCase;
    private BookingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaBookingRepository(); // in-memory implementation
        useCase = new BookSeatUseCase(repository);
    }

    @Test
    void should_book_seats_successfully() {
        // When
        Booking booking = useCase.execute("user-1", "showtime-1", List.of("A1", "A2", "A3"));

        // Then
        assertNotNull(booking.getId());
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(3, booking.getSeats().size());
        assertEquals("user-1", booking.getUserId());
        assertEquals("showtime-1", booking.getShowtimeId());
    }

    @Test
    void should_persist_booking_in_repository() {
        // When
        Booking booking = useCase.execute("user-1", "showtime-1", List.of("A1"));

        // Then
        assertTrue(repository.findById(booking.getId()).isPresent());
    }

    @Test
    void should_fail_with_empty_seat_list() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute("user-1", "showtime-1", List.of()));
    }
}
