package com.puzzle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.puzzle.R;
import com.puzzle.adapter.viewholder.ShareViewHolder;
import com.puzzle.bean.Share;

import java.util.List;

/**
 * Created by hyc on 2017/12/20 00:31
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareViewHolder> {

    List<Share> list;

    public void setList(List<Share> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShareViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_share,parent,false));
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
            holder.load(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null?0:list.size();
    }
}
