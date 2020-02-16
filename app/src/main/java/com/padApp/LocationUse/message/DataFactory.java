package com.padApp.LocationUse.message;


/*
 * 文件名：回复消息工厂
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.cardupgrade.message
 * 修改时间：2019/5/10
 * 修改内容：新增
 * 创建者：hewei
 */
public class DataFactory {

    public static int GNSS_ORDER=0;
    public static int IMU_ORDER=0;

    /**
     * 创建相关回复对象
     * @param data
     * @return
     */
    public static AbstractData createData(byte[] data){
        int order= GNSS_ORDER++%0xFF;
//        final String deviceId = App.getInstance().getDeviceId();
        return createData(data,order);
    }

    public static AbstractData createData(byte[] data,int order){
        AbstractData record=null;
        if(null!=data){
            String res=new String(data).trim();
            if(res.contains(GNRMCData.TYPE)){
                record=new GNRMCData();//GNRMC
            }else if(res.contains(GNGGAData.TYPE)){
                record=new GNGGAData();//升级请求数据包命令
            }else if(res.contains(GNGLLData.TYPE)){
                record=new GNGLLData();
            }else if(res.contains(GNGSAData.TYPE)){
                record=new GNGSAData();
            }else if(res.contains(GPGSVData.TYPE)){
                record=new GPGSVData();
            }else if(res.contains(GPRMCData.TYPE)){
                record=new GPRMCData();
            }else if(res.contains(GPGGAData.TYPE)){
                record=new GPGGAData();
            }else if(res.contains(GPGLLData.TYPE)){
                record=new GPGLLData();
            }else if(res.contains(GPGSAData.TYPE)){
                record=new GPGSAData();
            }else if(res.contains(GBGGAData.TYPE)){
                record=new GBGGAData();
            }else if(res.contains(GBRMCData.TYPE)){
                record=new GBRMCData();
            }else if(res.contains(BDGSVData.TYPE)){
                record=new BDGSVData();
            }else if(Axis9Data.isMatch(data)){
                record=new Axis9Data();//IMU, 获取卡片配置回复
            }
            if(null!=record){
                record.setOrder((byte) order);
                record.deserialize(data);
            }
        }
        return record;
    }

}
