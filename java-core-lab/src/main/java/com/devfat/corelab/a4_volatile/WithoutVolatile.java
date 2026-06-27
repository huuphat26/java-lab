package com.devfat.corelab.a4_volatile;

/**
 * A4 — Bước "Đập": KHÔNG dùng volatile.
 * Thread chính set running = false, nhưng worker thread có thể KHÔNG BAO GIỜ dừng
 * vì JVM cache giá trị `running` trong CPU register/cache của worker thread.
 *
 * Lưu ý: Bug này không phải lúc nào cũng tái hiện — phụ thuộc JIT và HW.
 * Nếu chạy mà vẫn dừng → thử thêm -server flag hoặc chạy trên server JVM.
 */
public class WithoutVolatile {

    private static boolean running = true; // KHÔNG volatile

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            long count = 0;
            while (running) { // có thể đọc giá trị cached (true mãi mãi)
                count++;
            }
            System.out.println("Worker stopped after " + count + " iterations");
        });

        worker.start();

        Thread.sleep(1000); // chờ 1 giây
        System.out.println("Main thread: setting running = false...");
        running = false; // ghi vào main memory, nhưng worker có thể không thấy

        worker.join(3000); // chờ tối đa 3s
        if (worker.isAlive()) {
            System.out.println("→ Worker thread KHÔNG DỪNG! (visibility bug — thiếu volatile)");
            worker.interrupt(); // force stop để chương trình kết thúc
        }
    }
}
