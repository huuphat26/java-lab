package com.devfat.booking.infrastructure.persistence;

import com.devfat.booking.domain.booking.Booking;
import com.devfat.booking.domain.booking.BookingId;
import com.devfat.booking.domain.booking.BookingRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Infrastructure: Implement BookingRepository interface từ Domain layer.
 *
 * Hiện tại dùng in-memory Map để đơn giản hoá.
 * Khi sẵn sàng, thay bằng JPA/Spring Data implementation thật.
 *
 * Đây là Dependency Inversion: Domain định nghĩa interface,
 * Infrastructure cung cấp implementation cụ thể.
 */
@Repository
public class JpaBookingRepository implements BookingRepository {

    // TODO: Thay bằng JPA EntityManager hoặc Spring Data JpaRepository
    private final Map<String, Booking> store = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        store.put(booking.getId().getValue(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(BookingId id) {
        return Optional.ofNullable(store.get(id.getValue()));
    }
}
