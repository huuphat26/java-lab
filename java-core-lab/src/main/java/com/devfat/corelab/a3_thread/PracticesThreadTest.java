package com.devfat.corelab.a3_thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PracticesThreadTest {

    static class Account {
        double balance;
        public Account(double balance) { this.balance = balance; }
        public double getBalance() { return balance; }
    }

    Account account = new Account(1_000_000);

    public void withdraw(int amount) {
        if (account.balance >= amount) {
            try {
                Thread.sleep(1); // 1ms - đủ lớn để ép context switch, khác hẳn 1 nano giây trước đó
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            account.balance -= amount;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PracticesThreadTest test = new PracticesThreadTest();
        int threadCount = 2000;
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadCount);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                try {
                    startSignal.await();   // tất cả đứng chờ ở đây
                    test.withdraw(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            });
            threads.add(t);
            t.start();
        }

        startSignal.countDown();  // "bắn cò" - 2000 thread cùng chạy gần như đồng thời
        doneSignal.await();

        double expected = 1_000_000 - threadCount * 500;
        System.out.println("Balance cuối cùng: " + test.account.getBalance());
        System.out.println("Kỳ vọng lý thuyết: " + expected);
        System.out.println("Chênh lệch (đúng phải = 0, lệch dương = đã mất bớt số lần trừ): "
                + (test.account.getBalance() - expected));
    }
}