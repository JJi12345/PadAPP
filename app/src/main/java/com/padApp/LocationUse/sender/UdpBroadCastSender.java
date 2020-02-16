package com.padApp.LocationUse.sender;

import com.padApp.LocationUse.cache.DataCache;
import com.padApp.LocationUse.config.AppConfig;
import com.padApp.LocationUse.message.AbstractData;
import com.padApp.LocationUse.message.DataPackage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.List;

/*
 * 文件名：udp广播
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.sender
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class UdpBroadCastSender extends Sender{

    private MulticastSocket _sender = null;
    private DatagramPacket _datagramPacket = null;
    private InetAddress _address = null;


    public UdpBroadCastSender() {
    }


    @Override
    public void run() {

        try {
            _sender = new MulticastSocket();
            while (!_cancel && !interrupted()){
                _address = InetAddress.getByName(AppConfig.getInstance().getIp());
                final List<AbstractData> datas = DataCache.getInstance().popAll();
                if(null!=datas && datas.size()>0) {
                    //发送数据到服务器.
                    DataPackage dataPackage = new DataPackage();
                    for (AbstractData data : datas) {
                        dataPackage.addData(data);
                    }
                    final byte[] serialize = dataPackage.serialize();
                    try{
                        _datagramPacket = new DatagramPacket(serialize,serialize.length,_address,AppConfig.getInstance().getPort());
                        _sender.send(_datagramPacket);
                    }catch (SocketException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                sleep(500);//休眠
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            _sender.close();
        }
    }
}
