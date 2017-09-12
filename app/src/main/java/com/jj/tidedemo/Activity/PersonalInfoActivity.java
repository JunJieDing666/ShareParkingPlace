package com.jj.tidedemo.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jj.tidedemo.R;
import com.jj.tidedemo.Utils.ConstantValue;
import com.jj.tidedemo.Utils.SpUtils;

/**
 * Created by Administrator on 2017/9/3.
 */

public class PersonalInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        initView();
    }

    private void initView() {
        //判断sp是否有用户注册的手机号
        String spUserPhone = SpUtils.getString(getApplicationContext(), ConstantValue.USER_PHONE_NUM, "");
        //用户手机号和车牌号
        TextView user_phone_num = (TextView) findViewById(R.id.user_phone_num);
        LinearLayout ll_user_phone_num = (LinearLayout) findViewById(R.id.ll_user_phone_num);
        LinearLayout ll_user_plate_num = (LinearLayout) findViewById(R.id.ll_user_plate_num);
        ImageView profile_image = (ImageView) findViewById(R.id.profile_image);
        Button quit_btn = (Button) findViewById(R.id.quit_btn);
        //读入用户手机号
        user_phone_num.setText(spUserPhone);

        ll_user_phone_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ll_user_plate_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        quit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtils.putString(getApplicationContext(), ConstantValue.USER_PHONE_NUM, "");
                PersonalInfoActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
