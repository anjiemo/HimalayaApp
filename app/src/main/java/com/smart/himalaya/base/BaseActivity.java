package com.smart.himalaya.base;

import androidx.fragment.app.FragmentActivity;

import com.smart.himalaya.utils.LogUtil;

public class BaseActivity extends FragmentActivity {
    @Override
    protected void onStart() {
        super.onStart();
        try {
//            new RuntimeException("This APP was written by 2695734816@qq.com").printStackTrace();
            LogUtil.w("Note!","================> This APP was written by 2695734816@qq.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

