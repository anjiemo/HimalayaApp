package com.smart.himalaya.attr;

import android.view.View;

import java.util.List;
import java.util.NoSuchElementException;

public class SkinView {

    private View mView;
    private List<SkinAttr> mAttrs;

    public SkinView(View view, List<SkinAttr> attrs) {
        if (view == null || attrs == null) {
            throw new NoSuchElementException("are you ok? Your View or attrs is empty！");
        }
        mView = view;
        mAttrs = attrs;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public List<SkinAttr> getAttrs() {
        return mAttrs;
    }

    public void setAttrs(List<SkinAttr> attrs) {
        mAttrs = attrs;
    }

    public void apply() {
        for (SkinAttr attr : mAttrs) {
            attr.apply(mView);
        }
    }
}
