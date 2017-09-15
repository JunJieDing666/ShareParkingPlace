package com.jj.tidedemo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.navi.model.NaviLatLng;
import com.jj.tidedemo.Activity.LeaseInfoActivity;
import com.jj.tidedemo.Activity.RouteNaviActivity;
import com.jj.tidedemo.R;
import com.jj.tidedemo.Realm.UserPark;
import com.jj.tidedemo.Utils.ToastUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/9/4.
 */

public class InfoWinAdapter implements AMap.InfoWindowAdapter, View.OnClickListener {
    private static final int REQUEST_LEASE = 1;
    private Context mContext;
    private AMapLocation mCurrentLocation;
    private LatLng latLng;
    private LinearLayout rent;
    private LinearLayout navigation;
    private TextView nameTV;
    private String agentName;
    private TextView addrTV;
    private String snippet;

    public AMapLocation getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setmCurrentLocation(AMapLocation mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public InfoWinAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        snippet = marker.getSnippet();
        agentName = marker.getTitle();
    }

    @NonNull
    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.poikeywordsearch_uri, null);
        navigation = (LinearLayout) view.findViewById(R.id.navigation_LL);
        rent = (LinearLayout) view.findViewById(R.id.rent_LL);
        nameTV = (TextView) view.findViewById(R.id.name);
        addrTV = (TextView) view.findViewById(R.id.addr);

        nameTV.setText(agentName);
        addrTV.setText(String.format(mContext.getString(R.string.agent_addr), snippet));

        navigation.setOnClickListener(this);
        rent.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.navigation_LL:  //点击导航
                startAMapNavi();
                break;
            case R.id.rent_LL:  //点击租赁
                if (agentName == "车位出租"){
                    Intent intent = new Intent(mContext, LeaseInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("park_info",snippet);
                    intent.putExtras(bundle);
                    ((Activity) mContext).startActivityForResult(intent,REQUEST_LEASE);
                } else {
                    ToastUtil.show(mContext,"请选择正确车位");
                }
                break;
        }
    }

    private void startAMapNavi() {
        if (mCurrentLocation == null) {
            return;
        }
        Intent intent = new Intent(mContext, RouteNaviActivity.class);
        intent.putExtra("gps", true);
        intent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        intent.putExtra("end", new NaviLatLng(latLng.latitude, latLng.longitude));
        mContext.startActivity(intent);
    }

}
