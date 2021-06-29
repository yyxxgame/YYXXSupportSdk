package com.yyxx.support.demo

import android.app.Application
import android.content.Context
import cn.yyxx.support.hawkeye.LogUtils
import cn.yyxx.support.msa.MsaDeviceIdsHandler
import com.tencent.mmkv.MMKV

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
class DemoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
//        MsaDeviceIdsHandler.initMsaDeviceIds(this) { code, msg, _ ->
//            LogUtils.i("initMsaDeviceIds code : $code , msg : $msg")
//        }

    }

    override fun onCreate() {
        super.onCreate()
//        val dir = MMKV.initialize(this)
//        LogUtils.i("mmkv dir : $dir")
    }
}