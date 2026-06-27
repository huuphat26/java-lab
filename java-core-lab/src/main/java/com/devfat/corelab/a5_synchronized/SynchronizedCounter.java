package com.devfat.corelab.a5_synchronized;

/**
 * A5 — synchronized keyword: đảm bảo mutual exclusion + visibility.
 * So sánh với A3 RaceConditionDemo — cùng bài toán count++,
 * nhưng dùng synchronized để fix thay vì AtomicInteger.
 */
public class SynchronizedCounter {

    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) {
            count++; // chỉ 1 thread vào block này tại 1 thời điểm
        }
    }

    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                counter.increment();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Luôn = 200_000
        System.out.println("Count (expected 200000): " + counter.getCount());
        System.out.println("→ synchronized đảm bảo mutual exclusion!");
    }
}
