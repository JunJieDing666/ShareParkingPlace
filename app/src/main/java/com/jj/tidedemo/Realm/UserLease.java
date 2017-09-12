package com.jj.tidedemo.Realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Administrator on 2017/9/11.
 */

public class UserLease extends RealmObject {
    @PrimaryKey
    private int lease_id;
    @Required
    private String rent_id;

    private String lease_user_name;
    @Required
    private String lease_user_phone;
    @Required
    private Date lease_start_time;
    @Required
    private Date lease_end_time;

    public int getLease_id() {
        return lease_id;
    }

    public void setLease_id(int lease_id) {
        this.lease_id = lease_id;
    }

    public String getRent_id() {
        return rent_id;
    }

    public void setRent_id(String rent_id) {
        this.rent_id = rent_id;
    }

    public String getLease_user_name() {
        return lease_user_name;
    }

    public void setLease_user_name(String lease_user_name) {
        this.lease_user_name = lease_user_name;
    }

    public String getLease_user_phone() {
        return lease_user_phone;
    }

    public void setLease_user_phone(String lease_user_phone) {
        this.lease_user_phone = lease_user_phone;
    }

    public Date getLease_start_time() {
        return lease_start_time;
    }

    public void setLease_start_time(Date lease_start_time) {
        this.lease_start_time = lease_start_time;
    }

    public Date getLease_end_time() {
        return lease_end_time;
    }

    public void setLease_end_time(Date lease_end_time) {
        this.lease_end_time = lease_end_time;
    }

}
