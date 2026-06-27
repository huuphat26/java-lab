package com.devfat.booking.application;

import com.devfat.booking.domain.booking.Booking;
import com.devfat.booking.domain.booking.BookingId;
import com.devfat.booking.domain.booking.BookingRepository;
import com.devfat.booking.domain.seat.Seat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service (Use Case): Điều phối domain logic.
 *
 * Lớp này KHÔNG chứa business logic — chỉ điều phối:
 * 1. Nhận request từ Presentation
 * 2. Gọi Domain objects để thực thi logic
 * 3. Gọi Repository để persist
 *
 * Business logic nằm trong Booking (Aggregate Root) và Seat (Entity).
 */
@Service
public class BookSeatUseCase {

    private final BookingRepository bookingRepository;

    public BookSeatUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Đặt ghế cho 1 user tại 1 suất chiếu.
     *
     * @param userId     ID người dùng
     * @param showtimeId ID suất chiếu
     * @param seatIds    Danh sách ghế muốn đặt
     * @return Booking đã được confirm
     */
    public Booking execute(String userId, String showtimeId, List<String> seatIds) {
        // 1. Tạo domain objects
        List<Seat> seats = seatIds.stream()
                .map(Seat::new)
                .collect(Collectors.toList());

        // 2. Tạo Booking (Aggregate Root)
        Booking booking = new Booking(
                BookingId.generate(),
                userId,
                showtimeId,
                seats
        );

        // 3. Confirm booking — domain logic chạy bên trong Booking.confirm()
        booking.confirm();

        // 4. Persist qua repository
        return bookingRepository.save(booking);
    }
}
