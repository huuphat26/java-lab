package com.devfat.corelab.a6_reentrantlock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A6 — Condition: Producer-Consumer pattern với bounded buffer.
 * Dùng ReentrantLock + Condition thay vì wait()/notify().
 */
public class BoundedBufferDemo {

    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBufferDemo(int capacity) {
        this.capacity = capacity;
    }

    public void produce(int item) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.size() == capacity) {
                System.out.println("Buffer đầy — Producer chờ...");
                notFull.await(); // chờ cho đến khi buffer có chỗ
            }
            buffer.add(item);
            System.out.println("Produced: " + item + " | Buffer size: " + buffer.size());
            notEmpty.signal(); // báo cho consumer biết có hàng
        } finally {
            lock.unlock();
        }
    }

    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                System.out.println("Buffer rỗng — Consumer chờ...");
                notEmpty.await(); // chờ cho đến khi có hàng
            }
            int item = buffer.poll();
            System.out.println("Consumed: " + item + " | Buffer size: " + buffer.size());
            notFull.signal(); // báo cho producer biết có chỗ
            return item;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BoundedBufferDemo buffer = new BoundedBufferDemo(3);

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.produce(i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.consume();
                    Thread.sleep(500); // consumer chậm hơn producer
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }
}
