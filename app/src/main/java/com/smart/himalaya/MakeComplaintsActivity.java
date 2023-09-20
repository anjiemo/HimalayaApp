package com.smart.himalaya;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.smart.himalaya.base.BaseActivity;
import com.smart.himalaya.base.BaseApplication;
import com.smart.himalaya.config.Constants;
import com.smart.himalaya.utils.ImageTools;
import com.smart.himalaya.views.X5WebView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

/**
 * 吐槽界面
 */
public class MakeComplaintsActivity extends BaseActivity {

    private static final String TAG = "MakeComplaintsActivity";
    X5WebView webView;
    TitleBar titleBar;
    ProgressBar progressHorizontal;
    private String mTitle = null;
    private String mUrl;
    private android.webkit.ValueCallback<Uri> uploadMessage;
    private android.webkit.ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).init();
        setContentView(R.layout.activity_make_complaints);
        initView();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    protected void initPresenter() {
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }

            @Override
            public void onTitleClick(View v) {
                webView.reload();
            }

            @Override
            public void onRightClick(View v) {

            }
        });
        webView.setOnLongClickListener(v -> {
            WebView.HitTestResult result = webView.getHitTestResult();
            switch (result.getType()) {
                case WebView.HitTestResult.IMAGE_TYPE: // 图片类型
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片类型
                    new Thread(() -> {
                        try {
                            String message = parseQrImage(result);
                            BaseApplication.getHandler().post(() -> ToastUtils.showShort(message));
                            Log.d(TAG, "initPresenter: ============" + message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            BaseApplication.getHandler().post(() -> ToastUtils.showShort("解析异常，请重试！"));
                        }
                    }).start();
                    break;
            }
            return true;
        });
    }

    private String parseQrImage(WebView.HitTestResult result) throws Exception {
        String message = null;
        String extra = result.getExtra();
        Drawable drawable = Glide.with(this).load(extra).getFallbackDrawable();
        if (drawable != null) {
            message = QRCodeDecoder.syncDecodeQRCode(ImageTools.drawableToBitmap(drawable));
        }
        return message;
    }

    protected void initView() {
//        ImmersionBar.with(this)
//                //设置状态栏的颜色
//                .statusBarColor(R.color.white)
//                //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
//                .fitsSystemWindows(true)
//                .init();
        webView = findViewById(R.id.webView);
        titleBar = findViewById(R.id.titleBar);
        progressHorizontal = findViewById(R.id.progressHorizontal);

        initWebClient();
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(Constants.STR_URL);
        mTitle = intent.getStringExtra(Constants.STR_TITLE);
        webView.loadUrl(mUrl);
    }

    private void initWebClient() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        if (ObjectUtils.isEmpty(webView)) {
            return;
        }
        webView.setWebViewClient(new WebViewClient() {
            /**
             * 防止加载网页时调起系统浏览器
             */
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (url == null) {
                    return false;
                }
                try {
                    if (url.startsWith("weixin://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {

            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                if (TextUtils.isEmpty(mTitle)) {
                    mTitle = webView.getTitle();
                }
                titleBar.setTitle(mTitle);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                progressHorizontal.setVisibility(View.VISIBLE);
                progressHorizontal.setProgress(progress);
                if (progress == 100) {
                    progressHorizontal.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openImageChooserActivity() {
        // 调用自己的图库
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { // 处理返回的图片，并进行上传
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    /**
     * 拦截back键，改为切换到上一次浏览的网页
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ObjectUtils.isNotEmpty(webView)) {
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                // 后退网页并且拦截该事件
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
