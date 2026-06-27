package com.devfat.corelab.a3_thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A3 — Bước "Vá": Dùng CountDownLatch để ép 2 thread chạy cùng lúc,
 * kết hợp AtomicInteger để fix race condition.
 */
public class FixedWithLatch {

    private static final AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                startSignal.await(); // chờ tín hiệu
                for (int i = 0; i < 100_000; i++) {
                    count.incrementAndGet(); // atomic operation
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                startSignal.await(); // chờ tín hiệu
                for (int i = 0; i < 100_000; i++) {
                    count.incrementAndGet(); // atomic operation
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();

        // Cả 2 thread đang chờ → countdown để chạy đồng thời
        startSignal.countDown();

        t1.join();
        t2.join();

        // Luôn = 200_000 vì AtomicInteger là thread-safe
        System.out.println("Count (expected 200000): " + count.get());
        System.out.println("→ AtomicInteger + CountDownLatch = chính xác & đồng thời!");
    }
}
