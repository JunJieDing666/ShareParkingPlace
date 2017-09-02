package com.jj.tidedemo.Activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.jj.tidedemo.Fragment.ContentFragment;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Utils.PermissionsHelper.PermissionsHelper;
import com.jj.tidedemo.Utils.PermissionsHelper.permission.DangerousPermissions;

import java.util.ArrayList;
import java.util.List;

import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

/**
 * Created by Administrator on 2016/12/13.
 */

public class HomeActivity extends AppCompatActivity implements LocationSource,ViewAnimator.ViewAnimatorListener {
    //地图定位所使用到的变量
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

    //第三方侧滑栏使用到的变量
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ContentFragment contentFragment;
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;
    private ContentFragment mContentReplaceFg;

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

        /*************************************找到标题栏控件*****************************************/
        /*MyTopBarView my_topbar = (MyTopBarView) findViewById(R.id.my_topbar);
        my_topbar.setOnLeftAndRightBtnClickListener(new MyTopBarView.onLeftAndRightBtnClickListener() {
            @Override
            public void onLeftBtnClick() {
                Toast.makeText(getApplicationContext(), "leftbtnclick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightBtnClick() {
                Toast.makeText(getApplicationContext(), "rightbtnclick", Toast.LENGTH_SHORT).show();
            }
        });*/
        /********************************初始化第三方侧滑栏******************************************/
        //先生成一个透明的Fragment以供调用侧滑栏
        contentFragment = ContentFragment.newInstance("TRANSPARENT");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);
    }

    /********************************侧滑栏方法********************************************/
    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.icn_close);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.BUILDING, R.drawable.icn_1);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.BOOK, R.drawable.icn_2);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.PAINT, R.drawable.icn_3);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.CASE, R.drawable.icn_4);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(ContentFragment.SHOP, R.drawable.icn_5);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(ContentFragment.PARTY, R.drawable.icn_6);
        list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(ContentFragment.MOVIE, R.drawable.icn_7);
        list.add(menuItem7);
    }


    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //显示actionbar右侧的setting按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            //右侧菜单里得菜单项被点击
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ScreenShotable replaceFragment(android.support.v4.app.Fragment fragment, int topPosition) {
        //圆形显示Fragment
        /*View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackground(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();*/
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        return contentFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        //菜单项的监听器
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            case ContentFragment.BUILDING:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.BUILDING);
                replaceFragment(mContentReplaceFg,position);
                return screenShotable;
            case ContentFragment.BOOK:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.BOOK);
                replaceFragment(mContentReplaceFg,position);
                return screenShotable;
            case ContentFragment.PAINT:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.PAINT);
                replaceFragment(mContentReplaceFg,position);
                return screenShotable;
            case ContentFragment.CASE:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.CASE);
                replaceFragment(mContentReplaceFg,position);
                return screenShotable;
            default:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.BOOK);
                replaceFragment(mContentReplaceFg,position);
                return screenShotable;
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
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

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            Log.i("Test","1111111");
            super.onBackPressed();
        }else{
            Log.i("Test","2222222");
            getSupportFragmentManager().popBackStack();
        }
    }
}
