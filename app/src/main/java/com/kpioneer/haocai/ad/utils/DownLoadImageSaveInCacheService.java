package com.kpioneer.haocai.ad.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.kpioneer.haocai.ad.bean.AdTestBean;
import com.kpioneer.haocai.ad.inter.ImageDownLoadCallBack;
import com.kpioneer.haocai.ad.inter.ImageDownLoadSaveInCacheCallBack;
import com.robin.lazy.cache.CacheLoaderManager;
import com.robin.lazy.util.bitmap.ImageDecodingInfo;
import com.robin.lazy.util.bitmap.ImageScaleType;
import com.robin.lazy.util.bitmap.ImageSize;
import com.robin.lazy.util.bitmap.ViewScaleType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片下载
 *
 */
public class DownLoadImageSaveInCacheService implements Runnable {
    public   static final  String   AD_IMAGE_KEY = "AdImageKey";
    public   static final  String   AD_DATA_KEY  = "AD_DATA_KEY";

    private String response;
    private Context context;
    private ImageDownLoadSaveInCacheCallBack callBack;
    private File currentFile;
    private boolean isExisted =false;
    private String imgUrl;
    public DownLoadImageSaveInCacheService(Context context, String response, ImageDownLoadSaveInCacheCallBack callBack) {
        this.response = response;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void run() {
        File file = null;
        Bitmap bitmap = null;
        try {
//            file = Glide.with(context)
//                    .load(url)
//                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .get();

            Gson gson = new Gson();
            AdTestBean newAdBean= gson.fromJson(response, AdTestBean.class);//对于javabean直接给出class实例
            imgUrl =newAdBean.getData().getImageUrl();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = true;
            options.inTargetDensity = Resources.getSystem().getDisplayMetrics().densityDpi ;
            options.inJustDecodeBounds = true;
            ImageDecodingInfo imageDecodingInfo = new ImageDecodingInfo(new ImageSize(1920,1080), ImageScaleType.EXACTLY_STRETCHED, ViewScaleType.FIT_INSIDE,true,options);
            Bitmap bmp =   CacheLoaderManager.getInstance().loadBitmap(AD_IMAGE_KEY,imageDecodingInfo);
            if(bmp!=null) {
                String oldAdJson = CacheLoaderManager.getInstance().loadString(AD_DATA_KEY);
                if (oldAdJson != null) {

                    AdTestBean oldAdBean = gson.fromJson(oldAdJson, AdTestBean.class);//对于javabean直接给出class实例
                    if (newAdBean == null || newAdBean.equals(oldAdBean.getData().getImageUrl())) {
                        Log.d("main993,", "不用重新下载图片");
                        isExisted = true;
                        saveAdData(response);
                        return;
                    }
                }
            }
            saveAdData(response);
            bitmap = Glide.with(context)
                    .load(imgUrl)
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            if (bitmap != null){
                // 在这里执行图片保存方法
                //saveImageToGallery(context,bitmap);
                saveInLazyCache(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (file != null) {
//                callBack.onDownLoadSuccess(file);
//            } else {
//                callBack.onDownLoadFailed();
//            }
            if (bitmap != null||isExisted) {
                callBack.onDownLoadSuccess();
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }

    public static void saveAdData(String jsonData){
//        String oldAdJson = CacheLoaderManager.getInstance().loadString(AD_DATA_KEY);
//        if(jsonData.equals(jsonData)){
//            Log.d("main992,","广告数据一样");
//            return;
//        }

        long currentTime= System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
        Gson gson = new Gson();
        AdTestBean adTestBean = gson.fromJson(jsonData, AdTestBean.class);//对于javabean直接给出class实例



        long remainingTime = adTestBean.getData().getEndInterval()-currentTime;
        if(remainingTime <=0){
            CacheLoaderManager.getInstance().delete(AD_DATA_KEY);
            CacheLoaderManager.getInstance().delete(AD_IMAGE_KEY);
            return;
        }
        CacheLoaderManager.getInstance().saveString(AD_DATA_KEY, jsonData, 10*1000L);
    }

    public void saveInLazyCache( Bitmap bmp){
        String json = CacheLoaderManager.getInstance().loadString(AD_DATA_KEY);
        if(TextUtils.isEmpty(json)){

            CacheLoaderManager.getInstance().delete(AD_IMAGE_KEY);
            return;
        }
        Gson gson = new Gson();
        AdTestBean adTestBean = gson.fromJson(json, AdTestBean.class);//对于javabean直接给出class实例

        long currentTime= System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳

        long remainingTime = adTestBean.getData().getEndInterval()-currentTime;
        Log.d("main99,","remainingTime"+remainingTime);
        if(remainingTime <=0){
            CacheLoaderManager.getInstance().delete(AD_IMAGE_KEY);
            return;
        }
        Log.d("main991,","remainingTime"+remainingTime);
        CacheLoaderManager.getInstance().saveBitmap(AD_IMAGE_KEY, bmp, remainingTime,true);

    }

    public String getTime(){

        long time= System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳

        String  str=String.valueOf(time);

        return str;

    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File file= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();//注意小米手机必须这样获得public绝对路径
        String fileName = "新建文件夹";
      File appDir = new File(file ,fileName);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
         fileName = System.currentTimeMillis() + ".jpg";
        currentFile = new File(appDir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    currentFile.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(currentFile.getPath()))));
    }
}