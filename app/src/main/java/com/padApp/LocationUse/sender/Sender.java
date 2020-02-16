package com.padApp.LocationUse.sender;

/*
 * 文件名：
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.sender
 * 修改时间：2019/11/27
 * 修改内容：新增
 * 创建者：hewei
 */
public class Sender extends Thread {

    protected boolean _cancel=false;

    public void cancel(){
        _cancel=true;
    }


    @Override
    public void run() {
        super.run();
    }
}
