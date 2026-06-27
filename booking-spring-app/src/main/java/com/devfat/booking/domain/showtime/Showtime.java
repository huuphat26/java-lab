package com.devfat.booking.domain.showtime;

import java.time.LocalDateTime;

/**
 * Entity: Suất chiếu.
 * Thuộc Domain layer.
 */
public class Showtime {

    private final String showtimeId;
    private final String movieName;
    private final LocalDateTime startTime;
    private final int totalSeats;

    public Showtime(String showtimeId, String movieName, LocalDateTime startTime, int totalSeats) {
        this.showtimeId = showtimeId;
        this.movieName = movieName;
        this.startTime = startTime;
        this.totalSeats = totalSeats;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public String getMovieName() {
        return movieName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    @Override
    public String toString() {
        return "Showtime{" +
                "showtimeId='" + showtimeId + '\'' +
                ", movieName='" + movieName + '\'' +
                ", startTime=" + startTime +
                ", totalSeats=" + totalSeats +
                '}';
    }
}
