package com.devfat.booking.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * JPA Entity: mapping Booking domain object xuống database.
 *
 * LƯU Ý DDD: Class này thuộc Infrastructure, KHÔNG thuộc Domain.
 * Domain Booking class không có annotation JPA (@Entity, @Id...).
 * Class này là "adapter" giữa Domain model và database schema.
 *
 * TODO: Implement mapping giữa BookingJpaEntity ↔ Booking domain object.
 */
@Entity
@Table(name = "bookings")
public class BookingJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String showtimeId;

    @Column(nullable = false)
    private String status;

    // JPA cần default constructor
    protected BookingJpaEntity() {
    }

    public BookingJpaEntity(String id, String userId, String showtimeId, String status) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.status = status;
    }

    // --- Getters & Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
