package com.padApp.LocationUse.config;

/*
 * 文件名：app配置
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class AppConfig {

    private static AppConfig _instance;
    private String _ip;
    private int _port;


    private AppConfig() {
    }

    private AppConfig(String ip, int port) {
        this._ip = ip;
        this._port = port;
    }

    public static synchronized AppConfig getInstance(){
        if(null==_instance){
            _instance=new AppConfig();
        }
        return _instance;
    }

    public String getIp() {
        return _ip;
    }

    public void setIp(String ip) {
        this._ip = ip;
    }

    public int getPort() {
        return _port;
    }

    public void setPort(int port) {
        this._port = port;
    }
}
