package com.devfat.booking.domain.seat;

/**
 * Entity: Ghế trong rạp.
 * Thuộc Domain layer — KHÔNG phụ thuộc Spring/JPA.
 */
public class Seat {

    private final String seatId;
    private SeatStatus status;

    public Seat(String seatId) {
        this.seatId = seatId;
        this.status = SeatStatus.AVAILABLE;
    }

    public Seat(String seatId, SeatStatus status) {
        this.seatId = seatId;
        this.status = status;
    }

    /**
     * Đặt ghế — chỉ đặt được khi ghế đang AVAILABLE.
     * @throws IllegalStateException nếu ghế đã được đặt
     */
    public void reserve() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Ghế " + seatId + " không thể đặt — trạng thái hiện tại: " + status);
        }
        this.status = SeatStatus.RESERVED;
    }

    /**
     * Huỷ đặt ghế — trả về trạng thái AVAILABLE.
     */
    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }

    public String getSeatId() {
        return seatId;
    }

    public SeatStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Seat{seatId='" + seatId + "', status=" + status + "}";
    }
}
