package com.devfat.booking.domain.booking;

import com.devfat.booking.domain.seat.Seat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root: Booking (đơn đặt vé).
 * Thuộc Domain layer — KHÔNG phụ thuộc Spring/JPA.
 *
 * Aggregate Root là entry point duy nhất để thay đổi state
 * của các entity bên trong aggregate (Seat).
 */
public class Booking {

    private final BookingId id;
    private final String userId;
    private final String showtimeId;
    private final List<Seat> seats;
    private final LocalDateTime createdAt;
    private BookingStatus status;

    public Booking(BookingId id, String userId, String showtimeId, List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("Booking phải có ít nhất 1 ghế");
        }
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seats = new ArrayList<>(seats);
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
    }

    /**
     * Xác nhận booking — reserve tất cả ghế.
     */
    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể confirm booking PENDING, hiện tại: " + status);
        }
        for (Seat seat : seats) {
            seat.reserve(); // domain logic nằm trong Seat entity
        }
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Huỷ booking — release tất cả ghế.
     */
    public void cancel() {
        for (Seat seat : seats) {
            seat.release();
        }
        this.status = BookingStatus.CANCELLED;
    }

    // --- Getters ---

    public BookingId getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }
}
