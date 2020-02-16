package com.padApp.LocationUse.message;

/*
 * 文件名：
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.cardupgrade.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public abstract class AbstractData implements SerializableObject {

    public static final int GNSS=0;
    public static final int IMU=1;

    protected int _dataType=GNSS;//数据类型标示，GNSS或IMU
    protected String _device="";//设备标示
    protected String _prefix="";//数据前缀
    protected byte _order=0;//序号 0-255
    protected byte[] _data;

    public AbstractData(int dataType) {
        this._dataType=dataType;
        this._prefix = "";
    }

    public AbstractData(int dataType, String prefix) {
        this._dataType=dataType;
        this._prefix = prefix;
    }

    public String getPrefix() {
        return _prefix;
    }

    public String getDevice() {
        return _device;
    }

    public void setDevice(String device) {
        this._device = device;
    }


    public byte getOrder() {
        return _order;
    }

    public void setOrder(byte order) {
        this._order = order;
    }

    /**
     * 获取原始串口数据
     * @return
     */
    public byte[] getData(){
        return _data;
    }
    /**
     * 序列化
     * @return
     */
    public abstract byte[] serialize();

    /**
     * 反序列
     */
    public void deserialize(byte[] bytes){
        this._data=bytes;
    }

    @Override
    public String toString() {
        if(null==_data){
            _data=new byte[0];
        }
        String order= String.valueOf(_order);
        return "AbstractData{" +
                "_dataType=" + _dataType +
                ", _device='" + _device + '\'' +
                ", _prefix='" + _prefix + '\'' +
                ", _order=" + order + '\'' +
                ", _data='" + new String(_data) +
                '}';
    }
}
