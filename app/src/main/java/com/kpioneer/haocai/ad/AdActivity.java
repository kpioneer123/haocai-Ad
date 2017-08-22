package com.kpioneer.haocai.ad;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kpioneer.haocai.ad.bean.AdTestBean;
import com.kpioneer.haocai.ad.inter.ImageDownLoadSaveInCacheCallBack;
import com.kpioneer.haocai.ad.utils.DownLoadImageSaveInCacheService;
import com.robin.lazy.cache.CacheLoaderManager;
import com.robin.lazy.util.bitmap.ImageDecodingInfo;
import com.robin.lazy.util.bitmap.ImageScaleType;
import com.robin.lazy.util.bitmap.ImageSize;
import com.robin.lazy.util.bitmap.ViewScaleType;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kpioneer.haocai.ad.utils.DownLoadImageSaveInCacheService.AD_DATA_KEY;
import static com.kpioneer.haocai.ad.utils.DownLoadImageSaveInCacheService.AD_IMAGE_KEY;

public class AdActivity extends AppCompatActivity {


    private static final int MSG_VISIBLE = 100 ;
    private static final int MSG_ERROR = 101;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_VISIBLE :
                if(continueCount)return;
                Intent intent = new Intent();
                intent.setClass(AdActivity.this,SecondActivity.class);
                startActivity(intent);
                    Log.d("main","下载图片成功");
                    break;
                case MSG_ERROR :
                    Log.d("main",MSG_ERROR+"");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @BindView(R.id.iv_advertising)
    ImageView ivAdvertising;
//    @BindView(R.id.cv_countdownViewTest6)
//    CountdownView cvCountdownViewTest6;
    @BindView(R.id.tv_send_sms)
    TextView tvSendSms;

    private SendSmsTimeCount time;
    private  boolean continueCount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad);
        ButterKnife.bind(this);

//        cvCountdownViewTest6.start(3 * 1000L);
//        cvCountdownViewTest6.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
//            @Override
//            public void onEnd(CountdownView cv) {
//
//            }
//        });


        String url = "http://172.16.3.250:9090/db";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("mainhttp",e+"");
                    }

                    @Override
                    public void onResponse(String response, int id) {


                        Log.d("mainhttp",response);
                        Gson gson = new Gson();
                        AdTestBean adTestBean = gson.fromJson(response, AdTestBean.class);//对于javabean直接给出class实例

//                        onDownLoad(adTestBean.getData().getImageUrl());
//                        DownLoadImageSaveInCacheService.saveAdData(response);


                        onDownLoad(response);
                    }


                });

        time = new SendSmsTimeCount(4000, 1000);// 构造CountDownTimer对象
        time.setOnTimeCountListener(new SendSmsTimeCount.OnTimeCountListener() {
            @Override
            public void onTick(long millisUntilFinished) {

                tvSendSms.setText("跳过" + millisUntilFinished/1000+"s");
            }

            @Override
            public void onFinish() {

                Intent intent = new Intent();
                intent.setClass(AdActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });


        tvSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AdActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        options.inTargetDensity = Resources.getSystem().getDisplayMetrics().densityDpi ;
        options.inJustDecodeBounds = true;

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）



        String adJson = CacheLoaderManager.getInstance().loadString(AD_DATA_KEY);

        if (adJson != null) {
            Gson gson = new Gson();
            AdTestBean adTestBean = gson.fromJson(adJson, AdTestBean.class);//对于javabean直接给出class实例

            long currentTime= System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
            long startTime=adTestBean.getData().getStartInterval();
            long remainingTime = adTestBean.getData().getEndInterval()-currentTime;
            Log.d("main",currentTime+"");
            Log.d("main2",startTime+"");
            Log.d("main3",remainingTime+"");

            if(currentTime>startTime&&remainingTime >0){
                ImageDecodingInfo imageDecodingInfo = new ImageDecodingInfo(new ImageSize(width,height), ImageScaleType.EXACTLY_STRETCHED, ViewScaleType.FIT_INSIDE,true,options);
                Bitmap bmp =   CacheLoaderManager.getInstance().loadBitmap(AD_IMAGE_KEY,imageDecodingInfo);
                if(bmp!=null){
                    ivAdvertising.setImageBitmap(bmp);
                    ivAdvertising.setVisibility(View.VISIBLE);
                    tvSendSms.setVisibility(View.VISIBLE);
                    time.start();
                    continueCount =true;
                }
            }
        }

    }


        private void onDownLoad(String response) {
        DownLoadImageSaveInCacheService service = new DownLoadImageSaveInCacheService(this.getApplicationContext(),
                response,
                new ImageDownLoadSaveInCacheCallBack() {



                    @Override
                    public void onDownLoadSuccess(File file) {
                    }

                    @Override
                    public void onDownLoadSuccess() {
                        // 在这里执行图片保存方法
                        Message message = new Message();
                        message.what = MSG_VISIBLE;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        Message message = new Message();
                        message.what = MSG_ERROR;
                        mHandler.sendMessage(message);
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }
}
