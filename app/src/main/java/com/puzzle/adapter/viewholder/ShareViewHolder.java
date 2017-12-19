package com.puzzle.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzzle.R;
import com.puzzle.bean.Share;
import com.puzzle.ui.PuzzleApplication;

/**
 * Created by hyc on 2017/12/20 00:31
 */

public class ShareViewHolder extends RecyclerView.ViewHolder {

    TextView  tv_share_name;
    TextView  tv_share_time;
    ImageView iv_share_url;
    TextView  tv_share_content;

    public ShareViewHolder(View itemView) {
        super(itemView);
        tv_share_name = itemView.findViewById(R.id.tv_share_name);
        tv_share_time = itemView.findViewById(R.id.tv_share_time);
        iv_share_url = itemView.findViewById(R.id.iv_share_url);
        tv_share_content = itemView.findViewById(R.id.tv_share_content);
    }

    public void load(Share share){
        tv_share_name.setText(share.getUser().getUsername());
        tv_share_time.setText(share.getUser().getCreatedAt());
        Glide.with(PuzzleApplication.getContext()).load(share.getImageUrl())
                .into(iv_share_url);
        tv_share_content.setText(share.getContent());
    }
}
