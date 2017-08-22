package com.kpioneer.haocai.ad.inter;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Xionghu at 2017/8/17 15:02
 * function:
 * version:
 * desc:
 */

public interface ImageDownLoadCallBack {
    void onDownLoadSuccess(File file);
    void onDownLoadSuccess(Bitmap bitmap);

    void onDownLoadFailed();
}
