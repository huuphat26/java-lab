package com.devfat.corelab.a6_reentrantlock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test chứng minh tryLock hoạt động:
 * - 1 thread giữ lock thành công
 * - Thread khác timeout sau 500ms
 */
class TryLockPaymentTest {

    @Test
    void tryLock_one_succeeds_one_times_out() throws InterruptedException {
        TryLockPaymentDemo service = new TryLockPaymentDemo();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            if (service.processPayment("ORD-TEST-1")) {
                successCount.incrementAndGet();
            } else {
                failCount.incrementAndGet();
            }
            doneLatch.countDown();
        });

        Thread t2 = new Thread(() -> {
            if (service.processPayment("ORD-TEST-2")) {
                successCount.incrementAndGet();
            } else {
                failCount.incrementAndGet();
            }
            doneLatch.countDown();
        });

        t1.start();
        t2.start();
        doneLatch.await();

        // 1 thành công, 1 thất bại (timeout)
        assertEquals(1, successCount.get(), "Chỉ 1 payment thành công");
        assertEquals(1, failCount.get(), "1 payment bị timeout");
    }
}
