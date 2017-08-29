package com.jj.tidedemo.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jj.tidedemo.Activity.HomeActivity;
import com.jj.tidedemo.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/7/3.
 */
public class VersionUpdateUtils {
    private static final int MESSAGE_NET_ERROR = 101;
    private static final int MESSAGE_IO_ERROR = 102;
    private static final int MESSAGE_JSON_ERROR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    protected static final int MESSAGE_ENTERHOME = 105;

    /*用于更新UI*/
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_IO_ERROR:
                    Toast.makeText(context, "IO异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_ERROR:
                    Toast.makeText(context, "JSON异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_ERROR:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOEW_DIALOG:
                    showUpdateDialog();
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    //将导航界面关闭
                    context.finish();
                    break;
            }
        }
    };


    /*本地版本号*/
    private String mVersion;
    private Activity context;
    private ProgressDialog mProgressDialog;
    private String tag = "SplashActivity";
    private String des = null;
    private String code = null;
    private String url = null;

    /*该工具类的构造方法，设定了当前的版本和主活动*/
    public VersionUpdateUtils(String Version, Activity activity) {
        mVersion = Version;
        context = activity;
    }

    public void getCloudVersion() {
        Message msg = Message.obtain();
        long startTime = System.currentTimeMillis();
        try {
            //封装url地址
            URL apkurl = new URL("http://172.20.10.2:8088/updateinfo.json");
            try {
                //开启一个链接
                HttpURLConnection connection = (HttpURLConnection) apkurl.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);
                if (connection.getResponseCode() == 200) {
                    InputStream is = connection.getInputStream();
                    String json = StreamUtil.stream2String(is);
                    JSONObject jsonObject = new JSONObject(json);
                    des = jsonObject.getString("des");
                    code = jsonObject.getString("code");
                    url = jsonObject.getString("apkurl");

                    if (!mVersion.equals(code)) {
                    /*版本号不一致*/
                        handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
                    /*相当于
                    * Message message=new Message();
                    * message.what=MESSAGE_SHOEW_DIALOG
                    * handler.sendMessage(message);*/
                    } else {
                        stopAtFlash(startTime);
                        handler.sendEmptyMessage(MESSAGE_ENTERHOME);
                    }
                }
            } catch (IOException e) {
                stopAtFlash(startTime);
                handler.sendEmptyMessage(MESSAGE_IO_ERROR);
                e.printStackTrace();
            } catch (JSONException e) {
                stopAtFlash(startTime);
                handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            stopAtFlash(startTime);
            handler.sendEmptyMessage(MESSAGE_NET_ERROR);
            e.printStackTrace();
        }
    }

    private void showUpdateDialog() {
        //创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检测到新版本：" + code);
        builder.setMessage(des);
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_launcher);
        /*设置立即升级按钮点击事件*/
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initProgressDialog();
                /*此处的apkurl是从html界面解析过来的下载地址*/
                //Log.w(tag, url);
                downloadNewApk(url);
            }
        });
        /*设置暂不升级按钮点击事件*/
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        /*对话框必须用show方法才能显示*/
        builder.show();
    }

    /*选择立即更新后初始化进度对话框*/
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("准备下载.....");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    /*下载最新版本*/
    protected void downloadNewApk(String apkurl) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            HttpUtils httpUtils = new HttpUtils();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Defense.apk";
            httpUtils.download(apkurl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Log.w(tag, "下载成功");
                    mProgressDialog.dismiss();
                    File file = responseInfo.result;
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Log.w(tag, "下载失败");
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    mProgressDialog.setMessage("下载失败");
                    mProgressDialog.dismiss();
                    enterHome();
                }

                @Override
                public void onStart() {
                    //Log.w(tag, "开始下载");
                    super.onStart();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.w(tag, "下载中");
                    Log.w(tag, "total--" + total);
                    Log.w(tag, "current--" + current);
                    super.onLoading(total, current, isUploading);
                    mProgressDialog.setMax((int) total);
                    mProgressDialog.setMessage("正在下载.....");
                    mProgressDialog.setProgress((int) current);
                }
            });
        }
    }

    protected void installApk(File file) {
        //安装APK的界面为系统自带，查看源码可得其入口，用隐式intent进入
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        //开启安装界面并等待结果
        context.startActivityForResult(intent, 0);
        //签名和包名必须一致才能安装
    }

    //对外的接口，用于处理活动返回结果的方法
    public void onActivityResultSendMessage() {
        enterHome();
    }

    private void stopAtFlash(long startTime) {
        //强制在splash界面停4秒钟
        long endTime = System.currentTimeMillis();
        if (endTime - startTime < 3000) {
            try {
                Thread.sleep((3000 - (endTime - startTime)));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void enterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 0);
    }

    /**
     * 当设置成不自动更新时splash界面使用的进入主界面的方法
     */
    public void notUpdateEnterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 3000);
    }
}
