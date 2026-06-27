package com.devfat.booking.presentation.dto;

/**
 * DTO: Response body sau khi đặt ghế thành công.
 */
public class BookSeatResponse {

    private String bookingId;
    private String status;
    private int seatCount;

    public BookSeatResponse() {
    }

    public BookSeatResponse(String bookingId, String status, int seatCount) {
        this.bookingId = bookingId;
        this.status = status;
        this.seatCount = seatCount;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }
}
