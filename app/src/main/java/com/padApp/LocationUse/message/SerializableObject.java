package com.padApp.LocationUse.message;

import java.io.Serializable;

/*
 * 文件名：序列化对象
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public interface SerializableObject extends Serializable {
    /**
     * 反序列化
     * @param bytes
     */
    void deserialize(byte[] bytes);

    /**
     * 序列化
     * @return
     */
    byte[] serialize();
}
