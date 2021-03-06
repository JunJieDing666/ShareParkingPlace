package com.jj.tidedemo.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.jj.tidedemo.Adapter.InfoWinAdapter;
import com.jj.tidedemo.Fragment.ContentFragment;
import com.jj.tidedemo.Interface.Resourceble;
import com.jj.tidedemo.Overlay.PoiOverlay;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Realm.UserPark;
import com.jj.tidedemo.Utils.ConstantValue;
import com.jj.tidedemo.Utils.Md5Utils;
import com.jj.tidedemo.Utils.PermissionsHelper.PermissionsHelper;
import com.jj.tidedemo.Utils.PermissionsHelper.permission.DangerousPermissions;
import com.jj.tidedemo.Utils.SpUtils;
import com.jj.tidedemo.Utils.ToastUtil;
import com.jj.tidedemo.Utils.ViewAnimator;
import com.jj.tidedemo.View.SlideMenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by Administrator on 2016/12/13.
 */

public class HomeActivity extends AppCompatActivity implements AMap.OnMapClickListener, LocationSource,
        ViewAnimator.ViewAnimatorListener, AMap.OnMarkerClickListener, PoiSearch.OnPoiSearchListener,
        View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener{
    //地图定位所使用到的变量
    private MapView mMapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private double lat;
    private double lon;
    private boolean isFirstLoc = true;


    //地图搜索所使用到的变量
    private String mKeyWords = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 1;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private TextView mKeywordsTextView;
    private Marker mPoiMarker;
    private ImageView mCleanKeyWords;
    private InfoWinAdapter adapter;
    private Marker oldMarker;

    public static final int REQUEST_CODE = 100;
    public static final int RESULT_CODE_INPUTTIPS = 101;
    public static final int RESULT_CODE_KEYWORDS = 102;
    private static final int REQUEST_RENT = 0;
    private static final int REQUEST_LEASE = 1;

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
    private ViewAnimator viewAnimator;
    private LinearLayout linearLayout;
    private ContentFragment mContentReplaceFg;
    private ActionBar mSupportActionBar;
    private String currentDistrict;
    private String currentCity;

    //短信验证所用到的变量
    private EventHandler eventHandler;
    private String userPhoneNum;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(HomeActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    // 验证成功后处理你自己的逻辑
                    //设置用户密码
                    showSetPsdDialog(HomeActivity.this);
                    break;
            }
        }
    };
    private Button mRentBtn;
    private GeocodeSearch geocodeSearch;
    private Marker geoMarker;
    private Realm mRealm;
    private String query_park_addr;
    private String query_start_time;
    private String query_end_time;
    private String query_cost;
    private String parkAddr;
    private String start_time;
    private String end_time;
    private String cost;
    private int id;
    private SimpleDateFormat mFormat;
    private Circle mCircle;
    private int query_id;
    private String query_quality;
    private AMapLocation mCurrentLocation;


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
        permissionsHelper.setonAllNeedPermissionsGrantedListener(new PermissionsHelper
                .onAllNeedPermissionsGrantedListener() {
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
        //初始化UI
        //initView(savedInstanceState);
        //初始化地图
        initMap(savedInstanceState);
        //初始化侧滑栏
        initSlideMenu();
        //初始化短信验证
        initSMSSDK();
        //初始化数据库里的停车位点标注
        initPoint();
    }

    private void initPoint() {
        //初始化数据库
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("userpark-1.realm") //文件名
                .schemaVersion(1)//版本号
                .build();
        mRealm = Realm.getInstance(config);
    }

    private void initSMSSDK() {
        // 创建EventHandler对象
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (data instanceof Throwable) {
                    Throwable throwable = (Throwable) data;
                    String msg = throwable.getMessage();
                    Message message = new Message();
                    message.obj = msg;
                    message.what = 1;
                    handler.sendMessage(message);
                } else {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
            }
        };
        // 注册监听器
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void showSetPsdDialog(Context ctx) {
        //创建对话框
        final AlertDialog dialog = new AlertDialog.Builder(ctx).create();
        //因为对话框的样式需要自己设定，所以调用setView()方法
        final View view = View.inflate(ctx, R.layout.dialog_set_psd, null);
        dialog.setView(view);
        dialog.show();

        //设置对话框按钮的点击事件
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        //确认键的点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);

                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        //防止按返回键后对话框还在，需要解散对话框
                        dialog.dismiss();
                        //将用户设置的密码存储在sp中
                        SpUtils.putString(getApplicationContext(), ConstantValue.DEFENSE_PSD, Md5Utils.encode(psd));
                    } else {
                        Toast.makeText(getApplicationContext(), "确认密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //弹出吐司指出错误
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //取消键的点击事件
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void initSlideMenu() {
        /********************************初始化第三方侧滑栏******************************************/
        //先生成一个透明的Fragment以供调用侧滑栏
        mContentReplaceFg = ContentFragment.newInstance("TRANSPARENT");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContentReplaceFg)
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
        viewAnimator = new ViewAnimator<>(this, list, mContentReplaceFg, drawerLayout, this);
    }

    private void initMap(Bundle savedInstanceState) {
        /******************************初始化地图***************************************************/
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }

        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style

        //初始化地图UI控件
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类
        mUiSettings.setMyLocationButtonEnabled(true);
        //aMap.setTrafficEnabled(true);

        //搜索控件
        mCleanKeyWords = (ImageView) findViewById(R.id.clean_keywords);
        mCleanKeyWords.setOnClickListener(this);
        mKeyWords = "";
        mKeywordsTextView = (TextView) findViewById(R.id.main_keywords);
        mKeywordsTextView.setOnClickListener(this);
        mRentBtn = (Button) findViewById(R.id.rent_btn);
        mRentBtn.setOnClickListener(this);

        //地理编码
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 设置页面监听
     */
    private void setUpMap() {
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setOnMapClickListener(this);
        adapter = new InfoWinAdapter(this);
        aMap.setInfoWindowAdapter(adapter);// 添加显示infowindow监听事件
        aMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    /*private void initView(Bundle savedInstanceState) {

        *//*************************************找到标题栏控件*****************************************//*
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

    }*/

    /********************************侧滑栏方法********************************************/
    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.close, "");
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.BUILDING, R.drawable.history, "停车历史");
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.BOOK, R.drawable.bags, "钱包");
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.PAINT, R.drawable.help, "帮助");
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.CASE, R.drawable.set, "设置");
        list.add(menuItem4);
    }


    private void setActionBar() {
        //使用系统自带的actionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSupportActionBar = getSupportActionBar();
        mSupportActionBar.setHomeButtonEnabled(true);
        mSupportActionBar.setDisplayHomeAsUpEnabled(true);
        mSupportActionBar.setDisplayShowTitleEnabled(true);
        mSupportActionBar.setTitle("");
        //在此基础上添加自己的标题栏布局
        mSupportActionBar.setDisplayShowCustomEnabled(true);
        mSupportActionBar.setCustomView(R.layout.my_topbar_view);
        ImageButton rightBtn = (ImageButton) findViewById(R.id.top_bar_right_btn);
        ImageButton leftBtn = (ImageButton) findViewById(R.id.top_bar_left_btn);
        TextView textView = (TextView) findViewById(R.id.top_bar_title);
        leftBtn.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断sp是否有用户注册的手机号
                String spUserPhone = SpUtils.getString(getApplicationContext(), ConstantValue.USER_PHONE_NUM, "");
                //如果应用没有记录过用户手机号则进入注册页面
                if (TextUtils.isEmpty(spUserPhone)) {
                    RegisterPage registerPage = new RegisterPage();
                    registerPage.setRegisterCallback(new EventHandler() {
                        public void afterEvent(int event, int result, Object data) {
                            // 解析注册结果
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                @SuppressWarnings("unchecked")
                                HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                                userPhoneNum = (String) phoneMap.get("phone");
                                //验证成功后把记录用户号码
                                SpUtils.putString(getApplicationContext(), ConstantValue.USER_PHONE_NUM, userPhoneNum);
                            }
                        }
                    });
                    registerPage.show(getApplicationContext());
                } else {
                    //否则直接进入用户界面（暂时未做密码登录）
                    Intent userIntent = new Intent(getApplicationContext(), PersonalInfoActivity.class);
                    userIntent.putExtra("phone", spUserPhone);
                    startActivity(userIntent);
                }
            }
        });
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
        return false;
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

    private ScreenShotable replaceFragment(ContentFragment fragment, int topPosition) {
        //圆形显示Fragment
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, view.getWidth(), topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackground(new BitmapDrawable(getResources(), fragment.getBitmap()));
        animator.start();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.commit();
        return mContentReplaceFg;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        //菜单项的监听器
        switch (slideMenuItem.getName()) {
            case ContentFragment.CLOSE:
                return screenShotable;
            case ContentFragment.BUILDING:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.BUILDING);
                replaceFragment(mContentReplaceFg, position);
                return screenShotable;
            case ContentFragment.BOOK:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.BOOK);
                replaceFragment(mContentReplaceFg, position);
                return screenShotable;
            case ContentFragment.PAINT:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.PAINT);
                replaceFragment(mContentReplaceFg, position);
                return screenShotable;
            case ContentFragment.CASE:
                mContentReplaceFg = ContentFragment.newInstance(ContentFragment.CASE);
                replaceFragment(mContentReplaceFg, position);
                return screenShotable;
            default:
                mContentReplaceFg = ContentFragment.newInstance("TRANSPARENT");
                replaceFragment(mContentReplaceFg, position);
                return screenShotable;
        }
    }

    @Override
    public void disableHomeButton() {
        mSupportActionBar.setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        mSupportActionBar.setHomeButtonEnabled(true);
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
                            mCurrentLocation = amapLocation;
                            adapter.setmCurrentLocation(amapLocation);
                            lat = amapLocation.getLatitude();//获取纬度
                            lon = amapLocation.getLongitude();//获取经度
                            // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                            if (isFirstLoc) {
                                //获取定位信息
                                currentDistrict = amapLocation.getDistrict();
                                currentCity = amapLocation.getCity();
                                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15));
                                isFirstLoc = false;
                            }
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
        mRealm.close();
        SMSSDK.unregisterEventHandler(eventHandler);
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
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStackImmediate(0, 1);
        }
    }

    /********************************实现地图搜索功能***********************************************/
    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + mKeyWords);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keywords) {
        showProgressDialog();// 显示进度框
        currentPage = 1;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keywords, "", currentDistrict);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(10);
        // 设置查第一页
        query.setPageNum(currentPage);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //让定位点无法点击
        if (marker.getSnippet() == null || marker.getId() == "Marker1") {
            return true;
        }
        marker.showInfoWindow();
        aMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        if (oldMarker != null) {
            oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal));
        }
        oldMarker = marker;
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_select));
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //点击地图上没marker 的地方，隐藏inforwindow
        if (oldMarker != null) {
            oldMarker.hideInfoWindow();
            oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal));
        }

    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(HomeActivity.this, infomation);

    }


    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(HomeActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil.show(HomeActivity.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem item, int rCode) {
        // TODO Auto-generated method stub

    }

    /**
     * 输入提示activity选择结果后的处理逻辑
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_INPUTTIPS && data
                != null) {
            aMap.clear();
            Tip tip = data.getParcelableExtra(ConstantValue.EXTRA_TIP);
            if (tip.getPoiID() == null || tip.getPoiID().equals("")) {
                doSearchQuery(tip.getName());
            } else {
                addTipMarker(tip);
                addPointsAround();
            }
            mKeywordsTextView.setText(tip.getName());
            if (!tip.getName().equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if (resultCode == RESULT_CODE_KEYWORDS && data != null) {
            aMap.clear();
            String keywords = data.getStringExtra(ConstantValue.KEY_WORDS_NAME);
            if (keywords != null && !keywords.equals("")) {
                doSearchQuery(keywords);
            }
            mKeywordsTextView.setText(keywords);
            if (!keywords.equals("")) {
                mCleanKeyWords.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_RENT && resultCode == RESULT_OK) {
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal)));
            if (oldMarker != null) {
                if (oldMarker.isInfoWindowShown()) {
                    oldMarker.hideInfoWindow();
                    oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal));
                }
            }
            if (geoMarker.getPosition() != null) {
                geoMarker.hideInfoWindow();
            }
            id = data.getExtras().getInt("id");
            parkAddr = data.getExtras().getString("park_addr");
            start_time = data.getExtras().getString("start_time");
            end_time = data.getExtras().getString("end_time");
            cost = data.getExtras().getString("cost");
            //地理编码获取新加停车位的经纬度
            GeocodeQuery geocodeQuery = new GeocodeQuery(parkAddr, currentCity);
            geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
            geoMarker.setSnippet(parkAddr + "\n开始时间：\t" + start_time + "\n结束时间：\t"
                    + end_time + "\n单价：\t" + cost + "元/h" + "\n车位编号：\t" + id);
        } else if (requestCode == REQUEST_LEASE && resultCode == RESULT_OK) {
            //当把车位出租出去后，移除地图上的该车位
            String from_lease_info_id = data.getExtras().getString("from_lease_info_id");
            List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
            for (Marker marker : mapScreenMarkers) {
                //判断出事哪个车位出租了
                String temp_snippet = marker.getSnippet();
                //剔除内容为空的点标注
                if (temp_snippet != null) {
                    String[] snippet_infos = temp_snippet.split("\n");
                    //匹配id
                    if (snippet_infos.length >= 5) {
                        if (snippet_infos[4].equals(from_lease_info_id)) {
                            marker.remove();
                        }
                    }
                }
            }
        }
    }

    /*
    *   将搜索点附近1500m内的出租停车位添加到地图上
    * */
    private void addPointsAround() {
        //获取所有记录，并将方圆一千五百米内的停车位显示在地图上
        RealmResults<UserPark> allPoints = mRealm.where(UserPark.class).findAll();
        for (UserPark user : allPoints) {
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal)));
            query_park_addr = user.getPark_addr();
            mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            query_start_time = mFormat.format(user.getStart_time());
            query_end_time = mFormat.format(user.getEnd_time());
            query_cost = user.getCost();
            query_id = user.getId();
            query_quality = user.getQuality();
            double query_lat = user.getLat();
            double query_lon = user.getLon();
            LatLng latLng = new LatLng(query_lat, query_lon);
            //显示1500m内未租出的车位
            if (mCircle.contains(latLng) && !query_quality.equals("lease")) {
               /* Log.i("Test", query_park_addr);
                Log.i("Test", latLng.toString());
                Log.i("Test", query_quality);*/
                geoMarker.setPosition(latLng);
                geoMarker.setTitle("车位出租");
                geoMarker.setSnippet(query_park_addr + "\n开始时间：\t" + query_start_time + "\n结束时间：\t"
                        + query_end_time + "\n单价：\t" + query_cost + "元/h" + "\n车位编号：\t" + query_id);
            } else {
                Log.i("Test", "不在1500米内或已租出");
            }
        }
    }

    /**
     * 用marker展示输入提示list选中数据
     *
     * @param tip
     */
    private void addTipMarker(Tip tip) {
        if (tip == null) {
            return;
        }
        mPoiMarker = aMap.addMarker(new MarkerOptions());
        LatLonPoint point = tip.getPoint();
        if (point != null) {
            LatLng markerPosition = new LatLng(point.getLatitude(), point.getLongitude());
            mPoiMarker.setPosition(markerPosition);
            mCircle = aMap.addCircle(new CircleOptions().center(markerPosition)
                    .radius(1500).strokeColor(Color.argb(0, 0, 0, 0)));
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 14));
        }
        mPoiMarker.setTitle(tip.getName());
        mPoiMarker.setSnippet(tip.getAddress());
        mPoiMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal));
    }


    /**
     * 点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_keywords:
                Intent intent = new Intent(this, InputTipsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                aMap.clear();
                mCleanKeyWords.setVisibility(View.GONE);
                break;
            case R.id.rent_btn:
                Intent rentIntent = new Intent(HomeActivity.this, RentActivity.class);
                startActivityForResult(rentIntent, REQUEST_RENT);
            default:
                break;
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        //解析坐标
        if (i == 1000) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                //获取首个地址
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                //转化为经纬度，并包装起来
                LatLonPoint latLonPoint = address.getLatLonPoint();
                LatLng markerPosition = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                //给数据库中新加的停车位添加经纬度
                mRealm.beginTransaction();
                UserPark first_res = mRealm.where(UserPark.class).equalTo("park_addr", parkAddr)
                        .equalTo("id", id)
                        .equalTo("cost", cost)
                        .findFirst();
                if (first_res != null) {
                    first_res.setLat(latLonPoint.getLatitude());
                    first_res.setLon(latLonPoint.getLongitude());
                }
                mRealm.commitTransaction();
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 15));
                //设置点标记
                geoMarker.setPosition(markerPosition);
                geoMarker.setTitle("车位出租");
            } else {
                ToastUtil.show(this, "没有查询到结果！");
            }
        } else {
            ToastUtil.showerror(this, i);
        }
    }
}
