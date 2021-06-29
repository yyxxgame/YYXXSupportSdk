package cn.yyxx.support;

import android.content.Context;

/**
 * @author #Suyghur.
 * Created on 2021/04/22
 */
public class ResUtils {

    private ResUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static int getResId(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public static String getResString(Context context, String name) {
        if (getResId(context, name, "string") == 0) {
            return "";
        } else {
            return context.getResources().getString(getResId(context, name, "string"));
        }
    }
}
