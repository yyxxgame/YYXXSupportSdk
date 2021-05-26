package cn.yyxx.support.encryption;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author #Suyghur,
 * Created on 2021/1/27
 */
public class GzipUtils {

    public static String compress(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        ByteArrayOutputStream bos = null;
        GZIPOutputStream gos = null;

        try {
            //创建一个新的输出流
            bos = new ByteArrayOutputStream();
            //使用默认缓冲区创建新的输出流
            gos = new GZIPOutputStream(bos);
            //将字节写入
            gos.write(str.getBytes("UTF-8"));
            return bos.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gos != null) {
                try {
                    gos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static String deCompress(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        GZIPInputStream gis = null;
        try {
            bos = new ByteArrayOutputStream();
            bis = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            gis = new GZIPInputStream(bis);
            byte[] buffer = new byte[256];
            int count = 0;
            while ((count = gis.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
            }
            return bos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gis != null) {
                try {
                    gis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }
}
