package com.devfat.corelab.a8_completablefuture;

/**
 * A8 — Bước "Đập": Gọi 3 API tuần tự, tổng ~3000ms.
 * Mỗi API mất 1000ms, gọi lần lượt → chậm không cần thiết.
 */
public class SequentialCallsDemo {

    // Giả lập gọi API, mỗi call mất ~1000ms
    static String callUserService() throws InterruptedException {
        Thread.sleep(1000);
        return "User: Nguyễn Văn A";
    }

    static String callOrderService() throws InterruptedException {
        Thread.sleep(1000);
        return "Orders: 5 đơn hàng";
    }

    static String callPaymentService() throws InterruptedException {
        Thread.sleep(1000);
        return "Payment: 2.500.000 VNĐ";
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        // Gọi tuần tự — mỗi call phải chờ call trước hoàn thành
        String user = callUserService();
        String orders = callOrderService();
        String payment = callPaymentService();

        long elapsed = System.currentTimeMillis() - start;

        System.out.println(user);
        System.out.println(orders);
        System.out.println(payment);
        System.out.println("\n→ Tổng thời gian: " + elapsed + "ms (expected ~3000ms)");
        System.out.println("→ Vì gọi tuần tự, mỗi call chờ cái trước xong mới chạy!");
    }
}
