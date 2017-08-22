package com.kpioneer.haocai.ad.bean;

/**
 * Created by Xionghu at 2017/8/17 11:17
 * function:
 * version:
 * desc:
 */

public class AdTestBean {

    /**
     * data : {"cityId":"10","imageUrl":"23","url":"7","startInterval":"376666","endInterval":"376666"}
     * success : true
     */

    private DataBean data;
    private boolean success;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBean {
        /**
         * cityId : 10
         * imageUrl : 23
         * url : 7
         * startInterval : 376666
         * endInterval : 376666
         */

        private String cityId;
        private String imageUrl;
        private String url;
        private long startInterval;
        private long endInterval;

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
}
