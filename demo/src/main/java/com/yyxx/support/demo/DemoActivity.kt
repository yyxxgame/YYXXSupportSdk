package com.yyxx.support.demo

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import cn.yyxx.support.device.DeviceInfoUtils
import cn.yyxx.support.hawkeye.LogUtils
import cn.yyxx.support.msa.MsaDeviceIdsHandler

/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
class DemoActivity : Activity(), View.OnClickListener {

    private val events = mutableListOf(
            Item(0, "00 获取MSA DeviceIds"),
            Item(1, "111111"),
            Item(2, "222222"),
            Item(3, "333333")
    )

    private lateinit var textView: TextView
    private val sb = StringBuilder()
    private var hasReadIds = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initDeviceInfo()
    }

    private fun initView() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        textView = TextView(this)
        layout.addView(textView)
        for (event in events) {
            with(Button(this)) {
                id = event.id
                tag = event.id
                text = event.name
                setOnClickListener(this@DemoActivity)
                layout.addView(this)
            }
        }
        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun initDeviceInfo() {
        LogUtils.d("initDeviceInfo")
        sb.append("Android ID : ").append(DeviceInfoUtils.getAndroidDeviceId(this)).append("\n")
        sb.append("手机制造商 : ").append(DeviceInfoUtils.getDeviceManufacturer()).append("\n")
        sb.append("手机品牌 : ").append(DeviceInfoUtils.getDeviceBrand()).append("\n")
        sb.append("手机型号 : ").append(DeviceInfoUtils.getDeviceModel()).append("\n")
        sb.append("CPU核数 : ").append(DeviceInfoUtils.getCpuCount()).append("\n")
        sb.append("CPU架构 : ").append(DeviceInfoUtils.getCpuAbi()).append("\n")
        sb.append("本机运行内存Ram : ").append(DeviceInfoUtils.getDeviceRam()).append("\n")
        sb.append("本应用可用运行内存Ram : ").append(DeviceInfoUtils.getAppAvailRam(this)).append("\n")
        textView.text = sb.toString()
    }

    override fun onClick(v: View?) {
        v?.apply {
            when (tag as Int) {
                0 -> {
                    if (!hasReadIds) {
                        sb.append("OAID : ").append(MsaDeviceIdsHandler.oaid).append("\n")
                        sb.append("VAID : ").append(MsaDeviceIdsHandler.vaid).append("\n")
                        sb.append("AAID : ").append(MsaDeviceIdsHandler.aaid).append("\n")
                        textView.text = sb.toString()
                        hasReadIds = true
                    }
                }
            }
        }
    }
}