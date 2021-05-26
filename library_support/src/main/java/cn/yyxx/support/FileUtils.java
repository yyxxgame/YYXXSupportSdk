package cn.yyxx.support;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
public class FileUtils {

    private FileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将字节流转换成文件
     */
    public static void saveFile(String filepath, byte[] data) throws IOException {
        if (data != null) {
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        }
    }


    public static InputStream accessFileFromAssets(Context context, String fileName) {
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    public static InputStream accessFileFromMetaInf(Context context, String fileName) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String sourceDir = applicationInfo.sourceDir;
        ZipFile zipFile;
        InputStream in = null;
        try {
            zipFile = new ZipFile(sourceDir);
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/" + fileName)) {
                    if (entry.getSize() > 0) {
                        in = zipFile.getInputStream(entry);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    public static boolean isExistInAssets(Context context, String fileName, String path) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] fileNames = assetManager.list(path);
            if (fileNames != null && fileNames.length != 0) {
                for (String item : fileNames) {
                    if (fileName.equals(item)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    /**
     * 读取文件
     */
    public static String readFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        BufferedReader reader = null;
        FileInputStream is = null;
        StringBuffer stringBuffer = new StringBuffer();
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            try {
                String data;
                while ((data = reader.readLine()) != null) {
                    stringBuffer.append(data);
                }
            } catch (IOException e) {
                try {
                    if (reader != null) {
                        reader.close();
                        reader = null;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    if (reader != null) {
                        reader.close();
                        reader = null;
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
        }
        return stringBuffer.toString().trim();
    }

}
