package com.devfat.booking.infrastructure.lock;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

/**
 * BONUS B5 — Pessimistic Lock: SELECT ... FOR UPDATE.
 *
 * Dùng khi cần lock 1 row trong DB để tránh 2 request đặt cùng 1 ghế
 * tại cùng 1 thời điểm (double booking).
 *
 * Ưu điểm: Đơn giản, đáng tin cậy cho single-instance.
 * Nhược điểm: Chỉ hoạt động trong cùng 1 DB, không scale ra distributed system.
 *
 * TODO: Implement câu SQL SELECT FOR UPDATE thật khi đã có JPA entity mapping.
 */
@Component
public class PessimisticSeatLock {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lock ghế bằng SELECT FOR UPDATE.
     * Transaction phải đang active khi gọi method này.
     *
     * @param seatId ID ghế cần lock
     */
    public void lockSeat(String seatId) {
        // TODO: Implement khi đã có SeatJpaEntity
        // Ví dụ:
        // entityManager.createQuery(
        //     "SELECT s FROM SeatJpaEntity s WHERE s.seatId = :seatId",
        //     SeatJpaEntity.class)
        //     .setParameter("seatId", seatId)
        //     .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        //     .getSingleResult();

        System.out.println("[PessimisticLock] Locked seat: " + seatId);
    }
}
