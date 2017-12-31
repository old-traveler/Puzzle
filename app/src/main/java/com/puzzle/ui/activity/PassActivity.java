package com.puzzle.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.puzzle.R;
import com.puzzle.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class PassActivity extends BaseActivity {
    private List<Integer> imageId;
    private List<ImageView> list;
    ViewPager vp_pass;
    TextView level;
    TextView state;
    Button btn_begin;
    public int passLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);
        vp_pass = findViewById(R.id.vp_pass);
        setToolBar(R.id.tb_pass);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        passLevel = CommonUtils.getCurrentLevel();
        if (passLevel>=vp_pass.getCurrentItem()+1){
            state.setText("已解锁");
            state.setTextColor(Color.parseColor("#52f910"));
        }
    }

    private void initView() {
        if (imageId==null){
            imageId = new ArrayList<>();
            imageId.add(R.mipmap.level_1);
            imageId.add(R.mipmap.level_2);
            imageId.add(R.mipmap.level_3);
            imageId.add(R.mipmap.level_4);
            imageId.add(R.mipmap.level_5);
            imageId.add(R.mipmap.level_6);
            imageId.add(R.mipmap.level_7);
            imageId.add(R.mipmap.level_8);
            imageId.add(R.mipmap.level_9);
            imageId.add(R.mipmap.level_10);
        }
        list = new ArrayList<>();
        for (Integer integer : imageId) {
            ImageView imageView = new ImageView(PassActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(PassActivity.this)
                    .load(integer)
                    .into(imageView);
            list.add(imageView);
        }
        vp_pass.setAdapter(new ViewPagerAdapter());
        vp_pass.setCurrentItem(0);
        level = findViewById(R.id.tv_level);
        state = findViewById(R.id.tv_state);
        vp_pass.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                level.setText("第"+(position+1)+"关");
                if (position<passLevel){
                    state.setText("已解锁");
                    btn_begin.setClickable(true);
                    state.setTextColor(Color.parseColor("#52f910"));
                }else {
                    state.setText("未解锁");
                    state.setTextColor(Color.parseColor("#ca0d20"));
                    btn_begin.setClickable(false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        btn_begin = findViewById(R.id.btn_begin);
        btn_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassActivity.this,PassGameActivity.class);
                intent.putExtras(CommonUtils.packIntent("pass"
                        ,vp_pass.getCurrentItem()+1,imageId.get(vp_pass.getCurrentItem())));
                startActivity(intent);
            }
        });
    }
    public class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageId.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
