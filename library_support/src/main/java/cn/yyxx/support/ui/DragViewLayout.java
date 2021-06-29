package cn.yyxx.support.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author #Suyghur.
 * Created on 2021/05/12
 */
public class DragViewLayout extends LinearLayout {

    //view所在位置
    private int mLastX, mLastY;

    //屏幕宽高
    private int mScreenWidth, mScreenHeight;

    //view宽高
    protected int mWidth, mHeight;

    //是否在拖拽过程中
    protected boolean isDrag = false;

    //系统最新滑动距离
    private int mTouchSlop = 0;

    private WindowManager.LayoutParams floatLayoutParams;
    private WindowManager mWindowManager;


    //手指触摸位置
    private float xInScreen;
    private float yInScreen;
    private float xInView;
    private float yInView;

    public DragViewLayout(Context context) {
        super(context);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        floatLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.FIRST_SUB_WINDOW,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.RGBA_8888);
        floatLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        floatLayoutParams.x = 0;
        floatLayoutParams.y = (int) (mScreenHeight * 0.4);
        mWindowManager.addView(this, floatLayoutParams);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                yInView = event.getY();
                xInView = event.getX();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                //移动量大于阈值才判定为正在移动
                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
                    isDrag = true;
                }
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                //拖拽时调用WindowManager updateViewLayout更新悬浮球位置
                updateFloatPosition(false);
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //执行贴边
                    startAnim();
                    isDrag = false;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mWidth == 0) {
            //获取浮窗宽高
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    protected void updateFloatPosition(boolean isUp) {
        int x = (int) (xInScreen - xInView);
        int y = (int) (yInScreen - yInView);
        if (isUp) {
            x = isRightFloat() ? mScreenWidth : 0;
        }
        if (y > mScreenHeight - mHeight) {
            y = mScreenHeight - mHeight;
        }
        floatLayoutParams.x = x;
        floatLayoutParams.y = y;
        //更新位置
        mWindowManager.updateViewLayout(this, floatLayoutParams);
    }

    protected boolean isRightFloat() {
        return xInScreen > mScreenWidth / 2.0f;
    }

    protected void startAnim() {
        ValueAnimator valueAnimator;
        if (floatLayoutParams.x < mScreenWidth / 2) {
            valueAnimator = ValueAnimator.ofInt(floatLayoutParams.x, 0);
        } else {
            valueAnimator = ValueAnimator.ofInt(floatLayoutParams.x, mScreenWidth - mWidth);
        }
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                floatLayoutParams.x = (int) animation.getAnimatedValue();
                mWindowManager.updateViewLayout(DragViewLayout.this, floatLayoutParams);
            }
        });
        valueAnimator.start();
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public void release() {
        mWindowManager.removeView(this);
    }

}
