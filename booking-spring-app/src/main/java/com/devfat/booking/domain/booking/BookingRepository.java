package com.devfat.booking.domain.booking;

import java.util.Optional;

/**
 * Repository interface — định nghĩa ở Domain layer.
 * Implementation nằm ở Infrastructure layer (JpaBookingRepository).
 *
 * Đây là Dependency Inversion Principle (DIP) trong DDD:
 * Domain định nghĩa interface, Infrastructure implement.
 */
public interface BookingRepository {

    /**
     * Lưu booking.
     */
    Booking save(Booking booking);

    /**
     * Tìm booking theo ID.
     */
    Optional<Booking> findById(BookingId id);
}
