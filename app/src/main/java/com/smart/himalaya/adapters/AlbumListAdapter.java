package com.smart.himalaya.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smart.himalaya.R;
import com.smart.himalaya.utils.ObjectTools;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG = "AlbumListAdapter";
    private List<Album> mData = new ArrayList<>();
    private onAlbumItemLongClickListener mOnAlbumItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里是载入View
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //这里是设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {
            if (ObjectTools.isNotEmpty(mItemClickListener)) {
                int clickPosition = (Integer) v.getTag();
                mItemClickListener.onItemClick(clickPosition, mData.get(clickPosition));
            }
            Log.d(TAG, "onClick: =========" + v.getTag());
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (ObjectTools.isNotEmpty(mOnAlbumItemLongClickListener)) {
                int clickPosition = (Integer) v.getTag();
                mOnAlbumItemLongClickListener.onItemLongClick(mData.get(clickPosition));
            }
            //true表示消费了该事件
            return true;
        });
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        return mData.size();
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新一下UI。
        notifyDataSetChanged();
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到这个控件，设置数据
            //专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);
            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(digitalConversion(album.getPlayCount()));
            albumContentCountTv.setText(String.valueOf(album.getIncludeTrackCount()));
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Glide.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
            } else {
                albumCoverIv.setImageResource(R.mipmap.ximalay_logo);
            }
        }

        private String digitalConversion(long playCount) {
            return String.format("%s 万", playCount / 10000.0f);
        }
    }

    private onAlbumItemClickListener mItemClickListener = null;

    public void setAlbumItemClickListener(onAlbumItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface onAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }

    public void setOnAlbumItemLongClickListener(onAlbumItemLongClickListener onAlbumItemLongClickListener) {
        mOnAlbumItemLongClickListener = onAlbumItemLongClickListener;
    }

    /**
     * item长按的接口
     */
    public interface onAlbumItemLongClickListener {
        void onItemLongClick(Album album);
    }
}
