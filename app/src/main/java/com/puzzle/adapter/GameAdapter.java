package com.puzzle.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.puzzle.R;
import com.puzzle.adapter.viewholder.GameViewHolder;
import com.puzzle.ui.PuzzleApplication;
import com.puzzle.util.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by hyc on 2017/12/17 16:30
 */

public class GameAdapter  extends RecyclerView.Adapter<GameViewHolder>  {

    public List<Bitmap> list;

    public List<Integer> order;

    public int whitePosition;

    public int level;

    public boolean isGameOver = false;

    public float width ;

    public boolean isSwaping = false;

    public CountStepListener listener;

    public boolean isTips = false;

    public boolean isStop = true;

    public boolean isGuess = false;

    public List<Integer> blank;

    private Bitmap blankBitmap;

    public void setListener(CountStepListener listener) {
        this.listener = listener;
    }

    public GameAdapter(List<Bitmap> list,int level){
        this.list = list;
        this.level = level;
        initOrder();
        Log.e("TAG","level:"+list.size());
        width = list.get(0).getWidth();
    }

    public GameAdapter(List<Bitmap> list,boolean isGuess,int level){
        this.list = list;
        this.level = level;
        this.isGuess = isGuess;
        initOrder();
        Log.e("TAG","level:"+list.size());
        width = list.get(0).getWidth();
    }

    /**
     * 初始化拼图顺序
     */
    private void initOrder() {
        order = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            order.add(i);
        }
        CommonUtils.upset(order);
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i)==getItemCount()-1){
                whitePosition = i;
                break;
            }
        }

        blank = new ArrayList<>();
        if (isGuess){
            if (blankBitmap==null&&list!=null){
                blankBitmap = BitmapFactory.decodeResource(PuzzleApplication
                        .getContext().getResources(),R.mipmap.blank);
                blankBitmap = CommonUtils.zoomImage(blankBitmap,list
                        .get(0).getWidth(),list.get(0).getWidth());
            }
            int blankSize;
            if (level<4){
                blankSize=1;
            }else {
                blankSize=2;
            }
            for (int i = 0; i < blankSize; i++) {
                Random random = new Random();
                int temp = random.nextInt(list.size());
                if (blank.contains(temp)||temp==whitePosition){
                    i--;
                }else {
                    blank.add(temp);
                }
            }
        }
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GameViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game,parent,false));
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        if (isGuess){
            holder.loadImage(position==whitePosition
                    ,blank.contains(order.get(position)) ? blankBitmap : list.get(order
                            .get(position)),new PuzzleOnClickListener(position));
        }else {
            holder.loadImage(position==whitePosition
                    ,list.get(order.get(position)),new PuzzleOnClickListener(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class PuzzleOnClickListener implements View.OnClickListener{

        private int position;

        public PuzzleOnClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (isGameOver||position==whitePosition||isSwaping||isTips||isStop){
                return;
            }else if(position/level == whitePosition/level && Math.abs(whitePosition-position)==1){
                Collections.swap(order,whitePosition,position);
                swap(view,position-whitePosition>0?3:4);
                whitePosition = position;
                checkResult();
            }else if (position%level == whitePosition%level && Math.abs(whitePosition-position)==level){
                Collections.swap(order,whitePosition,position);
                swap(view,position-whitePosition>0?1:2);
                whitePosition = position;
                checkResult();
            }
        }

        /**
         * 检查是否正确完成拼图
         */
        private void checkResult() {
            boolean isOver = true;
            for (int i = 0; i < order.size(); i++) {
                if (i != order.get(i)){
                    isOver = false;
                    break;
                }
            }
            if (isOver){
                listener.gameOver();
                Toast.makeText(PuzzleApplication.getContext(), "游戏结束", Toast.LENGTH_SHORT).show();
                isGameOver = isOver;
            }
        }

    }

    /**
     * 拼图移动并进行数据交换
     * @param view
     * @param flag
     */
    public void swap(View view,int flag){
        isSwaping = true;
        listener.onStep();
        TranslateAnimation animation =null;
        switch (flag){
            case 1:animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -width);break;
            case 2:animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, width);break;
            case 3:animation = new TranslateAnimation(0.0f, -width, 0.0f, 0.0f);break;
            case 4:animation = new TranslateAnimation(0.0f, width, 0.0f, 0.0f);break;
        }
        animation.setDuration(150);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                notifyDataSetChanged();
                isSwaping = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 游戏计步监听
     */
    public interface CountStepListener{
        void onStep();
        void gameOver();
    }

    public void recycle(){
        if (list!=null){
            for (Bitmap bitmap : list) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 游戏提示
     */
    public void tips(){
        if(list==null||list.size()==0){
            return;
        }
        isTips = true;
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < order.size(); i++) {
            temp.add(i);
        }
        final List<Integer> save = order;
        order = temp;
        final int saveWhite = whitePosition;
        whitePosition = -1;
        notifyDataSetChanged();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTips = false;
                order = save;
                whitePosition = saveWhite;
                notifyDataSetChanged();
            }
        }, 3000);
    }

    /**
     * 继续游戏
     */
    public void continueGame(){
        isGameOver = false;
        initOrder();
        notifyDataSetChanged();
    }
}
