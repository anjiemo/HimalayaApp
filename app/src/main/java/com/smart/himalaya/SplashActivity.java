package com.smart.himalaya;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.NotificationUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.smart.himalaya.base.BaseActivity;
import com.smart.himalaya.base.BaseApplication;
import com.smart.himalaya.beans.Execution;
import com.smart.himalaya.config.Constants;
import com.smart.himalaya.utils.ImageTools;
import com.smart.himalaya.views.ProgressView;
import com.smart.himalaya.receivers.MyPlayerReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    @BindView(R.id.ivSplash)
    ImageView ivSplash;
    @BindView(R.id.progressView)
    ProgressView progressView;
    private List<Execution> mExecutions = new ArrayList<>();
    private RemoteViews mRemoteViews;
    private Runnable mTask;

    {
        mExecutions.add(new Execution(R.id.ivNotifyPlayerModeSwitch, Constants.STAR, "收藏"));
        mExecutions.add(new Execution(R.id.ivNotifyPlayPre, Constants.PLAY_PRE, "打开上一首"));
        mExecutions.add(new Execution(R.id.ivNotifyPlayOrPause, Constants.PLAY_OR_PAUSE, "播放或者暂停"));
        mExecutions.add(new Execution(R.id.ivNotifyPlayerNext, Constants.PLAY_NEXT, "打开下一首"));
        mExecutions.add(new Execution(R.id.ivNotifyClose, Constants.PLAY_CLOSE, "关闭播放器"));
        mExecutions.add(new Execution(R.id.rlNotifyLayout, Constants.OPEN_HOME, "打开首页"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).init();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
    }

    private int progress = 0;

    private void initView() {
        Glide.with(this).load(R.drawable.img_splash).into(ivSplash);
        List<String> permissions = ArrayUtils.asArrayList(Permission.Group.STORAGE);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Permission.READ_PHONE_STATE);
        XXPermissions.with(this).permission(permissions).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                progress = 0;
                mTask = new Runnable() {
                    @Override
                    public void run() {
                        if (progress < ProgressView.FINISH_CODE) {
                            mHandler.postDelayed(this, 24);
                        } else {
                            departures();
                        }
                        progressView.setProgress(progress);
                        progress++;
                    }
                };
                mHandler.post(mTask);
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                ToastUtils.showLong("您拒绝了权限，部分功能将无法正常使用！");
                departures();
            }
        });
        NotificationUtils.ChannelConfig channelConfig = new NotificationUtils
                .ChannelConfig("小米", "用于在小米手机上测试的通知", NotificationUtils.IMPORTANCE_MIN);
        NotificationUtils.notify(1233, channelConfig, builder -> {
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setContent(getRemoteView());
        });
    }

    @Override
    public void onDetachedFromWindow() {
        mHandler.removeCallbacks(mTask);
        isJump = true;
    }

    private boolean isJump = false;

    private void departures() {
        if (isJump) return;
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//        startActivity(new Intent(SplashActivity.this, TestFactoryActivity.class));
        finish();
    }

    @OnClick(R.id.progressView)
    public void onViewClicked() {
        departures();
    }

    private RemoteViews getRemoteView() {
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.item_custom_notification);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.img_app_icon);
        if (ObjectUtils.isNotEmpty(drawable)) {
            Bitmap bitmap = ImageUtils.drawable2Bitmap(drawable);
            bitmap = ImageTools.getRoundedCornerBitmap(bitmap, ConvertUtils.dp2px(20));
            mRemoteViews.setImageViewBitmap(R.id.ivNotifyLogo, bitmap);
        } else {
            mRemoteViews.setImageViewResource(R.id.ivNotifyLogo, R.drawable.img_app_icon);
        }
        mRemoteViews.setTextViewText(R.id.tvNotifyTitle, "歌曲名");
        mRemoteViews.setTextViewText(R.id.tvNotifyContent, "歌手");
        int what = 0;
        for (Execution execution : mExecutions) {
            setAction(execution.getViewId(), what++, execution.getAction());
        }
        return mRemoteViews;
    }

    /**
     * 设置View的点击响应事件
     *
     * @param viewId View的资源id
     * @param what   点击之后需要干什么
     * @param action 点击之后的具体操作
     */
    private void setAction(int viewId, int what, String action) {
        mRemoteViews.setOnClickPendingIntent(viewId, getClickPendingIntent(what, action));
    }

    /**
     * 获取点击自定义通知栏上面的按钮或者布局时的延迟意图
     *
     * @param what 要执行的指令
     * @return pendingIntent
     */
    public PendingIntent getClickPendingIntent(int what, String action) {
        Intent intent = new Intent(this, MyPlayerReceiver.class);
        intent.setAction(action);
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        return PendingIntent.getBroadcast(this, what, intent, flag);
    }
}