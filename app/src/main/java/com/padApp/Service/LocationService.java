package com.padApp.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;

public class LocationService extends Service {
    private String TAG = "LocationService";
    private boolean quit;
    String res ="";
    private int hight = 0;
    private LocationBinder binder = new LocationBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"Bind");
        return binder;
    }

    @Override
    public void onCreate() {
        System.out.println("onCreate invoke");
        super.onCreate();
        startServce();
    }
    public class LocationBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public LocationService getService(){
            return LocationService.this;
        }
    }
    /**
     * 解除绑定时调用
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service is invoke onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is invoke Destroyed");
        this.quit = true;
        super.onDestroy();
    }


    public String getLocation() {
        return res;
    }

    /**
     * 模拟下载任务，每秒钟更新一次
     */
    public void startServce(){
        HashMap<String ,String> map = new HashMap<>();

        map.put("latitude","39");
        map.put("longitude","116.1");
        map.put("height","76");
        map.put("state","walk");

        Handler location_handler = new Handler();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                map.put("stepCounter", String.valueOf(hight));
                hight += 5;
                hight = hight % 200;
                res = JSON.toJSONString(map);
                System.out.println("res===" + res);
                location_handler.postDelayed(this, 1000);
            }
        };

        location_handler.post(runnable);
    }



}
