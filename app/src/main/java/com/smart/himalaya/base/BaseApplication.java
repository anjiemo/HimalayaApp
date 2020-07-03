package com.smart.himalaya.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.blankj.utilcode.util.LogUtils;
import com.smart.himalaya.db.DaoMaster;
import com.smart.himalaya.db.DaoSession;
import com.smart.himalaya.config.Constants;
import com.smart.himalaya.utils.LogUtil;
import com.smart.himalaya.utils.SkinManager;
import com.smart.himalaya.receivers.MyPlayerReceiver;
import com.tencent.smtt.sdk.QbSdk;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

@SuppressLint("StaticFieldLeak")
public class BaseApplication extends Application {

    private static Handler sHandler = null;
    public static Context sContext = null;
    private static DaoSession sDaoSession = null;

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
        initHimalayaSDK();
        //初始化LogUtil
        LogUtil.init(this.getPackageName(), false);
        sHandler = new Handler();
        sContext = getApplicationContext();
        initGreenDao();
        initX5Environment();
    }

    /**
     * 初始化X5浏览器内核环境
     */
    private void initX5Environment() {
        //非wifi情况下，主动下载x5内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean isSuccess) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.d("onViewInitFinished================x5内核状态：" + isSuccess);
            }

            @Override
            public void onCoreInitFinished() {
                LogUtils.d("onCoreInitFinished================x5核心初始化完成");
            }
        };
        QbSdk.initX5Environment(this, cb);
    }

    private void initHimalayaSDK() {
        register();
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if (DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this, mAppSecret);
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this, mAppSecret);
        }
        //初始化播放器
        XmPlayerManager.getInstance(this).init();
    }

    private void register() {
        //动态注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        MyPlayerReceiver receiver = new MyPlayerReceiver();
        registerReceiver(receiver, intentFilter);
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, Constants.DB_NAME);
        SQLiteDatabase writableDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(writableDatabase);
        sDaoSession = daoMaster.newSession();
    }

    public static Context getAppContext() {
        return sContext;
    }

    public static Handler getHandler() {
        return sHandler;
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }
}
