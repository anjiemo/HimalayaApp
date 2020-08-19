package com.smart.himalaya;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smart.himalaya.base.BaseActivity;
import com.smart.himalaya.config.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class CrashActivity extends AppCompatActivity {

    private CaocConfig mConfig;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mConfig = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        if (mConfig == null) {
            // 这种情况永远不会发生，只要完成该活动就可以避免递归崩溃
            finish();
        }
    }

    @OnClick({R.id.btn_crash_restart, R.id.btn_crash_make_complaints, R.id.btn_crash_log})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crash_restart:
                CustomActivityOnCrash.restartApplication(CrashActivity.this, mConfig);
                break;
            case R.id.btn_crash_make_complaints:
                Intent intent = new Intent(this, MakeComplaintsActivity.class);
                intent.putExtra(Constants.STR_URL, Constants.MAKE_COMPLAINTS_URL);
                startActivity(intent);
                break;
            case R.id.btn_crash_log:
                if (mDialog == null) {
                    mDialog = new AlertDialog.Builder(CrashActivity.this)
                            .setTitle(R.string.crash_error_details)
                            .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()))
                            .setPositiveButton(R.string.crash_close, null)
                            .setNeutralButton(R.string.crash_copy_log, (dialog, which) -> copyErrorToClipboard())
                            .create();
                }
                mDialog.show();
                TextView textView = mDialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 复制报错信息到剪贴板
     */
    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent());
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(getString(R.string.crash_error_info), errorInformation));
    }
}