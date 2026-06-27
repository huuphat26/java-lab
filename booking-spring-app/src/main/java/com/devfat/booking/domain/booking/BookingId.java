package com.devfat.booking.domain.booking;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: BookingId.
 * Immutable, so sánh theo giá trị (value equality), không theo reference.
 *
 * Đây là pattern DDD: dùng Value Object thay cho primitive (String/Long)
 * để tránh nhầm lẫn tham số (ví dụ: truyền nhầm orderId vào chỗ bookingId).
 */
public class BookingId {

    private final String value;

    private BookingId(String value) {
        this.value = Objects.requireNonNull(value, "BookingId không được null");
    }

    public static BookingId generate() {
        return new BookingId(UUID.randomUUID().toString());
    }

    public static BookingId of(String value) {
        return new BookingId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingId bookingId = (BookingId) o;
        return Objects.equals(value, bookingId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
