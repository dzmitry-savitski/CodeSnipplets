package com.upwork.dsavitski.snipplets.threadpool;

import java.util.Random;

/**
 * Represents task example with random execution time.
 */
public class Task implements Runnable {
    private final int threadId;

    public Task(int threadId) {
        this.threadId = threadId;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread #" + threadId + " started.");
            Thread.sleep((new Random()).nextInt(5000));
            System.out.println("Thread #" + threadId + " finished.");
        } catch (InterruptedException unimportant) {/* NOP */}
    }
}
