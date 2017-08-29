package com.jj.tidedemo.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jj.tidedemo.R;

/**
 * Created by Administrator on 2016/12/20.
 */

public class MyTopBarView extends RelativeLayout {

    private TextView top_bar_title;
    private Button top_bar_left_btn;
    private Button top_bar_right_btn;
    //private static final String NAME_SPACE = "http://schemas.android.com/apk/res/com.jj.tidedemo";
    private String title_text = null;
    private Integer left_btn_background = null;
    private Integer right_btn_background = null;
    private onLeftAndRightBtnClickListener listener = null;

    /**
     * 设置左按钮可视性的方法
     *
     * @param flag 是否可见
     */
    public void setLeftBtnVisible(boolean flag) {
        if (flag) {
            top_bar_left_btn.setVisibility(VISIBLE);
        } else {
            top_bar_left_btn.setVisibility(INVISIBLE);
        }
    }

    /**
     * 设置右按钮可视性的方法
     *
     * @param flag 是否可见
     */
    public void setRightBtnVisible(boolean flag) {
        if (flag) {
            top_bar_right_btn.setVisibility(VISIBLE);
        } else {
            top_bar_right_btn.setVisibility(INVISIBLE);
        }
    }


    /**
     * 声明一个左右按钮点击事件的接口
     */
    public interface onLeftAndRightBtnClickListener {
        public void onLeftBtnClick();

        public void onRightBtnClick();
    }

    /**
     * 将实例中写的监听器设置给该类的监听器，以获得实例的左右点击事件处理逻辑
     *
     * @param listener
     */
    public void setOnLeftAndRightBtnClickListener(onLeftAndRightBtnClickListener listener) {
        this.listener = listener;
    }


    public MyTopBarView(Context context) {
        this(context,null);
    }

    public MyTopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTopBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //将标题栏的样式转化成view并挂载在此类上
        LayoutInflater.from(context).inflate(R.layout.my_topbar_view, this);
        top_bar_title = (TextView) findViewById(R.id.top_bar_title);
        top_bar_left_btn = (Button) findViewById(R.id.top_bar_left_btn);
        top_bar_right_btn = (Button) findViewById(R.id.top_bar_right_btn);
        //初始化标题栏的属性内容
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        //获得属性值集合
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTopBar);
        title_text = typedArray.getString(R.styleable.MyTopBar_title_text);
        left_btn_background = typedArray.getResourceId(R.styleable.MyTopBar_left_btn_background, 0);
        right_btn_background = typedArray.getResourceId(R.styleable.MyTopBar_right_btn_background, 0);
        //释放资源并将属性设置到view上
        typedArray.recycle();
        top_bar_title.setText(title_text);
        top_bar_left_btn.setBackgroundResource(left_btn_background);
        top_bar_right_btn.setBackgroundResource(right_btn_background);

        top_bar_left_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLeftBtnClick();
            }
        });

        top_bar_right_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRightBtnClick();
            }
        });
    }
}
