package com.jj.tidedemo.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Realm.UserLease;
import com.jj.tidedemo.Realm.UserPark;
import com.jj.tidedemo.Utils.ConstantValue;
import com.jj.tidedemo.Utils.SpUtils;
import com.jj.tidedemo.Utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

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
    private String[] start_time;
    private String[] end_time;
    private Date parse_lease_st;
    private Date parse_lease_et;
    private Realm mRealmLease;
    private UserLease mUserLease;
    private Integer rent_id;
    private SweetAlertDialog sweetAlertDialog;
    private String[] infos;

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
        RealmConfiguration configLease = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("userlease.realm") //文件名
                .schemaVersion(1)//版本号
                .build();
        mRealmLease = Realm.getInstance(configLease);
        //初始化查询条件
        infos = park_info.split("\n");
        //时间条件
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        start_time = infos[1].split("\t");
        end_time = infos[2].split("\t");
        try {
            parse_start = mFormat.parse(start_time[1]);
            parse_end = mFormat.parse(end_time[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] id = infos[4].split("\t");
        rent_id = new Integer(id[1]);
        UserPark result = mRealm.where(UserPark.class).equalTo("id", rent_id).findFirst();
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
        TextView confirm_time_start = (TextView) findViewById(R.id.confirm_time_start);
        TextView confirm_time_end = (TextView) findViewById(R.id.confirm_time_end);
        lease_time_start = (TextView) findViewById(R.id.lease_time_start);
        lease_time_end = (TextView) findViewById(R.id.lease_time_end);
        final_cost = (TextView) findViewById(R.id.final_cost);
        Button lease_confirm_btn = (Button) findViewById(R.id.lease_confirm_btn);

        lease_time_start.setOnClickListener(this);
        lease_time_end.setOnClickListener(this);
        lease_confirm_btn.setOnClickListener(this);
        //监听时间选择器，让其在选择完后自动计算租赁费用
        lease_time_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmCost(0);
            }
        });
        lease_time_end.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmCost(0);
            }
        });

        //初始化出租信息
        lease_user_name.setText(name);
        lease_user_cost.setText(cost + "元/h");
        lease_user_phone_num.setText(phone_num);
        lease_user_park_addr.setText(park_addr);
        confirm_time_start.setText(start_time[1]);
        confirm_time_end.setText(end_time[1]);

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

    private boolean confirmCost(int flag) {
        if (!TextUtils.isEmpty(lease_time_start.getText().toString())
                && !TextUtils.isEmpty(lease_time_end.getText().toString())) {
            //比较时间大小，最终转化为小时
            try {
                parse_lease_st = mFormat.parse(lease_time_start.getText().toString());
                parse_lease_et = mFormat.parse(lease_time_end.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (parse_lease_st.getTime() >= parse_start.getTime()
                    && parse_lease_et.getTime() <= parse_end.getTime()
                    && parse_lease_st.getTime() <= parse_lease_et.getTime()) {
                //得到时间差，执行显示费用逻辑
                double minutes = (parse_lease_et.getTime() - parse_lease_st.getTime()) / (60 * 1000);
                double parkingCost = minutes * Double.valueOf(cost) / 60;
                double totalCost = 200 + parkingCost;
                final_cost.setText("总费用：" + totalCost + "元（" + parkingCost + "元+200元押金）");
                if (flag == 1) {
                    //提示用户支付
                    sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("您总共需要支付")
                            .setContentText(totalCost + "元（" + parkingCost + "元+200元押金）")
                            .setConfirmText("确认")
                            .setCancelText("关闭")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    //改变对话框内容
                                    sDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            LeaseInfoActivity.this.finish();
                                        }
                                    });
                                    sDialog.setTitleText("支付成功")
                                            .setContentText("您已成功租赁该车位")
                                            .setConfirmText("确定")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    //传递消息让地图上刚被出租的车位标注移除
                                                    Intent intent = new Intent();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("from_lease_info_id", infos[4]);
                                                    intent.putExtras(bundle);
                                                    setResult(RESULT_OK, intent);
                                                    LeaseInfoActivity.this.finish();
                                                }
                                            })
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    //将租赁记录添加入数据库
                                    RealmResults<UserLease> all = mRealmLease.where(UserLease.class).findAll();
                                    //以此为主键的值，让其自增
                                    int size = all.size();
                                    mRealmLease.beginTransaction();
                                    mUserLease = mRealmLease.createObject(UserLease.class, size + 1);
                                    mUserLease.setRent_id(String.valueOf(rent_id));
                                    String spUserPhone = SpUtils.getString(getApplicationContext(),
                                            ConstantValue.USER_PHONE_NUM, "");
                                    if (!TextUtils.isEmpty(spUserPhone)) {
                                        mUserLease.setLease_user_phone(spUserPhone);
                                    } else {
                                        ToastUtil.show(LeaseInfoActivity.this, "请登录后再租赁车位");
                                        LeaseInfoActivity.this.finish();
                                    }
                                    mUserLease.setLease_start_time(parse_lease_st);
                                    mUserLease.setLease_end_time(parse_lease_et);
                                    mRealmLease.commitTransaction();
                                    //在出租数据库中标记该条记录为已租出
                                    mRealm.beginTransaction();
                                    UserPark first_res = mRealm.where(UserPark.class).equalTo("id", rent_id).findFirst();
                                    if (first_res != null) {
                                        first_res.setQuality("lease");
                                    }
                                    mRealm.commitTransaction();
                                }
                            });
                    sweetAlertDialog.show();
                }
            } else {
                ToastUtil.show(LeaseInfoActivity.this, "请选择正确的时间段");
            }
            return true;
        } else {
            return false;
        }
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return mFormat.format(date);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //确保在结束activity之前先结束dialog，不使内存泄露
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        /*RealmResults<UserLease> all = mRealmLease.where(UserLease.class).findAll();
        for(UserLease user:all) {
            Log.i("Test", user.getRent_id());
            Log.i("Test", String.valueOf(user.getLease_id()));
            Log.i("Test",user.getLease_user_phone());
            Log.i("Test", String.valueOf(user.getLease_start_time()));
        }
        mRealmLease.close();*/
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
                if (!confirmCost(1)) {
                    ToastUtil.show(LeaseInfoActivity.this, "请选择租赁的时间段");
                }
                break;
            default:
                break;
        }
    }
}
