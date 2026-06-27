package com.devfat.booking.infrastructure.lock;

import org.springframework.stereotype.Component;

/**
 * BONUS B5 — Distributed Lock bằng Redis (Redisson).
 *
 * Dùng khi hệ thống chạy trên nhiều instance (horizontal scaling).
 * Pessimistic lock chỉ hoạt động trên 1 DB → cần distributed lock.
 *
 * Ưu điểm: Lock across multiple instances.
 * Nhược điểm: Cần Redis, phức tạp hơn, phải handle lock expiry.
 *
 * TODO: Thêm dependency Redisson và implement lock thật.
 * Dependency cần thêm vào pom.xml:
 *   <dependency>
 *       <groupId>org.redisson</groupId>
 *       <artifactId>redisson-spring-boot-starter</artifactId>
 *       <version>3.27.0</version>
 *   </dependency>
 */
@Component
public class RedisDistributedLock {

    // TODO: Inject RedissonClient khi đã thêm dependency
    // private final RedissonClient redisson;

    /**
     * Lấy distributed lock cho 1 resource.
     *
     * @param lockKey  Key để lock (ví dụ: "seat:A1:showtime:123")
     * @param waitMs   Thời gian chờ tối đa (ms)
     * @param leaseMs  Thời gian giữ lock tối đa (ms) — tự release sau thời gian này
     * @return true nếu lấy được lock
     */
    public boolean tryLock(String lockKey, long waitMs, long leaseMs) {
        // TODO: Implement với Redisson
        // RLock lock = redisson.getLock(lockKey);
        // return lock.tryLock(waitMs, leaseMs, TimeUnit.MILLISECONDS);

        System.out.println("[RedisLock] tryLock: " + lockKey
                + " (wait=" + waitMs + "ms, lease=" + leaseMs + "ms)");
        return true; // placeholder
    }

    /**
     * Release distributed lock.
     */
    public void unlock(String lockKey) {
        // TODO: Implement với Redisson
        // RLock lock = redisson.getLock(lockKey);
        // if (lock.isHeldByCurrentThread()) {
        //     lock.unlock();
        // }

        System.out.println("[RedisLock] unlock: " + lockKey);
    }
}
