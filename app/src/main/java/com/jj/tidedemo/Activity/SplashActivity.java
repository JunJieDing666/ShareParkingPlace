package com.jj.tidedemo.Activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.jj.tidedemo.R;
import com.jj.tidedemo.Utils.ConstantValue;
import com.jj.tidedemo.Utils.SpUtils;
import com.jj.tidedemo.Utils.VersionUpdateUtils;

public class SplashActivity extends AppCompatActivity {

    private String myVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        initView();
        initModel();
        initController();
    }

    private void initController() {
        //查询当前的版本并进行比对，看是否要更新
        myVersion = getVersion(getApplicationContext());
        final VersionUpdateUtils versionUpdateUtils = new VersionUpdateUtils(myVersion, SplashActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (SpUtils.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false)) {
                    //获取服务器版本号
                    versionUpdateUtils.getCloudVersion();
                } else {
                    versionUpdateUtils.notUpdateEnterHome();
                }
            }
        }).start();
    }

    private void initModel() {

    }

    private void initView() {
        //1.找到闪现界面的根视图
        RelativeLayout rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        //2.给闪现界面设置动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        rl_root.setAnimation(alphaAnimation);
    }

    /**
     * 获得版本名称
     *
     * @param context
     * @return
     */
    public String getVersion(Context context) {
        /*获得清单文件中所有信息*/
        PackageManager manager = context.getPackageManager();
        /*获取当前程序的包名*/
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
