package com.smart.himalaya.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.smart.himalaya.R;

@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {

    //旋转角度
    private int rotateDegree = 0;

    //是否继续旋转
    private boolean mNeedRotate = false;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图标
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        //绑定到window的时候
        post(new Runnable() {
            @Override
            public void run() {
                rotateDegree += 30;
                rotateDegree = rotateDegree <= 360 ? rotateDegree : 0;
                //会调用onDraw()方法
                invalidate();
                //是否继续旋转
                if (mNeedRotate) {
                    postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //从window中解绑了，将是否旋转置为false
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第一个参数是旋转角度
         * 第二个参数是旋转的x坐标
         * 第三个参数是旋转的y坐标
         */
        canvas.rotate(rotateDegree, getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);
    }
}
