package cn.yyxx.support.ui.scaleprogress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author #Suyghur,
 * Created on 2021/3/2
 */
public class ScaleLoadingView extends View {

    private float mDefaultSize = 0f;
    private int mBarColor = Color.GREEN;
    private int startColor = 0;
    private int endColor = 0;
    private Paint mPaint;
    private List<DataBean> mDataBeanList = null;
    private int mBarCount = 5;
    private float mBarDivideSize = 0f;
    private long mPerBarTime = 300L;

    public ScaleLoadingView(Context context, int viewSize, int divideSize, int count, int startColor, int endColor) {
        super(context);
        this.mDefaultSize = viewSize;
        this.mBarDivideSize = divideSize;
        this.mBarCount = count;
        this.startColor = startColor;
        this.endColor = endColor;
        initView(context);
    }

//    public ScaleLoadingView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public ScaleLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView(context);
//    }

    private void initView(Context context) {
//        mDefaultSize = DensityUtils.dip2px(context, 60f);
//        mBarDivideSize = DensityUtils.dip2px(context, 5f);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(mBarColor);
        LinearGradient linearGradient = new LinearGradient(0, 0, 180, 0, startColor, endColor, Shader.TileMode.MIRROR);
        mPaint.setShader(linearGradient);
        if (mDataBeanList == null) {
            mDataBeanList = new ArrayList<>();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewSize = measureMinSize(widthMeasureSpec, heightMeasureSpec, Math.round(mDefaultSize));
        setMeasuredDimension(viewSize, viewSize);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (DataBean bean : mDataBeanList) {
            bean.valueAnimator.cancel();
        }

        mDataBeanList.clear();

        for (int i = 0; i < mBarCount; i++) {
            float totalDivideSize = (mBarCount - 1) * mBarDivideSize;
            if (totalDivideSize >= w) {
                return;
            }

            float barWidth = (w - totalDivideSize) / mBarCount;
            RectF rectF = new RectF();
            rectF.left = i * (barWidth + mBarDivideSize);
            rectF.top = h / 3.0f;
            rectF.right = rectF.left + barWidth;
            rectF.bottom = h - rectF.top;

            mDataBeanList.add(new DataBean(rectF, mBarCount <= 1 ? 0 : mPerBarTime / (mBarCount - 1) * i));
        }
        for (DataBean bean : mDataBeanList) {
            bean.valueAnimator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (DataBean bean : mDataBeanList) {
            canvas.drawRect(bean.rectF, mPaint);
        }
        postInvalidateDelayed(10);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (DataBean bean : mDataBeanList) {
            bean.valueAnimator.cancel();
            bean.valueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        LogUtils.d(" --- onDetachedFromWindow ---");
        if (mDataBeanList != null) {
//            LogUtils.d(" --- onDetachedFromWindow size : " + mDataBeanList.size() + " ---");
//            for (int i = 0; i < mDataBeanList.size(); i++) {
//                LogUtils.d("cancel valueAnimator " + i);
//                if (mDataBeanList.get(i).valueAnimator != null) {
//                    mDataBeanList.get(i).valueAnimator.cancel();
//                    mDataBeanList.get(i).valueAnimator = null;
//                }
//
//            }
            for (DataBean bean : mDataBeanList) {
                if (bean.valueAnimator != null) {
                    bean.valueAnimator.cancel();
                    bean.valueAnimator = null;
                }
            }
            mDataBeanList.clear();
            mDataBeanList = null;
        }
    }

    private int measureMinSize(int widthMeasureSpec, int heightMeasureSpec, int defaultSize) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (wMode == MeasureSpec.AT_MOST || wMode == MeasureSpec.UNSPECIFIED) {
            width = defaultSize;
        }
        if (hMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.UNSPECIFIED) {
            height = defaultSize;
        }
        return Math.min(width, height);
    }

    private class DataBean {
        RectF rectF;
        ValueAnimator valueAnimator;

        DataBean(final RectF rectF, long delay) {
            this.valueAnimator = ValueAnimator.ofFloat(0.0f, 100.0f);
            valueAnimator.setRepeatCount(1);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.setDuration(mPerBarTime);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.setStartDelay(delay);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = getHeight() / 3.0f;
                    float min = v / 2.0f;
                    rectF.top = getHeight() / 2.0f - (min + v * animation.getAnimatedFraction());
                    rectF.bottom = getHeight() - rectF.top;
                }
            });

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (valueAnimator != null) {
                        valueAnimator.setStartDelay(mBarCount <= 1 ? 0 : (mPerBarTime / (mBarCount - 1)) + 500);
                        valueAnimator.start();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            this.rectF = rectF;
        }
    }
}
