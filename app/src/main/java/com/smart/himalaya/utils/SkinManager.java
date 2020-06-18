package com.smart.himalaya.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.blankj.utilcode.util.LogUtils;
import com.smart.himalaya.attr.SkinView;
import com.smart.himalaya.callback.ISkinChangedListener;
import com.smart.himalaya.callback.ISkinChangingCallback;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class SkinManager {

    private Context mContext;
    private static SkinManager sInstance = null;
    private ResourcesManager mResourcesManager;
    private List<ISkinChangedListener> mListeners = new ArrayList<>();
    private Map<ISkinChangedListener, List<SkinView>> mSkinViewMaps = new HashMap<>();
    private PrefUtils mPrefUtils;
    private String mCurrentPath;
    private String mCurrentPkg;
    private String mSuffix;

    private SkinManager() {

    }

    public static SkinManager getInstance() {
        if (sInstance == null) {
            synchronized (SkinManager.class) {
                if (sInstance == null) {
                    sInstance = new SkinManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 此方法只需调用一次（在Application中调用）
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPrefUtils = new PrefUtils(mContext);
        try {
            String pluginPath = mPrefUtils.getPluginPath();
            String pluginPkg = mPrefUtils.getPluginPkg();
            mSuffix = mPrefUtils.getSuffix();
            File file = new File(pluginPath);
            if (file.exists()) {
                loadPlugin(pluginPath, pluginPkg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mPrefUtils.clear();
        }
    }

    public ResourcesManager getResourcesManager() {
        if (!usePlugin()) {
            return new ResourcesManager(mContext.getResources(), mContext.getPackageName(), mSuffix);
        }
        return mResourcesManager;
    }

    /**
     * 加载插件
     *
     * @param skinPluginPath
     * @param skinPluginPkg
     * @throws Exception
     */
    private void loadPlugin(String skinPluginPath, String skinPluginPkg) throws Exception {
        if (skinPluginPath.equals(mCurrentPath) && skinPluginPkg.equals(mCurrentPkg)) {
            return;
        }
        AssetManager assetManager = AssetManager.class.newInstance();
        //获取addAssetPath方法
        Method addAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
        //调用addAssetPath方法，第一个参数是当前对象，第二个参数是插件包的路径
        addAssetPathMethod.invoke(assetManager, skinPluginPath);
        Resources superResources = mContext.getResources();
        Resources resources = new Resources(assetManager, superResources.getDisplayMetrics(), superResources.getConfiguration());
        mResourcesManager = new ResourcesManager(resources, skinPluginPkg, null);
        mCurrentPath = skinPluginPath;
        mCurrentPkg = skinPluginPkg;
    }

    /**
     * 获取皮肤视图
     *
     * @param listener
     * @return
     */
    public List<SkinView> getSkinView(ISkinChangedListener listener) {
        return mSkinViewMaps.get(listener);
    }

    /**
     * 添加皮肤视图
     *
     * @param listener
     * @param views
     */
    public void addSkinView(ISkinChangedListener listener, List<SkinView> views) {
        mSkinViewMaps.put(listener, views);
    }

    public void registerListener(ISkinChangedListener listener) {
        mListeners.add(listener);
    }

    public void unRegisterListener(ISkinChangedListener listener) {
        mListeners.remove(listener);
        mSkinViewMaps.remove(listener);
    }

    public void changeSkin(String suffix) {
        clearPluginInfo();
        mSuffix = suffix;
        mPrefUtils.saveSuffix(suffix);
        notifyChangedListener();
    }

    private void clearPluginInfo() {
        mCurrentPath = null;
        mCurrentPkg = null;
        mSuffix = null;
        mPrefUtils.clear();
    }

    /**
     * 改变皮肤
     *
     * @param skinPluginPath
     * @param skinPluginPkg
     * @param callback
     */
    public void changeSkin(String skinPluginPath, String skinPluginPkg, ISkinChangingCallback callback) {
        if (callback == null) {
            callback = ISkinChangingCallback.DEFAULT_CALLBACK;
        }
        final ISkinChangingCallback finalCallback = callback;
        finalCallback.onStart();
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    loadPlugin(skinPluginPath, skinPluginPkg);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == -1) {
                    finalCallback.onError(null);
                    return;
                }
                try {
                    notifyChangedListener();
                    finalCallback.onComplete();
                    updatePluginInfo(skinPluginPath, skinPluginPkg);
                } catch (Exception e) {
                    e.printStackTrace();
                    finalCallback.onError(e);
                }
            }
        }.execute();
    }

    /**
     * 保存插件信息
     *
     * @param path
     * @param pkg
     */
    private void updatePluginInfo(String path, String pkg) {
        mPrefUtils.savePluginPath(path);
        mPrefUtils.savePluginPkg(pkg);
    }

    /**
     * 通知更新
     */
    private void notifyChangedListener() {
        for (ISkinChangedListener listener : mListeners) {
            skinChange(listener);
            listener.onSkinChanged();
        }
    }

    /**
     * 皮肤变了
     *
     * @param listener
     */
    public void skinChange(ISkinChangedListener listener) {
        List<SkinView> skinViews = mSkinViewMaps.get(listener);
        if (skinViews == null) {
            LogUtils.d("==================skinViews is null");
            //此处必须返回，否则无法换肤成功
            return;
        }
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    public boolean needChangeSkin() {
        return usePlugin() || useSuffix();
    }

    private boolean usePlugin() {
        return mCurrentPath != null && !mCurrentPath.trim().equals("");
    }

    private boolean useSuffix() {
        return mSuffix != null && !mSuffix.trim().equals("");
    }
}
