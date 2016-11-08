package com.upwork.dsavitski.snipplets.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An example of using ThreadPoolExecutor
 */
public class Example {
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
                for (int threadId = 0; threadId < 100; threadId++) {
                    executor.submit(new Task(threadId));
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
