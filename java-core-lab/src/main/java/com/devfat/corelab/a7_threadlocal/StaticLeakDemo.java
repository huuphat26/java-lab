package com.devfat.corelab.a7_threadlocal;

/**
 * A7 — Bước "Đập": Dùng biến static bị đụng giữa 2 thread.
 * Thread A set userId, Thread B ghi đè → Thread A đọc nhầm userId của Thread B.
 */
public class StaticLeakDemo {

    // Biến static dùng chung — NGUY HIỂM trong multi-thread
    private static String currentUserId;

    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            currentUserId = "USER_A";
            System.out.println("[Thread A] Set userId = " + currentUserId);
            try {
                Thread.sleep(100); // giả lập xử lý
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Ở đây có thể đọc được "USER_B" thay vì "USER_A"!
            System.out.println("[Thread A] Read userId = " + currentUserId + " (expected USER_A)");
        }, "Thread-A");

        Thread threadB = new Thread(() -> {
            currentUserId = "USER_B"; // GHI ĐÈ giá trị của Thread A!
            System.out.println("[Thread B] Set userId = " + currentUserId);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[Thread B] Read userId = " + currentUserId);
        }, "Thread-B");

        threadA.start();
        Thread.sleep(10); // để Thread A set trước
        threadB.start();

        threadA.join();
        threadB.join();

        System.out.println("\n→ Nếu Thread A đọc USER_B → static leak đã xảy ra!");
    }
}
