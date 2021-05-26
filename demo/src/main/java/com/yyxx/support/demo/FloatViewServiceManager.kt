package com.yyxx.support.demo

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.yyxx.support.demo.FloatViewService.FloatViewServiceBinder

/**
 * @author #Suyghur.
 * Created on 2021/05/12
 */
class FloatViewServiceManager {

    private var mService: FloatViewService? = null

    private var mActivity: Activity? = null
    private var mIntent: Intent? = null
    private var isBindService = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            TODO("Not yet implemented")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            mService = (service as FloatViewServiceBinder).getService()
            mService?.initFloatView(mActivity!!)
        }

    }

    fun init(activity: Activity) {
        this.mActivity = activity
        if (mService == null) {
            mIntent = Intent(activity.applicationContext, FloatViewService::class.java)
            activity.applicationContext.startService(mIntent)
            activity.applicationContext.bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            isBindService = true
        }
    }

    fun attach() {
        mService?.show()
    }

    fun detach() {
        mService?.hide()
    }

    fun release() {
        mService?.release()
        if (isBindService) {
            mActivity?.apply {
                applicationContext.unbindService(serviceConnection)
                applicationContext.stopService(mIntent)
            }

        }
        mIntent = null
        mActivity = null
    }

    companion object {
        fun getInstance(): FloatViewServiceManager {
            return FloatViewServiceManagerHolder.INSTANCE
        }

        private object FloatViewServiceManagerHolder {
            val INSTANCE = FloatViewServiceManager()
        }

    }
}