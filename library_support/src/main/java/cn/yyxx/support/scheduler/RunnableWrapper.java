package cn.yyxx.support.scheduler;

import cn.yyxx.support.hawkeye.LogUtils;

/**
 * @author #Suyghur.
 * Created on 10/26/20
 */
public class RunnableWrapper implements Runnable {

    private Runnable runnable;

    RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Throwable t) {
            LogUtils.e("Runnable error " + t.getMessage() + " of type " + t.getClass().getCanonicalName());
        }
    }
}
