package com.jj.tidedemo.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Realm.UserPark;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Administrator on 2017/9/7.
 */

public class LeaseInfoActivity extends Activity implements View.OnClickListener {

    private Realm mRealm;
    private String park_info;
    private SimpleDateFormat mFormat;
    private Date parse_start;
    private Date parse_end;
    private String price;
    private String phone_num;
    private String name;
    private String cost;
    private String lease_start_time;
    private String lease_end_time;
    private String park_addr;
    private TimePickerView mPvTime;
    private TextView lease_time_start;
    private TextView lease_time_end;
    private TextView final_cost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lease);
        park_info = getIntent().getExtras().getString("park_info");
        initDB();
        initView();
    }

    private void initDB() {
        //初始化数据库
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("userpark-1.realm") //文件名
                .schemaVersion(1)//版本号
                .build();
        mRealm = Realm.getInstance(config);
        //初始化查询条件
        String[] infos = park_info.split("\n");
        //时间条件
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] start_time = infos[1].split("\t");
        String[] end_time = infos[2].split("\t");
        try {
            parse_start = mFormat.parse(start_time[1]);
            parse_end = mFormat.parse(end_time[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserPark result = mRealm.where(UserPark.class).equalTo("park_addr", infos[0])
                .equalTo("start_time", parse_start)
                .equalTo("end_time", parse_end)
                .findFirst();
        phone_num = result.getPhone_num();
        name = result.getName();
        cost = result.getCost();
        lease_start_time = result.getStart_time().toString();
        lease_end_time = result.getEnd_time().toString();
        park_addr = result.getPark_addr();
    }

    private void initView() {
        TextView lease_user_name = (TextView) findViewById(R.id.lease_user_name);
        TextView lease_user_cost = (TextView) findViewById(R.id.lease_user_cost);
        TextView lease_user_phone_num = (TextView) findViewById(R.id.lease_user_phone_num);
        TextView lease_user_park_addr = (TextView) findViewById(R.id.lease_user_park_addr);
        lease_time_start = (TextView) findViewById(R.id.lease_time_start);
        lease_time_end = (TextView) findViewById(R.id.lease_time_end);
        final_cost = (TextView) findViewById(R.id.final_cost);

        lease_time_start.setOnClickListener(this);
        lease_time_end.setOnClickListener(this);
        lease_time_end.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(lease_time_start.getText()) && !TextUtils.isEmpty(lease_time_end.getText())) {
                    //比较时间大小，最终转化为小时
                }

            }
        });

        //初始化出租信息
        lease_user_name.setText(name);
        lease_user_cost.setText(cost + "元/h");
        lease_user_phone_num.setText(phone_num);
        lease_user_park_addr.setText(park_addr);

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
        mPvTime.setDate(Calendar.getInstance());

    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return mFormat.format(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lease_time_start:
                if (mPvTime != null) {
                    mPvTime.show(v);
                }
                break;
            case R.id.lease_time_end:
                if (mPvTime != null) {
                    mPvTime.show(v);
                }
                break;
            case R.id.lease_confirm_btn:

                break;
            default:
                break;
        }
    }
}
