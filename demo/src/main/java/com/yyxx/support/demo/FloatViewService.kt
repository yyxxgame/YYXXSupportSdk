package com.yyxx.support.demo

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import cn.yyxx.support.ResUtils
import cn.yyxx.support.hawkeye.LogUtils
import cn.yyxx.support.ui.floating.FloatItem
import cn.yyxx.support.ui.floating.FloatLogoMenu
import cn.yyxx.support.ui.floating.FloatMenuView

/**
 * @author #Suyghur.
 * Created on 2021/05/12
 */
class FloatViewService : Service() {

    private lateinit var activity: Activity
    private var floatView: FloatLogoMenu? = null

    override fun onCreate() {
        super.onCreate()
    }


    fun initFloatView(activity: Activity) {
        this.activity = activity
        LogUtils.d("init")
    }

    fun show() {
        if (floatView == null) {
            val features = mutableListOf(
                FloatItem(
                    "aaaa", "#1DB1AD",
                    "#000000", BitmapFactory.decodeResource(activity.resources, ResUtils.getResId(activity, "float_icon", "drawable"))
                ),
                FloatItem(
                    "aaaa", "#1DB1AD",
                    "#000000", BitmapFactory.decodeResource(activity.resources, ResUtils.getResId(activity, "float_icon", "drawable"))
                ),
                FloatItem(
                    "aaaa", "#1DB1AD",
                    "#000000", BitmapFactory.decodeResource(activity.resources, ResUtils.getResId(activity, "float_icon", "drawable"))
                ),
                FloatItem(
                    "aaaa", "#1DB1AD",
                    "#000000", BitmapFactory.decodeResource(activity.resources, ResUtils.getResId(activity, "float_icon", "drawable"))
                )
            )
            floatView = FloatLogoMenu.Builder()
                .withActivity(activity)
                .logo(BitmapFactory.decodeResource(activity.resources, ResUtils.getResId(activity, "float_icon", "drawable")))
                .drawCicleMenuBg(true)
                .backMenuColor("#FFFFFF")
                .setBgDrawable(activity.resources.getDrawable(ResUtils.getResId(activity, "yyxx_float_menu_bg", "drawable")))
                //这个背景色需要和logo的背景色一致
                .setFloatItems(features)
                .defaultLocation(FloatLogoMenu.LEFT)
                .drawRedPointNum(false)
                .showWithListener(object : FloatMenuView.OnMenuClickListener {
                    override fun onItemClick(position: Int, title: String?) {
                    }

                    override fun dismiss() {
                    }

                })
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