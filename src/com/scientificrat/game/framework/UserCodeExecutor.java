package com.scientificrat.game.framework;

import java.lang.reflect.Method;
import java.util.concurrent.*;

public class UserCodeExecutor {
    // 线程池
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    @SuppressWarnings("unchecked")
    public static <T> T runMethod(int timeLimit, Object instance, Method method, Object... args) throws InterruptedException, ExecutionException, TimeoutException {
        return cachedThreadPool.submit(() -> (T) method.invoke(instance, args)).get(timeLimit, TimeUnit.MILLISECONDS);
    }

    public static <T> T runMethod(int timeLimit, Callable<T> callable) throws InterruptedException, ExecutionException, TimeoutException {
        return cachedThreadPool.submit(callable).get(timeLimit, TimeUnit.MILLISECONDS);
    }
}
