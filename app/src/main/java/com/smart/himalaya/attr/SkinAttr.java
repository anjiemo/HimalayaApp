package com.smart.himalaya.attr;

import android.view.View;

public class SkinAttr {

    private String mResName;
    private SkinAttrType mType;

    public SkinAttr(String resName, SkinAttrType type) {
        mResName = resName;
        mType = type;
    }

    public String getResName() {
        return mResName;
    }

    public void setResName(String resName) {
        mResName = resName;
    }

    public SkinAttrType getType() {
        return mType;
    }

    public void setType(SkinAttrType type) {
        mType = type;
    }

    public void apply(View view) {
        mType.apply(view, mResName);
    }
}
