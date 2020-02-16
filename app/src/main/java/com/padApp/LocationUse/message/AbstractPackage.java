package com.padApp.LocationUse.message;

/*
 * 文件名：
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public abstract class AbstractPackage implements SerializableObject {

    public void deserialize(byte[] bytes) {

    }

    public byte[] serialize() {
        return new byte[0];
    }
}
