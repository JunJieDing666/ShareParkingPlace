package com.jj.tidedemo.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Utils.PermissionsHelper.PermissionsHelper;
import com.jj.tidedemo.Utils.PermissionsHelper.permission.DangerousPermissions;
import com.jj.tidedemo.View.MyTopBarView;

/**
 * Created by Administrator on 2016/12/13.
 */

public class HomeActivity extends Activity implements LocationSource {

    private MapView mMapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private UiSettings mUiSettings;//定义一个UiSettings对象

    // app所需要的全部危险权限
    static final String[] PERMISSIONS = new String[]{
            DangerousPermissions.LOCATION,
            DangerousPermissions.PHONE,
            DangerousPermissions.WRITE_STORAGE,
            DangerousPermissions.READ_STORAGE,
            DangerousPermissions.COARSE_LOCATION
    };
    private PermissionsHelper permissionsHelper;
    private String tag = "Permissions";


    /*----------------------------获取权限-----------------------------------------------------------*/
    private void checkPermissions() {
        permissionsHelper = new PermissionsHelper(this, PERMISSIONS, true);
        if (permissionsHelper.checkAllPermissions(PERMISSIONS)) {
            permissionsHelper.onDestroy();
            //doSomething
        } else {
            //申请权限
            permissionsHelper.startRequestNeedPermissions();
        }
        permissionsHelper.setonAllNeedPermissionsGrantedListener(new PermissionsHelper.onAllNeedPermissionsGrantedListener() {
            @Override
            public void onAllNeedPermissionsGranted() {
                //全部许可了,已经获得了所有权限
                Log.i(tag, "onAllNeedPermissionsGranted");
            }

            @Override
            public void onPermissionsDenied() {
                //被拒绝了,只要有一个权限被拒绝那么就会调用
                Log.i(tag, "onPermissionsDenied");
            }

            @Override
            public void hasLockForever() {
                //用户已经永久的拒绝了
                Log.i(tag, "hasLockForever");
            }

            @Override
            public void onBeforeRequestFinalPermissions(PermissionsHelper helper) {
                //被拒绝后,在最后一次申请权限之前
                Log.i(tag, "onBeforeRequestFinalPermissions");
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //----------------------------地图生命周期管理----------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //获得权限
        checkPermissions();

        //初始化数据
        initModel();
        //初始化UI
        initView(savedInstanceState);
        //初始化控制器
        initController();
    }

    private void initController() {

    }

    private void initModel() {

    }


    private void initView(Bundle savedInstanceState) {
        /******************************初始化地图***************************************************/
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        //初始化地图UI控件
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类
        mUiSettings.setMyLocationButtonEnabled(true);

        /*************************************找到标题栏控件****************************************/
        MyTopBarView my_topbar = (MyTopBarView) findViewById(R.id.my_topbar);
        my_topbar.setOnLeftAndRightBtnClickListener(new MyTopBarView.onLeftAndRightBtnClickListener() {
            @Override
            public void onLeftBtnClick() {
                Toast.makeText(getApplicationContext(), "leftbtnclick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightBtnClick() {
                Toast.makeText(getApplicationContext(), "rightbtnclick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    //----------------------------------------定位--------------------------------------------------
    //定位初始化及启动定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation amapLocation) {
                    if (mListener != null && amapLocation != null) {
                        if (amapLocation != null
                                && amapLocation.getErrorCode() == 0) {
                            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                        } else {
                            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                            Log.e("AmapErr", errText);
                        }
                    }
                }
            });
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
