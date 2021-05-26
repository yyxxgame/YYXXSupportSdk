package cn.yyxx.support;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.logging.Logger;

import cn.yyxx.support.hawkeye.LogUtils;

/**
 * @author #Suyghur.
 * Created on 2021/04/22
 */
public class HostModelUtils {
    private HostModelUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    //主机环境：1.dev；2.test；3.online
    public static final int ENV_DEV = 1;
    public static final int ENV_TEST = 2;
    public static final int ENV_ONLINE = 3;

    public static void setHostModel(Context context, int ipModel) {
        SharedPreferences sp = context.getSharedPreferences("yyxx_host_model", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("host_model", ipModel);
        editor.commit();
    }

    public static int getHostModel(Context context) {
        SharedPreferences sp = context.getSharedPreferences("yyxx_host_model", Context.MODE_PRIVATE);
        //默认线上环境
        return sp.getInt("host_model", 3);
    }
}
