package com.devfat.corelab.b1_concurrency_vs_parallelism;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

/**
 * B1 — Concurrency vs Parallelism: Demo parallelStream.
 *
 * - Concurrency: nhiều task TIẾN TRIỂN cùng lúc (có thể trên 1 core, luân phiên).
 * - Parallelism: nhiều task THỰC SỰ chạy cùng lúc (cần nhiều core).
 *
 * parallelStream() tận dụng ForkJoinPool để chia công việc ra nhiều core.
 */
public class ParallelStreamDemo {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
                .boxed()
                .toList();
        // Sequential stream
        long startSeq = System.currentTimeMillis();
        long sumSeq = numbers.stream()
                .mapToLong(n -> (long) n * n)
                .sum();
        long elapsedSeq = System.currentTimeMillis() - startSeq;

        // Parallel stream
        long startPar = System.currentTimeMillis();
        long sumPar = numbers.parallelStream()
                .mapToLong(n -> (long) n * n)
                .sum();
        long elapsedPar = System.currentTimeMillis() - startPar;

        System.out.println("Sequential sum: " + sumSeq + " | Time: " + elapsedSeq + "ms");
        System.out.println("Parallel   sum: " + sumPar + " | Time: " + elapsedPar + "ms");
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("\n→ Parallel nhanh hơn khi data đủ lớn & task CPU-bound.");
        System.out.println("→ Với data nhỏ hoặc I/O-bound, parallel có thể CHẬM hơn do overhead.");
    }
}
