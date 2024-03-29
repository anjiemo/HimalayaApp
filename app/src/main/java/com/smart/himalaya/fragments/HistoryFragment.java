package com.smart.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.smart.himalaya.PlayerActivity;
import com.smart.himalaya.R;
import com.smart.himalaya.adapters.TrackListAdapter;
import com.smart.himalaya.base.BaseApplication;
import com.smart.himalaya.base.BaseFragment;
import com.smart.himalaya.presenters.HistoryPresenter;
import com.smart.himalaya.presenters.PlayerPresenter;
import com.smart.himalaya.utils.ObjectTools;
import com.smart.himalaya.views.ConfirmCheckBoxDialog;
import com.smart.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;
import java.util.Objects;

public class HistoryFragment extends BaseFragment implements TrackListAdapter.ItemClickListener, TrackListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {

    private static final String TAG = "HistoryFragment";
    private UILoader mUiLoader;
    private HistoryPresenter mHistoryPresenter;
    private TrackListAdapter mTrackListAdapter;
    private Track mCurrentClickHistoryItem;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history, container, false);
        if (ObjectTools.isEmpty(mUiLoader)) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
        } else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        //HistoryPresenter
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mUiLoader.upDateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.loadHistories();
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container, false);
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableOverScroll(true);
        //RecyclerView
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置适配器
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongClickListener(this);
        historyList.setAdapter(mTrackListAdapter);
        return successView;
    }

    @Override
    protected void onRefresh() {
        List<Track> histories = mHistoryPresenter.getHistories();
        if (ObjectTools.isEmpty(histories)) {
            mUiLoader.upDateStatus(UILoader.UIStatus.EMPTY);
        } else {
            if (ObjectTools.isNotEmpty(mTrackListAdapter)) {
                mTrackListAdapter.setData(histories);
                mUiLoader.upDateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData,position);
        //跳转到播放器界面
        startActivity(new Intent(getActivity(), PlayerActivity.class));
    }

    @Override
    public void onItemLongClick(Track track) {
        mCurrentClickHistoryItem = track;
        //去删除历史
        //Toast.makeText(getActivity(),"历史记录长按..." + track.getTrackTitle(),Toast.LENGTH_SHORT).show();
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(Objects.requireNonNull(getActivity()));
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelClick() {
        //不用做
    }

    @Override
    public void onConfirmClick(boolean isCheck) {
        //去删除历史
        if(ObjectTools.isNotEmpty(mHistoryPresenter) && ObjectTools.isNotEmpty(mCurrentClickHistoryItem)) {
            if(!isCheck) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            } else {
                mHistoryPresenter.clearHistory();
            }
        }
        onRefresh();
    }
}
