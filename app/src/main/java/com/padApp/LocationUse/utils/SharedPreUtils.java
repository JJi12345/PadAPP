package com.padApp.LocationUse.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtils {
    private static final String SP_NAME = "heweisoftdxcg";

    /**
     * 返回一个SharedPrefrence编辑对象
     *
     * @param context
     * @param mode
     * @return
     */
    public static SharedPreferences.Editor getEditor(Context context, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, mode);
        return sharedPreferences.edit();
    }

    /**
     * 返回一个私有的编辑对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences.Editor getEditor(Context context) {
        return getEditor(context, Context.MODE_PRIVATE);
    }

    /**
     * 返回一个私有的SharedPreference对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context
                .MODE_PRIVATE);
        return sharedPreferences;
    }

    /**
     * 返回String对象
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    /**
     * 返回String对象，如不存在，则返回默认值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    /**
     * 写入sharedPreferences
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putString(Context context, String key, String value) {
        return getEditor(context).putString(key, value).commit();
    }

    /**
     * 返回Boolean
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    /**
     * 写入一个boolean
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        return getEditor(context).putBoolean(key, value).commit();
    }

    /**
     * 返回Integer
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(Context context, String key, int defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    /**
     * 写入一个Integer
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putInt(Context context, String key, int value) {
        return getEditor(context).putInt(key, value).commit();
    }

    /**
     * 返回long
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static long getLong(Context context, String key, long defaultValue) {
        return getSharedPreferences(context).getLong(key, defaultValue);
    }

    /**
     * 写入一个long
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putLong(Context context, String key, long value) {
        return getEditor(context).putLong(key, value).commit();
    }

    /**
     * 清除SharedPre中的数据
     *
     * @param context
     */
    public static boolean clearSharedPre(Context context) {
        return getEditor(context).clear().commit();
    }

    /**
     * 清除对应key的值
     *
     * @param context
     * @param key
     */
    public static boolean removeSharedKey(Context context, String key) {
        return getEditor(context).remove(key).commit();
    }

    /**
     * 获取服务器ip
     *
     * @param context
     * @return
     */
    public static String getServerIp(Context context) {
        return SharedPreUtils.getString(context, Config.SERVER_IP);
    }

    /**
     * 设置服务器ip
     * @param context
     * @param ip
     */
    public static boolean setServerIp(Context context, String ip) {
       return SharedPreUtils.putString(context, Config.SERVER_IP, ip);
    }

    /**
     * 获取服务器端口
     *
     * @param context
     * @return
     */
    public static int getServerPort(Context context) {
        return SharedPreUtils.getInt(context, Config.SERVER_PORT,0);
    }

    /**
     * 设置服务器端口
     * @param context
     * @param port
     */
    public static boolean setServerPort(Context context, int port) {
        return SharedPreUtils.putInt(context, Config.SERVER_PORT, port);
    }


    public static abstract class Config {
        public static final String SERVER_IP = "server_ip";//服务器ip
        public static final String SERVER_PORT = "server_port";//服务器端口
    }
}
