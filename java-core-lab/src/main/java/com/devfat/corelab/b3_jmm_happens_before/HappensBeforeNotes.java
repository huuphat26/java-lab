package com.devfat.corelab.b3_jmm_happens_before;

/**
 * B3 — Java Memory Model & Happens-Before: Ghi chú minh hoạ.
 *
 * File này chủ yếu là COMMENT giải thích — vì JMM là khái niệm lý thuyết,
 * khó demo trực tiếp bằng code (bug do JMM vi phạm rất khó tái hiện).
 *
 * ===== 6 QUY TẮC HAPPENS-BEFORE CỐT LÕI =====
 *
 * 1. Program Order Rule:
 *    Trong cùng 1 thread, statement trước happens-before statement sau.
 *    → x = 1; y = x + 1;  // y luôn thấy x = 1 (trong cùng thread)
 *
 * 2. Monitor Lock Rule:
 *    unlock() trên 1 monitor happens-before lock() tiếp theo trên cùng monitor.
 *    → synchronized(lock) { x = 1; }  // thread B sẽ thấy x = 1 khi vào synchronized(lock)
 *
 * 3. Volatile Variable Rule:
 *    Ghi volatile happens-before đọc volatile cùng biến.
 *    → volatile boolean flag; flag = true (T1) → T2 đọc flag luôn thấy true
 *
 * 4. Thread Start Rule:
 *    thread.start() happens-before mọi action trong thread đó.
 *    → x = 42; thread.start();  // thread mới luôn thấy x = 42
 *
 * 5. Thread Join Rule:
 *    Mọi action trong thread happens-before thread.join() return.
 *    → thread.join(); System.out.println(x);  // thấy mọi thay đổi từ thread
 *
 * 6. Transitivity:
 *    Nếu A happens-before B, và B happens-before C → A happens-before C.
 */
public class HappensBeforeNotes {

    // --- Ví dụ 1: KHÔNG có happens-before → có thể sai ---
    static int x = 0;
    static boolean ready = false; // KHÔNG volatile

    // --- Ví dụ 2: CÓ happens-before (volatile) → đảm bảo đúng ---
    static int safeX = 0;
    static volatile boolean safeReady = false; // CÓ volatile

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===== JMM & Happens-Before Demo =====\n");

        // Demo 1: Không có happens-before guarantee
        System.out.println("--- Demo 1: Không volatile (có thể sai) ---");
        for (int trial = 0; trial < 5; trial++) {
            x = 0;
            ready = false;

            Thread writer = new Thread(() -> {
                x = 42;
                ready = true; // KHÔNG volatile → compiler/CPU có thể reorder
            });

            Thread reader = new Thread(() -> {
                while (!ready) { /* spin */ }
                // Có thể đọc x = 0 dù ready = true (do reordering)
                if (x != 42) {
                    System.out.println("  BUG! ready=true nhưng x=" + x + " (expected 42)");
                }
            });

            writer.start();
            reader.start();
            writer.join(100);
            reader.join(100);
        }
        System.out.println("  (Bug rất khó tái hiện — nhưng trong production với traffic cao, SẼ xảy ra)\n");

        // Demo 2: Có happens-before (volatile)
        System.out.println("--- Demo 2: Có volatile (luôn đúng) ---");
        safeX = 0;
        safeReady = false;

        Thread safeWriter = new Thread(() -> {
            safeX = 42;
            safeReady = true; // volatile write → happens-before volatile read
        });

        Thread safeReader = new Thread(() -> {
            while (!safeReady) { /* spin */ }
            // safeX CHẮC CHẮN = 42 nhờ volatile happens-before
            System.out.println("  safeX = " + safeX + " (guaranteed 42 by happens-before)");
        });

        safeWriter.start();
        safeReader.start();
        safeWriter.join();
        safeReader.join();

        System.out.println("\n→ Đọc thêm: Mục 10 trong java-core-deep-dive.md");
    }
}
