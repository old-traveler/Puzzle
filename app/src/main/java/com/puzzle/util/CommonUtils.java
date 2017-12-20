package com.puzzle.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.puzzle.ui.PuzzleApplication;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by hyc on 2017/12/17 16:22
 */

public class CommonUtils {

    private static final String SD_PATH = "/sdcard/puzzle/";
    private static final String IN_PATH = "/puzzle/";
    public static final int MARGIN = 50;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = PuzzleApplication.getContext()
                .getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip( float pxValue) {
        final float scale = PuzzleApplication.getContext()
                .getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽高
     * @return size[0] 表示width size[1]表示高度
     */
    public static int[] getScreenWidthHeight(){
        int size[] = new int[2];
        DisplayMetrics dm = PuzzleApplication.getContext()
                .getResources().getDisplayMetrics();
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    /**
     * 生成N*N块Bitmap
     * @param bitmap
     * @param n
     * @return
     */
    public static List<Bitmap> splitBitmap(Bitmap bitmap,int n){
        List<Bitmap> bitmaps = new ArrayList<>();
        int width = bitmap.getWidth();
        int pieceWidth = width/n;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                int x = j*pieceWidth;
                int y = i*pieceWidth;
                bitmaps.add(Bitmap.createBitmap(bitmap
                        ,x,y,pieceWidth,pieceWidth));
            }
        }
        Log.i("TAG","长度"+width);
        bitmap.recycle();
        return bitmaps;
    }

    /**
     * 获取本地图片并分割为拼图小块
     * @param path
     * @param n
     * @return
     */
    public static List<Bitmap> getPuzzleFragment(String path,int n){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return splitBitmap(zoomBitmap(bitmap,true),n);
    }

    /**
     * 直接剪裁像素点并分割为拼图小块
     * @param bitmap
     * @param n
     * @return
     */
    public static List<Bitmap> getPuzzleFragment(Bitmap bitmap,int n){
        return splitBitmap(zoomBitmap(bitmap,true),n);
    }

    /**
     *
     * @param bitmap
     * @param isRecycle
     * @param n
     * @return
     */
    public static List<Bitmap> getPuzzleFragment(Bitmap bitmap,boolean isRecycle,int n){
        return splitBitmap(zoomBitmap(bitmap,isRecycle),n);
    }

    /**
     * 获取本地资源图片并裁剪为小块
     * @param resource
     * @param resId
     * @param n
     * @return
     */
    public static List<Bitmap> getPuzzleFragment(Resources resource, int resId, int n){
        Bitmap bitmap = BitmapFactory.decodeResource(resource,resId);
        return splitBitmap(zoomBitmap(bitmap,false),n);
    }



    /**
     * 拼图图片中心剪裁并缩放
     * @param bitmap
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap,boolean isRecycle){
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        Bitmap cutBitmap = null;
        if (width>height){
            cutBitmap = Bitmap.createBitmap(bitmap, (int)
                    (width-height)/2,0,(int)height,(int)height);
        }else if(width<height){
            cutBitmap = Bitmap.createBitmap(bitmap,0
                    ,(int)(height-width)/2,(int)width,(int)width);
        }else {
            isRecycle = false;
            cutBitmap = bitmap;
        }
        width = cutBitmap.getWidth();
        if (isRecycle){
            bitmap.recycle();
        }
        float scale = (getScreenWidthHeight()[0]-dip2px(MARGIN))*1.0f/width;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(cutBitmap,
                0, 0, (int) width,(int) width, matrix, true);
        if (isRecycle){
            cutBitmap.recycle();
        }
        return newBitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,(int) height, matrix, true);
        return bitmap;
    }

    /**
     * 截取View中的Bitmap
     * @param v
     * @return
     */
    public static Bitmap loadBitmapFromViewBySystem(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        return bitmap;
    }

    /**
     * 保存bitmap到本地
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + "share_"+System.in+ ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            mBitmap.recycle();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    /**
     * 打乱拼图顺序
     * @param order
     */
    public static void upset(List<Integer> order){
        int position = order.size()-1;
        int col = (int) Math.sqrt(order.size());
        if (col==2){
            int temp =new Random().nextInt(4);
            if (temp==0){
                Collections.swap(order,3,1);
            }else if(temp==1){
                Collections.swap(order,3,2);
            }else if (temp==2){
                Collections.swap(order,3,1);
                Collections.swap(order,1,0);
            }else {
                Collections.swap(order,3,2);
                Collections.swap(order,2,0);
            }
            return;
        }
        Random random = new Random();
        for(int i=0;i<100;i++){
            int direction = random.nextInt(4);
            Log.e("TAG","  "+direction);
            switch (direction){
                case 0:
                    if (position-col>0){
                        Collections.swap(order,position,position-col);
                        position = position -col;
                    }else {
                        i--;
                    }
                    break;
                case 1:
                    if (position+col<order.size()){
                        Collections.swap(order,position,position+col);
                        position = position +col;
                    }else {
                        i--;
                    }
                    break;
                case 2:
                    if ((position-1)/col==position/col&&position-1>0){
                        Collections.swap(order,position,position-1);
                        position--;
                    }else {
                        i--;
                    }
                    break;
                case 3:
                    if ((position+1)/col==position/col&&position+1<order.size()){
                        Collections.swap(order,position,position+1);
                        position++;
                    }else {
                        i--;
                    }
                    break;
            }
        }
    }

    /**
     * 跳转Intent打包数据
     * @param type
     * @param level
     * @param resId
     * @return
     */
    public static Bundle packIntent(String type,int level,int resId){
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putInt("level",level);
        bundle.putInt("resId",resId);
        return bundle;
    }
    public static Bundle packIntent(String type,int level,String url,int time,int integral){
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        bundle.putInt("level",level);
        bundle.putInt("time",time);
        bundle.putInt("integral",integral);
        bundle.putString("imageUrl",url);
        return bundle;
    }


    public static boolean isEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    public static boolean hasEmpty(EditText... editTexts){
        for (EditText editText : editTexts) {
            if (TextUtils.isEmpty(editText.getText().toString())){
                return true;
            }
        }
        return false;
    }

    public static BmobUser getUser(){
        BmobUser user = BmobUser.getCurrentUser();
        if (user==null){
            user = new BmobUser();
            user.setObjectId("a4e51d99b4");
        }
        return user;
    }

    public static void compress(String path,FileCallback fileCallback){
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.width = 600;
        options.height = 600;
        Tiny.getInstance().source(path).asFile().withOptions(options).compress(fileCallback);
    }

    public static void compressShare(String path,FileCallback fileCallback){
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.quality = 50;
        Tiny.getInstance().source(path).asFile().withOptions(options).compress(fileCallback);
    }

    public static boolean hasLogined(){
        return BmobUser.getCurrentUser() != null;
    }

}
