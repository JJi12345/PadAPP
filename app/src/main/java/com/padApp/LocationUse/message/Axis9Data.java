package com.padApp.LocationUse.message;

/*
 * 文件名：九轴数据
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class Axis9Data extends IMUData {

    public static final byte CMD_START_9_AXIS_1_SYMBOL = (byte) 0xAA;//九轴命令开始符号位字符1"0xAA";
    public static final byte CMD_START_9_AXIS_2_SYMBOL = 0x33;//九轴命令开始符号位字符2"0x33";

    public static final String TYPE=String.valueOf(CMD_START_9_AXIS_2_SYMBOL);
    public static final String PREFIX=String.valueOf(CMD_START_9_AXIS_1_SYMBOL)+TYPE;

    private static final int IMU_DATA_LENGTH = 23;//imu九轴数据长度

    public Axis9Data() {
        super(PREFIX);
    }

    public static boolean isMatch(byte[] data) {
        if(null!=data && data.length==IMU_DATA_LENGTH){
            return data[0]==CMD_START_9_AXIS_1_SYMBOL && data[1]==CMD_START_9_AXIS_2_SYMBOL;
        }
        return false;
    }
}
