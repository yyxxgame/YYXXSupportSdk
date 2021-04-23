package com.yyxx.support.demo

import android.app.Application
import android.content.Context
import cn.yyxx.support.hawkeye.LogUtils
import cn.yyxx.support.msa.MsaDeviceIdsHandler

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
class DemoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MsaDeviceIdsHandler.initMsaDeviceIds(this) { code, msg, ids ->
            LogUtils.i("initMsaDeviceIds code : $code , msg : $msg")
//            if (code == 0) {
//                LogUtils.d("oaid : ${ids["oaid"]}")
//                LogUtils.d("vaid : ${ids["vaid"]}")
//                LogUtils.d("aaid : ${ids["aaid"]}")
//            }
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}