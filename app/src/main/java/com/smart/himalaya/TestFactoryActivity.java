package com.smart.himalaya;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.smart.himalaya.base.BaseActivity;
import com.smart.himalaya.callback.ISkinChangingCallback;
import com.smart.himalaya.utils.SkinManager;

import java.io.File;

/**
 * 测试插件换肤用的Activity
 */
public class TestFactoryActivity extends AppCompatActivity {

    private String mSkinPluginPath = Environment.getExternalStorageDirectory() + File.separator + "HimalayaPlugin.apk";
    private String mSkinPluginPkg = "com.example.himalyaplugin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflaterCompat.setFactory2(inflater, new LayoutInflater.Factory2() {
            @Nullable
            @Override
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                if (name.equals("TextView")) {
                    return new Button(context, attrs);
                }
                return null;
            }

            @Nullable
            @Override
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return null;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_factory);
        SkinManager.getInstance().changeSkin(mSkinPluginPath, mSkinPluginPkg, new ISkinChangingCallback() {

            @Override
            public void onStart() {

            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showShort("换肤失败!");
            }

            @Override
            public void onComplete() {
                ToastUtils.showShort("换肤成功!");
            }
        });
    }
}