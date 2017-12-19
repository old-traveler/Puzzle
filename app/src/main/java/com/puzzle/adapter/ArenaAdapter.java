package com.puzzle.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.puzzle.R;
import com.puzzle.adapter.viewholder.ArenaViewHolder;
import com.puzzle.bean.Arena;
import com.puzzle.ui.activity.PassGameActivity;
import com.puzzle.util.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by hyc on 2017/12/19 15:27
 */

public class ArenaAdapter extends RecyclerView.Adapter<ArenaViewHolder> {

    List<Arena> list;

    WeakReference<Activity> activity;
    public ArenaAdapter(List<Arena> list, WeakReference<Activity> activity){
        this.list = list;
        this.activity = activity;
    }

    public void setList(List<Arena> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ArenaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArenaViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_arena,parent,false));
    }

    @Override
    public void onBindViewHolder(ArenaViewHolder holder, final int position) {
        holder.load(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.get(), PassGameActivity.class);
                Arena arena = list.get(position);
                intent.putExtras(CommonUtils.packIntent("arena",arena.getLevel()
                        ,arena.getImageUrl(),arena.getTime(),arena.getIntegral()));
                activity.get().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
}
