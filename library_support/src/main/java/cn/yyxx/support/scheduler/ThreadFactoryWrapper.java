package cn.yyxx.support.scheduler;

import android.os.Process;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import cn.yyxx.support.hawkeye.LogUtils;

/**
 * @author #Suyghur.
 * Created on 10/26/20
 */
public class ThreadFactoryWrapper implements ThreadFactory {
    private String source;
    private static final String THREAD_PREFIX = "YYXXSupport";

    public ThreadFactoryWrapper(String source) {
        this.source = source;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);

        thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        thread.setName("THREAD_PREFIX" + thread.getName() + "-" + source);
        thread.setDaemon(true);

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable tr) {
                LogUtils.e("Thread " + th.getName() + " with error " + tr.getMessage());
            }
        });

        return thread;
    }
}
