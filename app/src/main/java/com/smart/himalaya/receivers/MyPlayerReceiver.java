package com.smart.himalaya.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.smart.himalaya.MainActivity;
import com.smart.himalaya.config.Constants;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

public class MyPlayerReceiver extends BroadcastReceiver {

    public MyPlayerReceiver() {
        LogUtils.d("===============> MyPlayerReceiver init");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ObjectUtils.isEmpty(intent) || ObjectUtils.isEmpty(intent.getAction())) return;
        String action = intent.getAction();
        XmPlayerManager playerManager = XmPlayerManager.getInstance(context);
        switch (action) {
            case Constants.STAR:
                //收藏
                ToastUtils.showShort("收藏");
                break;
            case Constants.PLAY_PRE:
                //上一首
                playerManager.playPre();
                break;
            case Constants.PLAY_OR_PAUSE:
                //播放或者暂停
                if (playerManager.isPlaying()) {
                    playerManager.pause();
                    ToastUtils.showShort("已暂停");
                } else {
                    playerManager.play();
                    //ToastUtils.showShort("开始播放");
                }
                break;
            case Constants.PLAY_NEXT:
                //下一首
                playerManager.playNext();
                break;
            case Constants.PLAY_CLOSE:
                //关闭播放器
                playerManager.pause();
                playerManager.clearPlayCache();
                ToastUtils.showShort("清除播放缓存");
                break;
            case Constants.OPEN_HOME:
                //打开首页
                context.startActivity(new Intent(context, MainActivity.class));
                break;
            default:
                break;
        }
    }
}
