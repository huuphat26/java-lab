package com.devfat.corelab.a8_completablefuture;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

/**
 * Test chứng minh CompletableFuture.allOf() chạy nhanh hơn tuần tự.
 */
class ParallelWithCFTest {

    @Test
    void parallel_calls_should_be_faster_than_sequential() {
        long start = System.currentTimeMillis();

        CompletableFuture<String> f1 = ParallelWithCF.callUserService();
        CompletableFuture<String> f2 = ParallelWithCF.callOrderService();
        CompletableFuture<String> f3 = ParallelWithCF.callPaymentService();

        CompletableFuture.allOf(f1, f2, f3).join();

        long elapsed = System.currentTimeMillis() - start;

        // Nếu chạy song song → ~1000ms, nếu tuần tự → ~3000ms
        assertTrue(elapsed < 2000,
                "Parallel calls phải < 2000ms, actual: " + elapsed + "ms");

        // Verify kết quả không null
        assertNotNull(f1.join());
        assertNotNull(f2.join());
        assertNotNull(f3.join());
    }
}
