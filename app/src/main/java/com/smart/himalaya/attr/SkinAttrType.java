package com.smart.himalaya.attr;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.himalaya.utils.ResourcesManager;
import com.smart.himalaya.utils.SkinManager;

public enum SkinAttrType {

    BACKGROUND("background") {
        @Override
        public void apply(View view, String resName) {
            Drawable drawable = getResourcesManager().getDrawableByName(resName);
            if (drawable != null) {
                view.setBackgroundDrawable(drawable);
            }
        }
    },
    SRC("src") {
        @Override
        public void apply(View view, String resName) {
            Drawable drawable = getResourcesManager().getDrawableByName(resName);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
                }
            }
        }
    },
    TEXT_COLOR("textColor") {
        @Override
        public void apply(View view, String resName) {
            ColorStateList colorStateList = getResourcesManager().getColorByResName(resName);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (colorStateList != null) {
                    textView.setTextColor(colorStateList);
                }
            }
        }
    };

    private String mResType;

    public String getResType() {
        return mResType;
    }

    SkinAttrType(String type) {
        mResType = type;
    }

    public abstract void apply(View view, String resName);

    public ResourcesManager getResourcesManager() {
        return SkinManager.getInstance().getResourcesManager();
    }
}
