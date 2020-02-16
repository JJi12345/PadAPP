package com.padApp.LocationUse.message;

/*
 * 文件名：GBGSV
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class BDGSVData extends GPSData {

    public static final String TYPE="GBGSV";
    public static final String PREFIX="$"+TYPE;

    public BDGSVData() {
        super(PREFIX);
    }

//    public byte[] serialize() {
//        return new byte[0];
//    }
//
//    public void deserialize(byte[] bytes) {
//
//    }
}
