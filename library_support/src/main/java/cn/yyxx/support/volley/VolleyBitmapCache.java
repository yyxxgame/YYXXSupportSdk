package cn.yyxx.support.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.yyxx.support.cache.bitmap.DiskLruCache;
import cn.yyxx.support.encryption.Md5Utils;
import cn.yyxx.support.hawkeye.LogUtils;
import cn.yyxx.support.volley.source.toolbox.ImageLoader;


/**
 * @author #Suyghur,
 * Created on 2019/09/11
 * Copyright (c) 2019 3KWan.
 */
public class VolleyBitmapCache implements ImageLoader.ImageCache {
    /**
     * 内存缓存
     */
    private LruCache<String, Bitmap> lruCache;
    /**
     * 本地缓存
     */
    private static DiskLruCache diskLruCache;
    /**
     * 本地缓存文件名
     */
    final static String DISK_CACHE_DIR = "qsgame_img";
    /**
     * 本地缓存大小
     */
    final long DISK_MAX_SIZE = 20 * 1024 * 1024;

    private static VolleyBitmapCache volleyBitmapCache = null;

    /**
     * 默认格式jpeg
     */
    private static Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;

    public static VolleyBitmapCache getVolleyBitmapCache(Context context, String diskCachePath, Bitmap.CompressFormat compressFormat) {
        if (compressFormat != null) {
            mFormat = compressFormat;
        }
        if (volleyBitmapCache == null) {
            synchronized (Object.class) {
                if (volleyBitmapCache == null) {
                    volleyBitmapCache = new VolleyBitmapCache(context, diskCachePath);
                }
            }
        }
        return volleyBitmapCache;
    }

    private VolleyBitmapCache(Context context, String diskCachePath) {
        //获取应用内存
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemory / 8;
        //初始化LruCache,设置缓存大小cacheSize
        this.lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        //本地缓存
        //如果diskCachePath为空则设置默认的路径:data/data/packageName/cache/qsgame_img
        String diskLruCachePath;
        if (TextUtils.isEmpty(diskCachePath)) {
            diskLruCachePath = context.getCacheDir().getPath() + File.separator + DISK_CACHE_DIR;
        } else {
            diskLruCachePath = diskCachePath;
        }
        LogUtils.d("cachePath : " + diskLruCachePath);
        //缓存路径
        File disLruCacheDir = new File(diskLruCachePath);
        if (!disLruCacheDir.exists()) {
            disLruCacheDir.mkdirs();
        }
        try {
            //初始化DiskLruCache,设置最大缓存大小DISK_MAX_SIZE
            diskLruCache = DiskLruCache.open(disLruCacheDir, 1, 1, DISK_MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Bitmap getBitmap(String url) {
        String key = Md5Utils.encodeByMD5(url);
        //先从内存中查找
        Bitmap bitmap = lruCache.get(url);
        if (bitmap == null) {
            //加载本地
            LogUtils.d("getBitmap 加载本地 url : " + url);
            bitmap = getBitmap4DiskLruCache(key);
            //加载到内存
            if (bitmap != null) {
                LogUtils.d("getBitmap 加载到内存 url : " + url);
                lruCache.put(url, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        lruCache.put(url, bitmap);
        String key = Md5Utils.encodeByMD5(url);
        putBitmap2DiskLruCache(key, bitmap);
    }

    /**
     * 缓存到本地
     *
     * @param key
     * @param bitmap
     */
    private void putBitmap2DiskLruCache(String key, Bitmap bitmap) {
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            if (editor != null) {
                outputStream = editor.newOutputStream(0);
                boolean compress = bitmap.compress(mFormat, 100, outputStream);
                if (compress) {
                    LogUtils.d("缓存到本地");
                    diskLruCache.flush();
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (editor != null) {
                    editor.abort();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从本地加载
     *
     * @param key
     * @return
     */
    private Bitmap getBitmap4DiskLruCache(String key) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                if (inputStream != null) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}
