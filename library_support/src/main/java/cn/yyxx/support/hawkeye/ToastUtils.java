package cn.yyxx.support.hawkeye;

import android.content.Context;

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
public class ToastUtils {

    public static boolean DEBUG = false;

    public static void toastInfo(Context context, String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void toastDebugInfo(Context context, String msg) {
        if (DEBUG) {
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
