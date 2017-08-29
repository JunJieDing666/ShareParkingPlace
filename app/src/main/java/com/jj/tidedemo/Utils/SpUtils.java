package com.jj.tidedemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/7/10.
 */
public class SpUtils {
    private static SharedPreferences sp = null;

    /**
     * 将boolean对象存入sp
     *
     * @param ctx   上下文环境
     * @param key   存储节点名称
     * @param value 存储节点值
     */
    public static void putBoolean(Context ctx, String key, Boolean value) {
        //config是存储文件名，key是存储节点名称

        //判断sp对象是否已经存在
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 从sp读出boolean对象
     *
     * @param ctx      上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点的默认值
     * @return
     */
    public static boolean getBoolean(Context ctx, String key, Boolean defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 将String对象存入sp
     *
     * @param ctx   上下文环境
     * @param key   存储节点名称
     * @param value 存储节点值
     */
    public static void putString(Context ctx, String key, String value) {
        //config是存储文件名，key是存储节点名称

        //判断sp对象是否已经存在
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 从sp读出String对象
     *
     * @param ctx      上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点的默认值
     * @return
     */
    public static int getInt(Context ctx, String key, int defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }

    public static void putInt(Context ctx, String key, int value) {
        //config是存储文件名，key是存储节点名称

        //判断sp对象是否已经存在
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    /**
     * 从sp读出String对象
     *
     * @param ctx      上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点的默认值
     * @return
     */
    public static String getString(Context ctx, String key, String defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    /**
     *  从sp中指定节点删除
     * @param ctx   上下文
     * @param key   删除节点的名称
     */
    public static void remove(Context ctx,String key) {
        //判断sp对象是否已经存在
        if (sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }
}
