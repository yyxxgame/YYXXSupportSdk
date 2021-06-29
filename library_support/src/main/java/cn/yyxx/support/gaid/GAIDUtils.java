package cn.yyxx.support.gaid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.text.TextUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.yyxx.support.ReflectUtils;
import cn.yyxx.support.hawkeye.LogUtils;
import cn.yyxx.support.scheduler.SingleThreadFutureScheduler;

/**
 * @author #Suyghur.
 * Created on 10/26/20
 */
public class GAIDUtils {

    private GAIDUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static final int ONE_SECOND = 1000;

    private static String adid = "";

    private static volatile SingleThreadFutureScheduler playAdIdScheduler = null;

    public static Object getAdvertisingInfoObject(final Context context, long timeoutMilli) {
        return runSyncInPlayAdIdSchedulerWithTimeout(context, new Callable<Object>() {
            @Override
            public Object call() {
                String clzName = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
                String methodName = "getAdvertisingIdInfo";
                return ReflectUtils.callStaticMethod(clzName, methodName, new Class[]{Context.class}, new Object[]{context});
            }
        }, timeoutMilli);
    }

    public static String getPlayAdId(final Context context, final Object advertisingInfoObject, long timeoutMilli) {
        return runSyncInPlayAdIdSchedulerWithTimeout(context, new Callable<String>() {
            @Override
            public String call() {
                return getPlayAdId(advertisingInfoObject);
            }
        }, timeoutMilli);
    }

    public static String getPlayAdId(Object AdvertisingInfoObject) {
        try {
            return (String) ReflectUtils.callMethod(AdvertisingInfoObject, "getId", null, null);
        } catch (Throwable t) {
            return null;
        }
    }

    private static <R> R runSyncInPlayAdIdSchedulerWithTimeout(final Context context, Callable<R> callable, long timeoutMilli) {
        if (playAdIdScheduler == null) {
            synchronized (GAIDUtils.class) {
                if (playAdIdScheduler == null) {
                    playAdIdScheduler = new SingleThreadFutureScheduler("PlayAdIdLibrary", true);
                }
            }
        }
        ScheduledFuture<R> playAdIdFuture = playAdIdScheduler.scheduleFutureWithReturn(callable, 0);

        try {
            return playAdIdFuture.get(timeoutMilli, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getGoogleAdid() {
        return adid;
    }

    public static void initGoogleAdid(Context context, final OnDeviceIdsRead onDeviceIdRead) {

        if (Looper.myLooper() != Looper.getMainLooper()) {
            LogUtils.e("GoogleAdId should read in the background");
            String googleAdId = initGoogleAdid(context);
            LogUtils.e("GoogleAdId read " + googleAdId);
            adid = googleAdId;
            onDeviceIdRead.onGoogleAdIdRead(-1, googleAdId);
            return;
        }

        LogUtils.d("GoogleAdId is reading in the foreground");
        new AsyncTask<Context, Void, String>() {
            @Override
            protected String doInBackground(Context... params) {
                Context innerContext = params[0];
                String innerResult = initGoogleAdid(innerContext);
                LogUtils.d("GoogleAdId read " + innerResult);
                return innerResult;
            }

            @Override
            protected void onPostExecute(String playAdiId) {
                if (TextUtils.isEmpty(playAdiId)) {
                    onDeviceIdRead.onGoogleAdIdRead(-1, "Failed to connect to Google Service Framework, or Google Service Framework is unavailable");
                } else {
                    adid = playAdiId;
                    onDeviceIdRead.onGoogleAdIdRead(0, playAdiId);
                }
            }
        }.execute(context);
    }

    private static String initGoogleAdid(Context context) {
        String googleAdId = null;
        try {
            GooglePlayServicesClient.GooglePlayServicesInfo gpsInfo = GooglePlayServicesClient.getGooglePlayServicesInfo(context, ONE_SECOND * 11);
            googleAdId = gpsInfo.getGpsAdid();
            if (googleAdId == null) {
                Object advertisingInfoObject = getAdvertisingInfoObject(context, ONE_SECOND * 11);

                if (advertisingInfoObject != null) {
                    googleAdId = getPlayAdId(context, advertisingInfoObject, ONE_SECOND);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return googleAdId;
    }
}
