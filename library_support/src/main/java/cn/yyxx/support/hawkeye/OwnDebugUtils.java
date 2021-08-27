package cn.yyxx.support.hawkeye;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import cn.yyxx.support.FileUtils;
import cn.yyxx.support.PropertiesUtils;

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
public class OwnDebugUtils {

    private static final String OWN_DEBUG_FILE = "own.txt";

    public static boolean isOwnDebug(Context context, String fileName, String folderName, String key) {
        //先判断配置文件
        if (getOwnDebugFromCfg(context, fileName, folderName, key)) {
            return true;
        }
        //配置文件中关闭时再判断私有目录
        String ownFileName = context.getExternalFilesDir(null).getAbsolutePath() + "/" + OWN_DEBUG_FILE;
        File file = new File(ownFileName);
        if (!file.exists()) {
            return false;
        }
        String data = FileUtils.readFile(ownFileName);
        if (!TextUtils.isEmpty(data)) {
            return data.trim().equals("own");
        } else {
            return false;
        }

    }

    public static boolean getOwnDebugFromCfg(Context context, String fileName, String folderName, String key) {
        String value = PropertiesUtils.getValue4Properties(context, fileName, folderName, key);
        LogUtils.d("own value : " + value);
        return !TextUtils.isEmpty(value) && Boolean.parseBoolean(value);
    }

}
