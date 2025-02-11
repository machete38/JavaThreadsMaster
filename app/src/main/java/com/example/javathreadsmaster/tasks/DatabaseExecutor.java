package com.example.javathreadsmaster.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseExecutor {

    public static <T> Future<T> submit(Callable<T> task) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Future<T> future = executorService.submit(task);
        executorService.shutdown();
        return future;
    }


}
