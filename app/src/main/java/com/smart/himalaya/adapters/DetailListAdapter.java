package com.smart.himalaya.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.himalaya.R;
import com.smart.himalaya.utils.ObjectTools;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mUpdateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private Context mContext;

    @NonNull
    @Override
    public DetailListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailListAdapter.InnerHolder holder, int position) {
        //找到控件，设置数据
        View itemView = holder.itemView;
        Track track = mDetailData.get(position);
        //顺序Id
        holder.mOrderText.setText(String.valueOf(position + 1));
        //标题
        holder.mDetailItemTitle.setText(track.getTrackTitle());
        //播放次数
        holder.mDetailItemPlayCount.setText(String.valueOf(track.getPlayCount()));
        //时长
        long durationMil = track.getDuration() * 1000L;
        String duration = mDurationFormat.format(durationMil);
        holder.mDetailItemDuration.setText(duration);
        //更新日期
        String updateTimeText = mUpdateFormat.format(track.getUpdatedAt());
        holder.mDetailItemUpdateTime.setText(updateTimeText);
        //设置Item的点击事件
        itemView.setOnClickListener(v -> {
            if (ObjectTools.isNotEmpty(mOnItemClickListener)) {
                //参数需要有列表和位置
                mOnItemClickListener.onItemClick(mDetailData, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(List<Track> detailData, int position);
    }

    @Override

    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //清除原来的数据
        mDetailData.clear();
        //添加新的数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {

        private TextView mOrderText;
        private TextView mDetailItemTitle;
        private TextView mDetailItemPlayCount;
        private TextView mDetailItemDuration;
        private TextView mDetailItemUpdateTime;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mOrderText = itemView.findViewById(R.id.order_text);
            mDetailItemTitle = itemView.findViewById(R.id.detail_item_title);
            mDetailItemPlayCount = itemView.findViewById(R.id.detail_item_play_count);
            mDetailItemDuration = itemView.findViewById(R.id.detail_item_duration);
            mDetailItemUpdateTime = itemView.findViewById(R.id.detail_item_update_time);
        }
    }
}
