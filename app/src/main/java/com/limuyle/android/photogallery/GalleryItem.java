package com.limuyle.android.photogallery;

/**
 * Created by limuyle on 2018/4/1.
 */

class GalleryItem {
    private String mId;
    private String mCaption;
    private String mUrl;
    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }
    @Override
    public String toString(){
        return mCaption;
    }
}
