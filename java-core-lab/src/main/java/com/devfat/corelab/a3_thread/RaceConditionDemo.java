package com.devfat.corelab.a3_thread;

/**
 * A3 — Bước "Đập": 2 thread cùng count++, CHƯA FIX race condition.
 * Chạy nhiều lần — kết quả sẽ KHÁC NHAU mỗi lần (< 200_000).
 */
public class RaceConditionDemo {

    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                count++; // KHÔNG atomic — read-modify-write
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                count++; // race condition ở đây
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Kỳ vọng 200_000 nhưng thường sẽ < 200_000
        System.out.println("Count (expected 200000): " + count);
        System.out.println("→ Nếu < 200000 → race condition đã xảy ra!");
    }
}
