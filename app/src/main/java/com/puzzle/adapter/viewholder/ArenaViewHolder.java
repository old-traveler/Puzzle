package com.puzzle.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzzle.R;
import com.puzzle.bean.Arena;
import com.puzzle.ui.PuzzleApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hyc on 2017/12/19 15:28
 */

public class ArenaViewHolder extends RecyclerView.ViewHolder {
    TextView tv_item_time;
    TextView tv_item_integral;
    TextView tv_publish_time;
    TextView tv_item_level;
    ImageView iv_item_arena;
    public ArenaViewHolder(View itemView) {
        super(itemView);
        tv_item_time = itemView.findViewById(R.id.tv_item_time);
        tv_item_integral = itemView.findViewById(R.id.tv_item_integral);
        tv_publish_time = itemView.findViewById(R.id.tv_publish_time);
        tv_item_level = itemView.findViewById(R.id.tv_item_level);
        iv_item_arena = itemView.findViewById(R.id.iv_item_arena);
    }

    public void load(Arena arena){
        tv_item_time.setText("时间："+arena.getTime());
        tv_item_integral.setText("积分："+arena.getIntegral());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        tv_publish_time.setText(format.format(new Date(arena.getPublish_time())));
        Glide.with(PuzzleApplication.getContext()).load(arena.getImageUrl()).into(iv_item_arena);
        tv_item_level.setText("难度："+arena.getLevel());
    }
}
