package com.kpioneer.haocai.ad.bean;

/**
 * Created by Xionghu at 2017/8/16 17:27
 * function:
 * version:
 * desc:
 */

public class AbBean {
    String cityId;
    String imageUrl;
    String url;
    long startInterval;
    long endInterval;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStartInterval() {
        return startInterval;
    }

    public void setStartInterval(long startInterval) {
        this.startInterval = startInterval;
    }

    public long getEndInterval() {
        return endInterval;
    }

    public void setEndInterval(long endInterval) {
        this.endInterval = endInterval;
    }
}
