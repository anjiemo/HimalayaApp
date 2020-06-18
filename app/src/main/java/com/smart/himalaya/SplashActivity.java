package com.smart.himalaya;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.smart.himalaya.base.BaseActivity;
import com.smart.himalaya.base.BaseApplication;
import com.smart.himalaya.views.ProgressView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.ivSplash)
    ImageView ivSplash;
    @BindView(R.id.progressView)
    ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).init();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Glide.with(this).load(R.drawable.img_splash).into(ivSplash);
        List<String> permissions = ArrayUtils.asArrayList(Permission.Group.STORAGE);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Permission.READ_PHONE_STATE);
        XXPermissions.with(this).permission(permissions).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    int progress = 0;

                    @Override
                    public void run() {
                        BaseApplication.getHandler().post(() -> {
                            if (progress > ProgressView.FINISH_CODE) {
                                return;
                            }
                            BaseApplication.getHandler().post(() -> progressView.setProgress(progress));
                            progress++;
                        });
                    }
                }, 0, 20);
                progressView.setOnProgressChangedListener(currentProgress -> {
                    if (currentProgress == ProgressView.FINISH_CODE) {
                        departures();
                    }
                });
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                ToastUtils.showLong("您拒绝了权限，部分功能将无法正常使用！");
            }
        });
    }

    private void departures() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//        startActivity(new Intent(SplashActivity.this, TestFactoryActivity.class));
        finish();
    }

    @OnClick(R.id.progressView)
    public void onViewClicked() {
        departures();
    }
}