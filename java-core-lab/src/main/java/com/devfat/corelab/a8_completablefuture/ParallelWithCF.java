package com.devfat.corelab.a8_completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A8 — Bước "Vá": Dùng CompletableFuture.allOf() để gọi 3 API song song.
 * Tổng thời gian ~1000ms (bằng call chậm nhất) thay vì ~3000ms.
 */
public class ParallelWithCF {

    static CompletableFuture<String> callUserService() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "User: Nguyễn Văn A";
        });
    }

    static CompletableFuture<String> callOrderService() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "Orders: 5 đơn hàng";
        });
    }

    static CompletableFuture<String> callPaymentService() {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return "Payment: 2.500.000 VNĐ";
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        // Gọi song song — 3 calls chạy đồng thời
        CompletableFuture<String> userFuture = callUserService();
        CompletableFuture<String> orderFuture = callOrderService();
        CompletableFuture<String> paymentFuture = callPaymentService();

        // Chờ TẤT CẢ hoàn thành
        CompletableFuture.allOf(userFuture, orderFuture, paymentFuture).join();

        long elapsed = System.currentTimeMillis() - start;

        System.out.println(userFuture.get());
        System.out.println(orderFuture.get());
        System.out.println(paymentFuture.get());
        System.out.println("\n→ Tổng thời gian: " + elapsed + "ms (expected ~1000ms)");
        System.out.println("→ CompletableFuture.allOf() chạy song song, nhanh gấp 3!");
    }
}
