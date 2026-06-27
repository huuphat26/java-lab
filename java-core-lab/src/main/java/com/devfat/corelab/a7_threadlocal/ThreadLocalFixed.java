package com.devfat.corelab.a7_threadlocal;

/**
 * A7 — Bước "Vá": Dùng ThreadLocal để mỗi thread có bản copy riêng.
 * Thread A và Thread B không bao giờ đọc nhầm userId của nhau.
 *
 * LƯU Ý QUAN TRỌNG: Trong môi trường thread pool (web server, ExecutorService),
 * PHẢI gọi remove() sau khi xử lý xong để tránh memory leak.
 */
public class ThreadLocalFixed {

    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        Thread threadA = new Thread(() -> {
            try {
                currentUserId.set("USER_A");
                System.out.println("[Thread A] Set userId = " + currentUserId.get());
                Thread.sleep(100);
                System.out.println("[Thread A] Read userId = " + currentUserId.get()
                        + " (expected USER_A)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                currentUserId.remove(); // QUAN TRỌNG: cleanup trong finally
            }
        }, "Thread-A");

        Thread threadB = new Thread(() -> {
            try {
                currentUserId.set("USER_B");
                System.out.println("[Thread B] Set userId = " + currentUserId.get());
                Thread.sleep(100);
                System.out.println("[Thread B] Read userId = " + currentUserId.get()
                        + " (expected USER_B)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                currentUserId.remove();
            }
        }, "Thread-B");

        threadA.start();
        Thread.sleep(10);
        threadB.start();

        threadA.join();
        threadB.join();

        System.out.println("\n→ Mỗi thread đọc đúng userId riêng — ThreadLocal hoạt động!");
    }
}
