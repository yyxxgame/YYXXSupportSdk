package com.yyxx.support.demo

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import cn.yyxx.support.ResUtils
import cn.yyxx.support.hawkeye.LogUtils
import cn.yyxx.support.ui.DragViewLayout

/**
 * @author #Suyghur.
 * Created on 2021/05/12
 */
class FloatView(context: Context) : DragViewLayout(context) {

    private var imageView: ImageView

    init {
        isClickable = true
        imageView = ImageView(context)
        imageView.setBackgroundResource(ResUtils.getResId(context, "float_icon", "drawable"))
        imageView.setOnClickListener {
            LogUtils.d("点击了DemoFloatView")
        }
        val params = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(imageView, params)
    }
}