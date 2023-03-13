package com.xtremis.daedo.tkstrike.tools.utils;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TkStrikeExecutors {
  private static final ExecutorService threadPoolEs = Executors.newCachedThreadPool();
  
  private static final ScheduledExecutorService scheduledEs = Executors.newSingleThreadScheduledExecutor();
  
  public static void executeInThreadPool(Runnable runnable) {
    if (!threadPoolEs.isTerminated())
      threadPoolEs.execute(runnable); 
  }
  
  public static void executeInThreadPool(Callable<Void> callable) {
    if (!threadPoolEs.isTerminated())
      threadPoolEs.submit(callable); 
  }
  
  public static void executeInParallel(Collection<Callable<Void>> callables) throws InterruptedException {
    if (!threadPoolEs.isTerminated())
      threadPoolEs.invokeAll(callables); 
  }
  
  public static void schedule(Runnable command, long delay, TimeUnit unit) {
    if (!scheduledEs.isTerminated())
      scheduledEs.schedule(command, delay, unit); 
  }
  
  public static void schedule(Runnable command, long delay, long period, TimeUnit unit) {
    if (!scheduledEs.isTerminated())
      scheduledEs.scheduleAtFixedRate(command, delay, period, unit); 
  }
  
  public static void shutdownThreadPool() {
    threadPoolEs.shutdownNow();
  }
  
  public static void shutdownThreadScheduled() {
    scheduledEs.shutdownNow();
  }
}
