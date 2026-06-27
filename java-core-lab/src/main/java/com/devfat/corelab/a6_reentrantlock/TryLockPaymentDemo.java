package com.devfat.corelab.a6_reentrantlock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

/**
 * A6 — tryLock với timeout: kịch bản PaymentService.
 * Nếu không lấy được lock trong 500ms → từ chối giao dịch thay vì chờ vô hạn.
 */
public class TryLockPaymentDemo {

    private final ReentrantLock lock = new ReentrantLock();

    public boolean processPayment(String orderId) {
        try {
            // Chờ tối đa 500ms để lấy lock
            if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName()
                            + " → Đang xử lý payment cho order: " + orderId);
                    Thread.sleep(1000); // giả lập xử lý payment
                    System.out.println(Thread.currentThread().getName()
                            + " → Payment thành công: " + orderId);
                    return true;
                } finally {
                    lock.unlock(); // LUÔN unlock trong finally
                }
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " → Timeout! Không lấy được lock cho order: " + orderId);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static void main(String[] args) {
        TryLockPaymentDemo service = new TryLockPaymentDemo();

        // 2 thread cùng gọi processPayment — 1 sẽ thắng, 1 sẽ timeout
        Thread t1 = new Thread(() -> service.processPayment("ORD-001"), "Thread-A");
        Thread t2 = new Thread(() -> service.processPayment("ORD-002"), "Thread-B");

        t1.start();
        t2.start();
    }
}
