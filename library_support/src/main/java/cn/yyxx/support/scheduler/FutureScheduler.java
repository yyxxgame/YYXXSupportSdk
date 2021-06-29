package cn.yyxx.support.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

/**
 * @author #Suyghur.
 * Created on 10/26/20
 */
public interface FutureScheduler {

    ScheduledFuture<?> scheduleFuture(Runnable command, long millisecondDelay);

    ScheduledFuture<?> scheduleFutureWithFixedDelay(Runnable command, long initialMillisecondDelay, long millisecondDelay);

    <V> ScheduledFuture<V> scheduleFutureWithReturn(Callable<V> callable, long millisecondDelay);

    void teardown();
}
