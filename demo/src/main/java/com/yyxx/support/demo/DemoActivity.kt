package com.yyxx.support.demo

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import cn.yyxx.support.AppUtils
import cn.yyxx.support.DensityUtils
import cn.yyxx.support.device.DeviceInfoUtils
import cn.yyxx.support.hawkeye.LogUtils
import cn.yyxx.support.hawkeye.OwnDebugUtils
import cn.yyxx.support.msa.MsaDeviceIdsHandler
import cn.yyxx.support.ui.scaleprogress.ScaleLoadingView
import cn.yyxx.support.volley.VolleySingleton
import cn.yyxx.support.volley.source.Response
import cn.yyxx.support.volley.source.toolbox.ImageRequest
import com.tencent.mmkv.MMKV
import kotlin.system.exitProcess


/**
 * @author #Suyghur.
 * Created on 2021/04/23
 */
class DemoActivity : Activity(), View.OnClickListener {

    private val events = mutableListOf(
        Item(0, "00 获取MSA DeviceIds"),
        Item(1, "是否安装微信"),
        Item(2, "获取网络图片"),
        Item(3, "显示浮标"),
        Item(4, "隐藏浮标"),
        Item(5, "MMKV测试 encode"),
        Item(6, "MMKV测试 decode"),
        Item(7, "权限测试")

    )

    private lateinit var textView: TextView
    private lateinit var imgView: ImageView
    private lateinit var demoFloatView: FloatView
    private val sb = StringBuilder()
    private var hasReadIds = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initDeviceInfo()
        LogUtils.d("own ${OwnDebugUtils.isOwnDebug(this, "yyxx_cfg.properties", "yyxx_game", "YYXX_OWN_DEBUG")}")
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
//        val gifView = GifView(this)
//        gifView.setGifResource(ResUtils.getResId(this, "test", "drawable"))
//        layout.addView(gifView)
        imgView = ImageView(this)
        val viewSize = DensityUtils.dip2px(this, 60f)
        val divideSize = DensityUtils.dip2px(this, 3f)
        val count = 6
        val color = Color.parseColor("#1DB1AD")
        layout.addView(ScaleLoadingView(this, viewSize, divideSize, count, Color.parseColor("#1DB1AD"), Color.parseColor("#7EB944")))
        layout.addView(imgView)
        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
        FloatViewServiceManager.getInstance().init(this)
    }

    private fun initDeviceInfo() {
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

    private fun requestImg() {
        val url = "https://i.loli.net/2019/09/16/oMtIUKWiavEbFPw.jpg"
        val request = object : ImageRequest(url,
            Response.Listener<Bitmap> {
                imgView.setImageBitmap(it)
            },
            0, 0, Bitmap.Config.ARGB_8888,
            Response.ErrorListener {
                LogUtils.e("onError")
            }
        ) {

        }
        VolleySingleton.getInstance(this.applicationContext).addToRequestQueue(this.applicationContext, request)
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
                1 -> {
                    LogUtils.d("aaaaa : ${AppUtils.isPackageInstalled(this@DemoActivity, "com.tencent.mm")}")
                }
                2 -> requestImg()
                3 -> FloatViewServiceManager.getInstance().attach()

                4 -> FloatViewServiceManager.getInstance().detach()

                5 -> {
                    MMKV.defaultMMKV()!!.encode("test", "yyxx support")
                    MMKV.defaultMMKV()!!.encode("test1", "yyxx support1")
                    MMKV.defaultMMKV()!!.encode("test2", "yyxx support2")
                    MMKV.defaultMMKV()!!.encode("test3", "yyxx support3")
                    val text =
                        "eFeiSQvEaVfyAmbsKfYpHjK/g3VFQ2lzHaLMv7f2yKXCoka0wGE6zp/4y6REvnpjspBn81Gya+yi3Q3MV3h3csxF0QA2ebKy+ytV3Lmwb5RUx/F5ps01wZ83QkVa2WpxzDG1zBaT6NxnfDXO2oL0J+6d4/E82fbEt0kwvO0KyfU="
                }
                6 -> {
                    sb.append("MMKV decode : ").append(MMKV.defaultMMKV()!!.decodeString("test"))
                    textView.text = sb.toString()
                    val keys = MMKV.defaultMMKV()!!.allKeys()
                    keys?.apply {
                        for (key in this) {
                            LogUtils.i(key)
                        }
                    }
                }
                7 -> {
                   PermissionActivity.start(this@DemoActivity)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FloatViewServiceManager.getInstance().attach()
    }

    override fun onPause() {
        super.onPause()
        FloatViewServiceManager.getInstance().detach()
    }

    override fun onDestroy() {
        super.onDestroy()
        FloatViewServiceManager.getInstance().release()
        exitProcess(0)
    }
}