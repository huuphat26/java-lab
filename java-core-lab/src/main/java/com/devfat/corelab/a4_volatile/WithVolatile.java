package com.devfat.corelab.a4_volatile;

/**
 * A4 — Bước "Vá": Dùng volatile để đảm bảo visibility.
 * Worker thread LUÔN thấy giá trị mới nhất của `running`.
 */
public class WithVolatile {

    private static volatile boolean running = true; // CÓ volatile

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            long count = 0;
            while (running) { // volatile read → luôn đọc từ main memory
                count++;
            }
            System.out.println("Worker stopped after " + count + " iterations");
        });

        worker.start();

        Thread.sleep(1000); // chờ 1 giây
        System.out.println("Main thread: setting running = false...");
        running = false; // volatile write → flush về main memory ngay

        worker.join(3000);
        if (!worker.isAlive()) {
            System.out.println("→ Worker thread ĐÃ DỪNG đúng! (volatile đảm bảo visibility)");
        }
    }
}
