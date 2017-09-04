package com.jj.tidedemo.Fragment;

/**
 * Created by Administrator on 2017/8/30.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jj.tidedemo.R;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class ContentFragment extends Fragment implements ScreenShotable {
    public static final String CLOSE = "Close";
    public static final String BUILDING = "Building";
    public static final String BOOK = "Book";
    public static final String PAINT = "Paint";
    public static final String CASE = "Case";
    public static final String SHOP = "Shop";
    public static final String PARTY = "Party";
    public static final String MOVIE = "Movie";

    private View containerView;
    protected ImageView mImageView;
    protected String res;
    private Bitmap bitmap;
    private View mRootView;

    public static ContentFragment newInstance(String resStr) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(String.class.getName(), resStr);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getArguments().getString(String.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        switch (res) {
            case ContentFragment.BUILDING:
                mRootView = inflater.inflate(R.layout.fragment_parking_his, container, false);
                return mRootView;
            case ContentFragment.BOOK:
                mRootView = inflater.inflate(R.layout.fragment_wallet, container, false);
                mImageView = (ImageView) mRootView.findViewById(R.id.image_content);
                //获得焦点，使底层activity无法点击
                mImageView.setClickable(true);
                mImageView.setFocusable(true);
                return mRootView;
            case ContentFragment.PAINT:
                mRootView = inflater.inflate(R.layout.fragment_help, container, false);
                return mRootView;
            case ContentFragment.CASE:
                mRootView = inflater.inflate(R.layout.fragment_setting, container, false);
                return mRootView;
            default:
                mRootView = inflater.inflate(R.layout.fragment_home, container, false);
                return mRootView;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switch (res) {
            //在此声明各个fragment对应布局上的控件方法
            case ContentFragment.BUILDING:
                return;
            case ContentFragment.BOOK:
                Button btn = (Button) getActivity().findViewById(R.id.btn_test);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "You have click this button.", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            case ContentFragment.PAINT:
                return;
            case ContentFragment.CASE:
                return;
            default:
                return;
        }
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContentFragment.this.bitmap = bitmap;
            }
        };

        thread.start();

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}
