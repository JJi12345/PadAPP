package com.padApp.LocationUse;

import android.app.Application;
import com.padApp.LocationUse.config.AppConfig;
import com.padApp.LocationUse.utils.SharedPreUtils;

/*
 * 文件名：
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class App extends Application {

    private static App _instance;

    public App() {
        super();
    }

    public static App getInstance(){
        return _instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        _instance=this;
        final String serverIp = SharedPreUtils.getServerIp(this);
        final int serverPort = SharedPreUtils.getServerPort(this);
        AppConfig.getInstance().setIp(serverIp);
        AppConfig.getInstance().setPort(serverPort);
    }

    /**
     * 获取设备序列号
     * @return
     */
//    public String getDeviceId(){
//        String seri;
//        seri= android.os.Build.SERIAL;
//        Log.d("1121","");
//        if (seri==null){
//            return "";
//        }
//        else
//            return seri;
//
//    }
}
