package com.devfat.booking.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO: Request body cho API đặt ghế.
 */
public class BookSeatRequest {

    @NotBlank(message = "userId không được trống")
    private String userId;

    @NotBlank(message = "showtimeId không được trống")
    private String showtimeId;

    @NotEmpty(message = "Phải chọn ít nhất 1 ghế")
    private List<String> seatIds;

    // --- Constructors ---

    public BookSeatRequest() {
    }

    public BookSeatRequest(String userId, String showtimeId, List<String> seatIds) {
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
    }

    // --- Getters & Setters ---

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

    public List<String> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<String> seatIds) {
        this.seatIds = seatIds;
    }
}
