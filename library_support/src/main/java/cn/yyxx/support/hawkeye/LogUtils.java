package cn.yyxx.support.hawkeye;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Array;

/**
 * @author #Suyghur.
 * Created on 4/22/21
 */
public class LogUtils {

    public static boolean DEBUG = true;
    public static Handler handler = null;

    private static final String TAG = "yyxx_support";

    public static void d(Object object) {
        d(TAG, object);
    }

    public static void d(String tag, Object object) {
        if (DEBUG) {
            print(Log.DEBUG, tag, object);
        }
    }

    public static void i(Object object) {
        i(TAG, object);
    }

    public static void i(String tag, Object object) {
        print(Log.INFO, tag, object);
    }

    public static void e(Object object) {
        e(TAG, object);
    }

    public static void e(String tag, Object object) {
        print(Log.ERROR, tag, object);
    }

    private static void print(int level, String tag, Object obj) {
        String msg;
        if (obj == null) {
            msg = "null";
        } else {
            Class<?> clz = obj.getClass();
            if (clz.isArray()) {
                StringBuilder sb = new StringBuilder(clz.getSimpleName());
                sb.append(" [ ");
                int len = Array.getLength(obj);
                for (int i = 0; i < len; i++) {
                    if (i != 0 && i != len - 1) {
                        sb.append(", ");
                    }
                    Object tmp = Array.get(obj, i);
                    sb.append(tmp);
                }
                sb.append(" ] ");
                msg = sb.toString();
            } else {
                msg = obj + "";
            }
        }

        switch (level) {
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
        }
    }

    public static void logHandler(Handler handler, String msg) {
        if (handler != null) {
            Message message = Message.obtain();
            message.what = 10001;
            message.obj = msg;
            handler.sendMessage(message);
        }
    }

    public static void logHandler(String msg) {
        if (handler != null) {
            logHandler(handler, msg);
        }
    }
}
