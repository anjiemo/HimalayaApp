package com.smart.himalaya;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.smart.himalaya.adapters.IndicatorAdapter;
import com.smart.himalaya.adapters.MainContentAdapter;
import com.smart.himalaya.base.BaseActivity;
import com.smart.himalaya.base.BaseApplication;
import com.smart.himalaya.interfaces.IPlayerCallback;
import com.smart.himalaya.presenters.PlayerPresenter;
import com.smart.himalaya.presenters.RecommendPresenter;
import com.smart.himalaya.config.Constants;
import com.smart.himalaya.utils.LogUtil;
import com.smart.himalaya.utils.ObjectTools;
import com.smart.himalaya.views.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends BaseActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private LinearLayout mPlayControlItem;
    private View mSearchBtn;
    private View mFeedbackBtn;
    private List<String> mPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
        initPresenter();
    }

    private void initData() {
        mPermissions = ArrayUtils.asArrayList(Permission.Group.STORAGE);
        mPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        mPermissions.add(Permission.READ_PHONE_STATE);
    }

    private void initPermissions() {
        XXPermissions.with(this).permission(mPermissions)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            jump();
                        } else {
                            ToastUtils.showShort("您还没有授予权限！");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            LogUtils.d("noPermission: ============权限被永久拒绝了！");
                        }
                        XXPermissions.gotoPermissionSettings(BaseApplication.getAppContext());
                    }
                });
    }

    private void jump() {
        Intent intent = new Intent(this, MakeComplaintsActivity.class);
        intent.putExtra(Constants.STR_TITLE, "喜马拉雅FM - By QQ：2695734816");
        intent.putExtra(Constants.STR_URL, Constants.MAKE_COMPLAINTS_URL);
        startActivity(intent);
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClickListener(index -> {
            Log.d(TAG, "onTabClick: ==========" + "click index is ====>" + index);
            if (ObjectTools.isNotEmpty(mContentPager)) {
                mContentPager.setCurrentItem(index);
            }
        });
        mPlayControl.setOnClickListener(v -> {
            if (ObjectTools.isNotEmpty(mPlayerPresenter)) {
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    //没有设置过播放列表，我们就播放默认的第一个推荐专辑
                    //第一个推荐专辑，每天都会变的
                    playFirstRecommend();
                } else {
                    if (mPlayerPresenter.isPlaying()) {
                        mPlayerPresenter.pause();
                    } else {
                        mPlayerPresenter.play();
                    }
                }
            }
        });
        mPlayControlItem.setOnClickListener(v -> {
            boolean hasPlayList = mPlayerPresenter.hasPlayList();
            if (!hasPlayList) {
                playFirstRecommend();
            }
            //跳转到播放器界面
            startActivity(new Intent(this, PlayerActivity.class));
        });
        mFeedbackBtn.setOnClickListener(v -> initPermissions());
        mSearchBtn.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
    }

    /**
     * 播放第一个推荐的内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (ObjectTools.isNotEmpty(currentRecommend)) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        MagicIndicator magicIndicator = findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);//设置调整模式(自我调节，平分位置)
        commonNavigator.setAdapter(mIndicatorAdapter);
        //ViewPager
        mContentPager = findViewById(R.id.content_pager);
        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager, FragmentPagerAdapter.POSITION_NONE);
        mContentPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定到一起
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mContentPager);
        //播放控制相关的
        mRoundRectImageView = findViewById(R.id.main_track_cover);
        mHeaderTitle = findViewById(R.id.main_head_title);
        mSubTitle = findViewById(R.id.main_sub_title);
        mPlayControl = findViewById(R.id.main_play_control);
        mPlayControlItem = findViewById(R.id.main_play_control_item);
        //反馈
        mFeedbackBtn = findViewById(R.id.feedback_btn);
        //搜索
        mSearchBtn = findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ObjectTools.isNotEmpty(mPlayerPresenter)) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);

    }

    private void updatePlayControl(boolean isPlaying) {
        if (ObjectTools.isNotEmpty(mPlayControl)) {
            mPlayControl.setImageResource(isPlaying ? R.drawable.selector_player_pause : R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayError(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoad(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (ObjectTools.isNotEmpty(track)) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            LogUtil.d(TAG, "trackTitle --- > " + trackTitle);
            if (ObjectTools.isNotEmpty(mHeaderTitle)) {
                mHeaderTitle.setText(trackTitle);
            }
            LogUtil.d(TAG, "nickname --- > " + nickname);
            if (ObjectTools.isNotEmpty(mSubTitle)) {
                mSubTitle.setText(nickname);
            }
            LogUtil.d(TAG, "coverUrlMiddle --- > " + coverUrlMiddle);
            Glide.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
