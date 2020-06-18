package com.smart.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart.himalaya.R;

public class PlayControllerView extends LinearLayout {

    private FrameLayout mFlRecord;
    private ImageView mIvBaton;
    private RotateAnimation mStartPlayAnim;
    private RotateAnimation mStopPlayAnim;
    private int duration = 0;

    public PlayControllerView(@NonNull Context context) {
        super(context);
        init();
    }

    public PlayControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_controller_play, this,false);
        addView(view);
        mIvBaton = view.findViewById(R.id.ivBaton);
        mFlRecord = view.findViewById(R.id.flRecord);
        initStartPlayAnim();
        initStopPlayAnim();
    }

    /**
     * 唱片开始旋转
     */
    private void onRotate() {
        long multiple = 100000L;
        RotateAnimation rotateAnim = new RotateAnimation(0, 360 * multiple, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(6000 * multiple);
        rotateAnim.setRepeatCount(Animation.INFINITE);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setRepeatMode(Animation.RESTART);
        mFlRecord.startAnimation(rotateAnim);
    }

    /**
     * 初始化播放时，指挥棒的动画
     */
    private void initStartPlayAnim() {
        mStartPlayAnim = new RotateAnimation(-22, 0, Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF, 0.0f);
        mStartPlayAnim.setRepeatCount(0);
        mStartPlayAnim.setFillAfter(true);
        mStartPlayAnim.setRepeatMode(Animation.RESTART);
    }

    /**
     * 初始化暂停时，指挥棒的动画
     */
    private void initStopPlayAnim() {
        mStopPlayAnim = new RotateAnimation(0, -22, Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF, 0.0f);
        mStopPlayAnim.setRepeatCount(0);
        mStopPlayAnim.setFillAfter(true);
        mStopPlayAnim.setRepeatMode(Animation.RESTART);
    }

    private boolean isPlaying = false;

    public PlayControllerView setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        if (playing) {
            mStartPlayAnim.setDuration(getDuration());
            onStartPlaying();
        } else {
            mStopPlayAnim.setDuration(getDuration());
            onPausePlaying();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * 执行播放的动画
     */
    private void onStartPlaying() {
        mIvBaton.startAnimation(mStartPlayAnim);
    }

    /**
     * 执行暂停的动画
     */
    private void onPausePlaying() {
        mIvBaton.startAnimation(mStopPlayAnim);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onRotate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //从window中解绑了，清除动画
        mIvBaton.clearAnimation();
        mFlRecord.clearAnimation();
    }
}
