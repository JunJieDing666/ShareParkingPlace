package com.jj.tidedemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Realm.UserPark;
import com.jj.tidedemo.Utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/9/6.
 */

public class RentActivity extends AppCompatActivity implements View.OnClickListener {

    private TimePickerView mPvTime;
    private Realm mRealm;
    private SimpleDateFormat mFormat;
    private UserPark mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);
        initView();
        initDB();
    }

    private void initDB() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("userpark-1.realm") //文件名
                .schemaVersion(1) //版本号
                .build();
        mRealm = Realm.getInstance(config);
    }

    private void initView() {
        TextView time_start = (TextView) findViewById(R.id.time_start);
        TextView time_end = (TextView) findViewById(R.id.time_end);
        Button rent_confirm_btn = (Button) findViewById(R.id.rent_confirm_btn);
        time_start.setOnClickListener(this);
        time_end.setOnClickListener(this);
        rent_confirm_btn.setOnClickListener(this);

        //选中事件回调
        mPvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                TextView textView = (TextView) v;
                textView.setText(getTime(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .build();
        mPvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return mFormat.format(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_start:
                if (mPvTime != null) {
                    mPvTime.show(v);
                }
                break;
            case R.id.time_end:
                if (mPvTime != null) {
                    mPvTime.show(v);
                }
                break;
            case R.id.pick_address:
                pickAdress();
                break;
            case R.id.rent_confirm_btn:
                saveInfo();
                break;
            default:
                break;
        }

    }

    private void saveInfo() {
        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView user_phone_num = (TextView) findViewById(R.id.user_phone_num);
        TextView user_park_addr = (TextView) findViewById(R.id.user_park_addr);
        TextView time_start = (TextView) findViewById(R.id.time_start);
        TextView time_end = (TextView) findViewById(R.id.time_end);
        TextView user_cost = (TextView) findViewById(R.id.user_cost);
        if (TextUtils.isEmpty(user_name.getText()) || TextUtils.isEmpty(user_phone_num.getText())
                || TextUtils.isEmpty(user_park_addr.getText()) || TextUtils.isEmpty(time_start.getText())
                || TextUtils.isEmpty(time_end.getText())) {
            ToastUtil.show(this, "请将信息填写完整");
        } else {
            //将用户发布的停车位信息记录进数据库
            RealmResults<UserPark> all = mRealm.where(UserPark.class).findAll();
            //以此为主键的值，让其自增
            int size = all.size();
            mRealm.beginTransaction();
            mUser = mRealm.createObject(UserPark.class, size + 1);
            mUser.setName(user_name.getText().toString());
            mUser.setPhone_num(user_phone_num.getText().toString());
            mUser.setPark_addr(user_park_addr.getText().toString());
            mUser.setCost(user_cost.getText().toString());
            mUser.setQuality("rent");
            try {
                mUser.setStart_time(mFormat.parse(time_start.getText().toString()));
                mUser.setEnd_time(mFormat.parse(time_end.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            mRealm.commitTransaction();
            //返回主界面
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("id", size + 1);
            bundle.putString("park_addr", user_park_addr.getText().toString());
            bundle.putString("start_time", time_start.getText().toString());
            bundle.putString("end_time", time_end.getText().toString());
            bundle.putString("cost", user_cost.getText().toString());
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void pickAdress() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*RealmResults<UserPark> all = mRealm.where(UserPark.class).findAll();
        for(UserPark user:all) {
            Log.i("Test",user.getStart_time().toString());
            Log.i("Test",user.getName());
            Log.i("Test",user.getPhone_num());
            Log.i("Test",user.getPark_addr());
            Log.i("Test", String.valueOf(user.getLat()));
            Log.i("Test", String.valueOf(user.getLon()));
        }*/
        mRealm.close();
    }

}
