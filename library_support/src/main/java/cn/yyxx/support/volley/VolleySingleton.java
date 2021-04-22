package cn.yyxx.support.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import cn.yyxx.support.volley.source.Request;
import cn.yyxx.support.volley.source.RequestQueue;
import cn.yyxx.support.volley.source.toolbox.ImageLoader;
import cn.yyxx.support.volley.source.toolbox.Volley;

/**
 * @author #Suyghur.
 * Created on 2021/04/22
 */
public class VolleySingleton {

    private volatile static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    private final ImageLoader imageLoader;

    private VolleySingleton(Context context) {
        requestQueue = getRequestQueue(context);
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VolleySingleton.class) {
                if (mInstance == null) {
                    mInstance = new VolleySingleton(context);
                }
            }
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Context context, Request<T> request) {
        getRequestQueue(context).add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
