package com.yyxx.support.demo

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import cn.yyxx.support.hawkeye.LogUtils

/**
 * @author #Suyghur.
 * Created on 2021/05/12
 */
class FloatViewService : Service() {

    private lateinit var activity: Activity
    private var floatView: FloatView? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun initFloatView(activity: Activity) {
        this.activity = activity
        LogUtils.d("init")
    }

    fun show() {
        if (floatView == null) {
            floatView = FloatView(activity)
        }

        floatView?.show()
    }

    fun hide() {
        floatView?.hide()
    }

    fun release() {
        floatView?.release()
    }


    override fun onBind(intent: Intent): IBinder {
        return FloatViewServiceBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    inner class FloatViewServiceBinder : Binder() {

        fun getService(): FloatViewService {
            return this@FloatViewService
        }
    }
}