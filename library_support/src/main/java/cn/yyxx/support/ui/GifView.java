package cn.yyxx.support.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author #Suyghur,
 * Created on 2019/07/22
 */
public class GifView extends View {

    /**
     * gif播放时间,默认1s
     */
    private static final int DEFAULT_GIF_DURATION = 1000;

    private Movie mMovie;
    /**
     * gif开始时间
     */
    private long mStartTime;
    /**
     * 当前显示的帧
     */
    private int mCurrentFrame;
    private float mMarginLeft;
    private float mMarginTop;
    private int mWidth;
    private int mHeight;
    private float mScale;
    private boolean isVisible = true;
    private volatile boolean isPause = false;


    public GifView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
            float scaleW = (float) movieWidth / (float) maxWidth;
            mScale = 1f / scaleW;
            mWidth = maxWidth;
            mHeight = (int) (movieHeight * mScale);
            setMeasuredDimension(mWidth, mHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mMarginLeft = (getWidth() - mWidth) / 2f;
        mMarginTop = (getHeight() - mHeight) / 2f;
        isVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            if (!isPause) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        isVisible = screenState == SCREEN_STATE_ON;
        invalidateView();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    /**
     * 更新当前显示进度
     */
    private void updateAnimationTime() {
        long current = android.os.SystemClock.uptimeMillis();
        // 如果第一帧，记录起始时间
        if (mStartTime == 0) {
            mStartTime = current;
        }
        // 取出动画的时长
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_GIF_DURATION;
        }
        // 算出需要显示第几帧
        mCurrentFrame = (int) ((current - mStartTime) % dur);
    }

    /**
     * 绘制图片
     *
     * @param canvas 画布
     */
    private void drawMovieFrame(Canvas canvas) {
        // 设置要显示的帧，绘制即可
        mMovie.setTime(mCurrentFrame);
        canvas.save();
        canvas.scale(mScale, mScale);
        mMovie.draw(canvas, mMarginLeft / mScale, mMarginTop / mScale);
        canvas.restore();
    }

    /**
     * 重绘
     */
    private void invalidateView() {
        if (isVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }

    /**
     * 设置gif图资源
     */
    public void setGifResource(int giftResId) {
        byte[] bytes = gif2Bytes(giftResId);
        mMovie = Movie.decodeByteArray(bytes, 0, bytes.length);
        requestLayout();
    }

    public void setGifResource(File file) {
        byte[] bytes = gif2Bytes(file);
        mMovie = Movie.decodeByteArray(bytes, 0, bytes.length);
        requestLayout();
    }

    /**
     * 手动设置 Movie对象
     *
     * @param movie Movie
     */
    public void setMovie(Movie movie) {
        this.mMovie = movie;
        requestLayout();
    }

    /**
     * 得到Movie对象
     *
     * @return Movie
     */
    public Movie getMovie() {
        return mMovie;
    }

    /**
     * 设置要显示第几帧动画
     *
     * @param frame 帧
     */
    public void setMovieTime(int frame) {
        mCurrentFrame = frame;
        invalidate();
    }

    /**
     * 设置暂停
     *
     * @param paused
     */
    public void setPaused(boolean paused) {
        this.isPause = paused;
        if (!paused) {
            mStartTime = android.os.SystemClock.uptimeMillis() - mCurrentFrame;
        }
        invalidate();
    }

    /**
     * 获取当前播放状态
     *
     * @return
     */
    public boolean getStatus() {
        return this.isPause;
    }

    private byte[] gif2Bytes(File file) {
        byte[] data = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;

        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return data;
    }


    private byte[] gif2Bytes(int resId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = getResources().openRawResource(resId);
        byte[] b = new byte[1024];
        int len;
        try {
            while ((len = is.read(b, 0, 1024)) != -1) {
                baos.write(b, 0, len);
            }
            baos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }
}
