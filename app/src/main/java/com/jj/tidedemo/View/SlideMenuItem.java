package com.jj.tidedemo.View;

import com.jj.tidedemo.Interface.Resourceble;

/**
 * Created by Administrator on 2017/9/3.
 */


public class SlideMenuItem implements Resourceble {
    private String name;
    private int imageRes;
    private String imageTitle;

    public SlideMenuItem(String name, int imageRes,String imageTitle) {
        this.name = name;
        this.imageRes = imageRes;
        this.imageTitle = imageTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

}
