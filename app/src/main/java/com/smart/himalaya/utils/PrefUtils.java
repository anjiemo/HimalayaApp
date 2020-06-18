package com.smart.himalaya.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.smart.himalaya.config.Constants;

public class PrefUtils {

    private Context mContext;

    public PrefUtils(Context context) {
        mContext = context;
    }

    public void savePluginPath(String path) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(Constants.KEY_PLUGIN_PATH, path).apply();
    }

    public void savePluginPkg(String pkg) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(Constants.KEY_PLUGIN_PKG, pkg).apply();
    }

    public String getPluginPath() {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(Constants.KEY_PLUGIN_PATH, "");
    }

    public String getPluginPkg() {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(Constants.KEY_PLUGIN_PKG, "");
    }

    public void saveSuffix(String suffix) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(Constants.KEY_PLUGIN_SUFFIX, suffix).apply();
    }

    public String getSuffix() {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(Constants.KEY_PLUGIN_SUFFIX, "");
    }

    public void clear() {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
}
