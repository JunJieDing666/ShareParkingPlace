package com.jj.tidedemo.Realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Administrator on 2017/9/6.
 */

public class UserPark extends RealmObject {
    @Required
    private String name;
    @Ignore
    private String sex;
    @Required
    private String phone_num;
    @Required
    private String park_addr;
    @Required
    private Date start_time;
    @Required
    private Date end_time;
    @Required
    private String quality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getPark_addr() {
        return park_addr;
    }

    public void setPark_addr(String park_addr) {
        this.park_addr = park_addr;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
