package com.puzzle.adapter.viewholder;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.puzzle.R;


/**
 * Created by hyc on 2017/12/17 16:33
 */

public class GameViewHolder extends RecyclerView.ViewHolder{

    public ImageView iv_game;

    public GameViewHolder(View itemView) {
        super(itemView);
        iv_game = itemView.findViewById(R.id.iv_game);
    }

    public void loadImage(boolean isWhiteboard,Bitmap bitmap, View.OnClickListener listener){
        if (!isWhiteboard){
            iv_game.setImageBitmap(bitmap);
        }else {
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.parseColor("#393a3f"));
            iv_game.setImageBitmap(bitmap1);
        }
        iv_game.setOnClickListener(listener);
    }



}
