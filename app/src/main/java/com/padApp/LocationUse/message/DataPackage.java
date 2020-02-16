package com.padApp.LocationUse.message;

import com.padApp.LocationUse.utils.ByteUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * 文件名：数据包
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.message
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class DataPackage extends AbstractPackage{
    private static final byte START_SYMBOL= (byte) 0xAA;//开始符
    private static final byte END_SYMBOL= (byte) 0xFF;//结束符

    private List<AbstractData> _data=new ArrayList<AbstractData>();


    public DataPackage() {
    }

    public void addData(AbstractData data){
        _data.add(data);
    }

    public void removeData(AbstractData data){
        _data.remove(data);
    }

    public void clearData(){
        _data.clear();
    }

    @Override
    public void deserialize(byte[] bytes) {
        super.deserialize(bytes);
    }

    @Override
    public byte[] serialize() {
        if(null!=_data){
            List<byte[]> dataBufferList=new ArrayList<>();
            int totalLength=0;
            for(AbstractData data:_data){
                final byte[] serialize = data.serialize();
                dataBufferList.add(serialize);
                totalLength+=serialize.length;
            }
            //开始符号
            int index=0;
            byte[] buffer=new byte[1+4+totalLength+1];//数据缓存，
            buffer[index]=START_SYMBOL;
            //总的数据长度
            index=index+1;
            final byte[] lengthBytes = ByteUtil.intToByte(totalLength);
            System.arraycopy(lengthBytes,0,buffer,index,lengthBytes.length);
            index=index+lengthBytes.length;
            for(int i=0;i<dataBufferList.size();i++){
                //处理复制单个数据
                final byte[] dataBuffer = dataBufferList.get(i);
                System.arraycopy(dataBuffer,0,buffer,index,dataBuffer.length);
                index=index+dataBuffer.length;
            }
            //结束符号
            buffer[index]=END_SYMBOL;
            return buffer;
        }
        return super.serialize();
    }
}
