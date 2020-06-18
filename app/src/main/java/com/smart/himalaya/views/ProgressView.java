package com.smart.himalaya.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by HXF on 2020/6/15.
 */
@SuppressLint("DrawAllocation")
public class ProgressView extends AppCompatImageView {
    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        measure(0, 0);
    }

    //圆圈和字体的颜色
    private int circleColor = Color.WHITE;
    //进度完成的代码
    public static final int FINISH_CODE = 100;
    //是否需要继续绘制
    private boolean mNeedExecute = false;
    //当前进度
    private int currentProgress = 0;
    //绘制速度（自带方法的绘制速度）
    private int speed = 40;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedExecute = true;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getProgress() {
        return currentProgress;
    }

    /**
     * 设置圆环的进度
     *
     * @param progress progress>=0 && progress <=100
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            return;
        }
        currentProgress = progress * 36;
        if (mOnProgressChangedListener != null) {
            mOnProgressChangedListener.onProgressChanged(currentProgress / 36);
        }
        invalidate();
    }

    /**
     * 绘制一次性的动画
     */
    public void startRawCircle() {
        int maxProgress = 3600;
        post(new Runnable() {
            @Override
            public void run() {
                currentProgress += speed;
                if (mOnProgressChangedListener != null) {
                    mOnProgressChangedListener.onProgressChanged(currentProgress / 36);
                }
                if (currentProgress <= maxProgress) {
                    if (mNeedExecute) {
                        postDelayed(this, 100);
                        invalidate();
                    }
                } else {
                    mNeedExecute = false;
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int radius = (int) (width / 2f);
        int innerC = (int) (width / 2 * 0.8f);
        int outerC = (int) (width / 2 * 0.9f);
        Paint paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        //设置防抖动
        paint.setDither(true);
        paint.setColor(getCircleColor());
        // 将画笔设置为描边
        paint.setStyle(Paint.Style.FILL);
        // 设置线条宽度
        paint.setStrokeWidth(0);
        //设置文字大小（像素为单位）
        paint.setTextSize(40);
        //设置文字居中
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(currentProgress / 36), radius * 1.01f, radius * 1.2f, paint);
//        canvas.drawCircle(radius, radius, 15f, paint);
        canvas.save();
        for (int i = 0; i < currentProgress; i++) {
            int offset = 0;
            canvas.drawLine(radius, radius - outerC, radius, radius - innerC + offset, paint);
            canvas.rotate(0.1f, radius, radius);
        }
        canvas.restore();
    }

    private int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    private DisplayMetrics getDisplayMetrics() {
        return getContext().getResources().getDisplayMetrics();
    }

    public void setOnProgressChangedListener(onProgressChangedListener onProgressChangedListener) {
        mOnProgressChangedListener = onProgressChangedListener;
    }

    private onProgressChangedListener mOnProgressChangedListener = null;

    public interface onProgressChangedListener {
        void onProgressChanged(int currentProgress);
    }
}
