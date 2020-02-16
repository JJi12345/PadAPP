package com.padApp.LocationUse.message;

import android.text.TextUtils;
import com.padApp.LocationUse.utils.ByteUtil;


/*
 * 文件名：GPS数据
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class GPSData extends AbstractData {

    public GPSData(String prefix) {
        super(AbstractData.GNSS, prefix);
    }


    public byte[] serialize() {
        if (null != _data) {
            int length = _data.length;
            byte[] serializeBytes = new byte[1 + 20 + 4 + 1 + length];//类型长度+设备标示+数据长度+序号+数据内容
            //数据类型
            int index = 0;
            final byte[] bytes = ByteUtil.intToByte(_dataType);
            if (null != bytes && bytes.length > 0) {
                serializeBytes[index] = bytes[0];
            }

            //设备标示
            index = index + 1;
            byte[] deviceBytes = new byte[20];//设置标示
            if (!TextUtils.isEmpty(_device)) {
                final byte[] devBytes = _device.getBytes();
                System.arraycopy(devBytes, 0, deviceBytes, 0, devBytes.length);
            }
            System.arraycopy(deviceBytes, 0, serializeBytes, index, deviceBytes.length);

            //数据长度
            index = index + 20;
            final byte[] lengthBytes = ByteUtil.intToByte(length + 1);
            System.arraycopy(lengthBytes, 0, serializeBytes, index, lengthBytes.length);

            //序号
            index = index + 4;
            serializeBytes[index] = _order;

            //数据内容
            index = index + 1;
            System.arraycopy(_data, 0, serializeBytes, index, _data.length);

            return serializeBytes;
        }
        return new byte[0];
    }

    public void deserialize(byte[] bytes) {
        super.deserialize(bytes);
    }


}
