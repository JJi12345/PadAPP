package com.padApp.LocationUse.sender;

import com.padApp.LocationUse.BdGpsCtrl.MyFunc;
import com.padApp.LocationUse.cache.DataCache ;
import com.padApp.LocationUse.message.*;
import locating.IOface.DataAnalyser;
import locating.IOface.DataCalcCallback;
import locating.IOface.DataInterface;

/*
 * 文件名：本地分析器
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.sender
 * 修改时间：2019/11/27
 * 修改内容：新增
 * 创建者：hewei
 */
public class LocalAnalyserSender extends Sender {

    private static final String TAG = LocalAnalyserSender.class.getSimpleName();

    private DataInterface _dataInterface;

    public LocalAnalyserSender(DataInterface dataInterface) {
        _dataInterface = dataInterface;
    }

    public LocalAnalyserSender(DataCalcCallback callback) {
        _dataInterface = new DataAnalyser(callback);
    }

    @Override
    public void run() {

        try {
            while (!_cancel && !interrupted()) {
                final AbstractData data = DataCache.getInstance().pop();
                if (null != data && null != _dataInterface) {
                    //发送数据
                    if (data instanceof GNGGAData) {
                        GNGGAData gxData = (GNGGAData) data;
                        final byte[] sourceData = gxData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            //dataValue="$GNGGA,000208.000,3110.699347,N,12124.601985,E,0,00,127.000,-56.675,M,0,M,,*7B";
                            //Log.d(TAG,"发送GPS数据:"+dataValue);
                            // _dataInterface.Event(dataValue,null);
                            _dataInterface.eventGNSS(dataValue);
                        }
                    }
                    if (data instanceof GNRMCData) {
                        GNRMCData gxData = (GNRMCData) data;
                        final byte[] sourceData = gxData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            //dataValue="$GNRMC,000203.000,A,3110.699347,N,12124.601985,E,0.000,0.000,,,E,A*2E";
                            //Log.d(TAG,"发送GPS数据:"+dataValue);
                            // _dataInterface.Event(dataValue,null);
                            _dataInterface.eventGNSS(dataValue);
                        }
                    }
                    if (data instanceof GPGGAData) {
                        GPGGAData gxData = (GPGGAData) data;
                        final byte[] sourceData = gxData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            _dataInterface.eventGNSS(dataValue);
                        }
                    }
                    if (data instanceof GPRMCData) {
                        GPRMCData gxData = (GPRMCData) data;
                        final byte[] sourceData = gxData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            _dataInterface.eventGNSS(dataValue);
                        }
                    }
                    if (data instanceof GBGGAData) {
                        GBGGAData gxData = (GBGGAData) data;
                        final byte[] sourceData = gxData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            _dataInterface.eventGNSS(dataValue);
                        }
                    }
                    if (data instanceof GBRMCData) {
                        GBRMCData gxData = (GBRMCData) data;
                        final byte[] sourceData = gxData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            _dataInterface.eventGNSS(dataValue);
                        }
                    }
                    if (data instanceof IMUData) {
                        IMUData imuData = (IMUData) data;
                        final byte[] sourceData = imuData.getData();
                        if (null != sourceData) {
                            String dataValue = new String(sourceData);
                            final String hexImuData = MyFunc.ByteArrToHex(sourceData);
                            //Log.d(TAG,"发送IMU数据:"+hexImuData);
                            // _dataInterface.Event("",sourceData);
                            _dataInterface.eventIMU(sourceData);
                        }
                    }
                }
//                sleep(500);//休眠
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
