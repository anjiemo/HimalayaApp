package com.smart.himalaya.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class ResourcesManager {

    private Resources mResources;
    private String mPkgName;
    private String mSuffix;

    public ResourcesManager(Resources resources, String pkgName, String suffix) {
        mResources = resources;
        mPkgName = pkgName;
        if (suffix == null) {
            suffix = "";
        }
        mSuffix = suffix;
    }

    public Drawable getDrawableByName(String name) {
        try {
            name = appendSuffix(name);
            return mResources.getDrawable(mResources.getIdentifier(name, "drawable", mPkgName));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String appendSuffix(String name) {
        if (!TextUtils.isEmpty(mSuffix)) {
            name += "_" + mSuffix;
        }
        return name;
    }

    public ColorStateList getColorByResName(String name) {
        try {
            name = appendSuffix(name);
            return mResources.getColorStateList(mResources.getIdentifier(name, "color", mPkgName));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
