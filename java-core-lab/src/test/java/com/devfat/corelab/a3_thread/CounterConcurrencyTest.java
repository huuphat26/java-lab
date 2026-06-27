package com.devfat.corelab.a3_thread;

import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test chứng minh race condition bằng CountDownLatch.
 * Dùng @RepeatedTest để chạy nhiều lần — race condition sẽ lộ ra.
 */
class CounterConcurrencyTest {

    @RepeatedTest(5)
    void unsynchronized_counter_should_have_race_condition() throws InterruptedException {
        final int[] count = {0}; // mutable container
        int threadCount = 10;
        int incrementsPerThread = 10_000;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await(); // chờ tất cả thread sẵn sàng
                    for (int j = 0; j < incrementsPerThread; j++) {
                        count[0]++; // KHÔNG thread-safe
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown(); // bắt đầu đồng thời
        doneLatch.await();

        int expected = threadCount * incrementsPerThread;
        // Race condition → count thường < expected
        System.out.println("  Unsync count: " + count[0] + " / expected: " + expected);
        // Không assert vì kết quả không deterministic
    }

    @RepeatedTest(5)
    void atomic_counter_should_always_be_correct() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        int threadCount = 10;
        int incrementsPerThread = 10_000;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < incrementsPerThread; j++) {
                        count.incrementAndGet(); // thread-safe
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        doneLatch.await();

        int expected = threadCount * incrementsPerThread;
        assertEquals(expected, count.get(), "AtomicInteger phải luôn chính xác");
    }
}
