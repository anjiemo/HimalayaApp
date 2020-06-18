package com.smart.himalaya.base;

import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import com.blankj.utilcode.util.LogUtils;
import com.smart.himalaya.attr.SkinAttr;
import com.smart.himalaya.attr.SkinAttrSupport;
import com.smart.himalaya.attr.SkinView;
import com.smart.himalaya.callback.ISkinChangedListener;
import com.smart.himalaya.utils.LogUtil;
import com.smart.himalaya.utils.SkinManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseActivity extends AppCompatActivity implements ISkinChangedListener {

    private static final Class<?>[] sConstructorSignature = new Class<?>[]{Context.class, AttributeSet.class};
    private static final int[] sOnClickAttrs = new int[]{android.R.attr.onClick};
    private static final String[] sClassPrefixList = {"android.widget.", "android.view.", "android.webkit."};
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();
    private final Object[] mConstructorArgs = new Object[2];
    private Method mCreateViewMethod = null;
    static final Class<?>[] sCreateViewSignature = new Class[]{View.class, String.class, Context.class, AttributeSet.class};
    private final Object[] mCreateViewArgs = new Object[4];

    @Override
    protected void onStart() {
        super.onStart();
        try {
//            new RuntimeException("This APP was written by 2695734816@qq.com \n This is my homework, no other people copy it!").printStackTrace();
//            LogUtil.w("Note!","================> This APP was written by 2695734816@qq.com \n This is my homework, no other people copy it!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SkinManager.getInstance().registerListener(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflaterCompat.setFactory(inflater, (parent, name, context, attrs) -> {
            //系统有没有使用setFactory
            //完成AppCompat factory的工作
            AppCompatDelegate delegate = getDelegate();
            View view = null;
            List<SkinAttr> skinAttrs = null;
            try {
                if (mCreateViewMethod == null) {
                    mCreateViewMethod = delegate.getClass().getMethod("createView", sCreateViewSignature);
                }
                //mCreateViewArgs
                mCreateViewArgs[0] = parent;
                mCreateViewArgs[1] = name;
                mCreateViewArgs[2] = context;
                mCreateViewArgs[3] = attrs;
                view = (View) mCreateViewMethod.invoke(delegate, mCreateViewArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            skinAttrs = SkinAttrSupport.getSkinAttrs(context, attrs);
            if (skinAttrs.isEmpty()) {
                return null;
            }
            if (view == null) {
                view = createViewFromTag(context, name, attrs);
            }
            if (view != null) {
                injectSkin(view, skinAttrs);
            }
            return view;
        });
        super.onCreate(savedInstanceState);
    }

    private void injectSkin(View view, List<SkinAttr> skinAttrs) {
        List<SkinView> skinViews = SkinManager.getInstance().getSkinView(this);
        if (skinViews == null) {
            skinViews = new ArrayList<>();
            SkinManager.getInstance().addSkinView(this, skinViews);
        }
        SkinView skinView = new SkinView(view, skinAttrs);
        skinViews.add(skinView);
        //检测当前是否需要自动换肤，如果需要则换肤
        if (SkinManager.getInstance().needChangeSkin()) {
            SkinManager.getInstance().skinChange(this);
        }
    }

    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }
        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;
            if (-1 == name.indexOf('.')) {
                for (int i = 0; i < sClassPrefixList.length; i++) {
                    final View view = createViewByPrefix(context, name, sClassPrefixList[i]);
                    if (view != null) {
                        return view;
                    }
                }
                return null;
            } else {
                return createViewByPrefix(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private View createViewByPrefix(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                Class<? extends View> clazz = Class.forName(
                        prefix != null ? (prefix + name) : name,
                        false,
                        context.getClassLoader()).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }

    @Override
    public void onSkinChanged() {
        LogUtils.d("=================" + BaseActivity.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        SkinManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }
}

