package com.devfat.corelab.a5_synchronized;

import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;

/**
 * Test chứng minh SynchronizedCounter luôn cho kết quả đúng
 * dù chạy với nhiều thread đồng thời.
 */
class SynchronizedCounterTest {

    @RepeatedTest(5)
    void synchronized_counter_should_always_be_correct() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        int threadCount = 10;
        int incrementsPerThread = 10_000;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter.increment();
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
        assertEquals(expected, counter.getCount(),
                "synchronized counter phải luôn chính xác: " + counter.getCount() + " vs " + expected);
    }
}
