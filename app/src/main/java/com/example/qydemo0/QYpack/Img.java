package com.example.qydemo0.QYpack;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import com.bumptech.glide.load.MultiTransformation;
import com.example.qydemo0.R;

public class Img {

    public static void setOnClickForView(View v, AppCompatActivity s, Class t, String url){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(s, t);
                intent.putExtra("img", url);
                s.startActivity(intent);
            }
        });
    }



    public static String saveImg(Bitmap bitmap, String name, Context context) {
        try {
            String path = context.getExternalCacheDir().getPath();
            if(path != null){
                File dir = new File(path + "/pics");
                if(!dir.exists()) dir.mkdirs();
                path = dir + "/" + System.currentTimeMillis();
                File pic = new File(path);
                Log.d("hjt.IMG.path", path);
                FileOutputStream outputStream = new FileOutputStream(pic);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                return path;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFormUrl(String url, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit(360, 480).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap getDiskBitmap(String pathString){
        Bitmap bitmap = null;
        try{
            File file=new File(pathString);
            if (file.exists()){
                bitmap= BitmapFactory.decodeFile(pathString);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String compressWithUrl(String img_url, Context context){
        Bitmap tmp = getDiskBitmap(img_url);
        return saveImg(tmp, String.valueOf(System.currentTimeMillis()), context);
    }


    public static boolean url2imgView(String img_url, ImageView img, Context context){
        Glide.with(context)
                .load(img_url)
                .into(img);
        return true;
    }

    public static void url2imgViewRoundRectangle(String img_url, ImageView img, Context context, int radius){
        Glide.with(context)
                .load(img_url)
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(radius)))
                .into(img);
    }

    public static void url2imgViewRoundRectangle(Drawable img_b, ImageView img, Context context, int radius){
        Glide.with(context)
                .load(img_b)
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(radius)))
                .into(img);
    }

    public static ImageView linearLayoutDivideLine(Activity context){
        ImageView l = new ImageView(context);
        l.setBackgroundColor(context.getResources().getColor(R.color.gray));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        l.setLayoutParams(layoutParams);
        return l;
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }



    public static void roundImgUri(Activity ac, ImageView img, Uri uri){
        Glide.with(ac)
                .load(uri)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(img);
    }

    public static void roundImgUrl(Activity ac, ImageView img, String url){
        Glide.with(ac)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(img);
    }

    public  static Bitmap getBitmapFromLocalUrl(String url){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(url);
            return  BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable){
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

}