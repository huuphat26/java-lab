package com.devfat.corelab.bonus_jep491_pinning;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;

/**
 * BONUS — Virtual Thread Pinning (JEP 491).
 *
 * Trước JDK 24: Virtual thread bị "pinned" khi vào synchronized block,
 * khiến carrier thread bị block → giảm throughput.
 *
 * JDK 24+: JEP 491 cho phép virtual thread unmount ngay cả trong synchronized,
 * giải quyết vấn đề pinning.
 *
 * YÊU CẦU: Chạy trên JDK 21+ (để dùng Virtual Thread).
 * Thêm -Djdk.tracePinnedThreads=short để thấy warning khi pin xảy ra (JDK < 24).
 */
public class VirtualThreadPinningDemo {

    private static final Object lock = new Object();

    /**
     * Task giả lập I/O blocking bên trong synchronized block.
     * Trên JDK < 24: virtual thread bị pinned ở đây.
     * Trên JDK 24+: virtual thread sẽ unmount bình thường.
     */
    static void blockingTaskWithSync(int taskId) {
        synchronized (lock) {
            try {
                System.out.println("Task " + taskId + " — bắt đầu (thread: "
                        + Thread.currentThread() + ")");
                Thread.sleep(500); // giả lập I/O
                System.out.println("Task " + taskId + " — hoàn thành");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Nếu JDK < 24: thêm -Djdk.tracePinnedThreads=short để thấy pinning warning\n");

        int taskCount = 10;
        Instant start = Instant.now();

        // Dùng virtual thread executor
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> blockingTaskWithSync(taskId));
            }
        } // executor.close() chờ tất cả task hoàn thành

        Duration elapsed = Duration.between(start, Instant.now());
        System.out.println("\nTổng thời gian cho " + taskCount + " tasks: " + elapsed.toMillis() + "ms");
        System.out.println("→ JDK < 24 (pinned): ~" + (taskCount * 500) + "ms (tuần tự vì lock)");
        System.out.println("→ JDK 24+ (no pin):  ~500ms (virtual thread unmount trong synchronized)");
    }
}
