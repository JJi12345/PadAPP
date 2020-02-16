package com.padApp.LocationUse.bean;

import java.text.SimpleDateFormat;

/**
 * 串口数据
 */
public class ComBean {
    private byte[] bRec = null;
    private byte[] mNav = null;    //xuefengli + : nav mode , BD,GPS,BD+GPS
    private String sRecTime = "";
    private String sComPort = "";

    public ComBean(String sPort, byte[] buffer, int size) {
        sComPort = sPort;
        mNav = new byte[1];    //xuefengli +
        bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            bRec[i] = buffer[i];
            //xuefengli +
            if (buffer[i] == '$' && i + 1 < size && i + 2 < size) {
                if ((buffer[i + 1] == 'B') && (buffer[i + 2] == 'D'))    //BD mode
                    mNav[0] = '0';
                else if ((buffer[i + 1] == 'G') && (buffer[i + 2] == 'P'))    //GPS mode
                    mNav[0] = '1';
                else if ((buffer[i + 1] == 'G') && (buffer[i + 2] == 'N'))    //GPS+BD mode
                    mNav[0] = '2';
            }
            //xuefengli -
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        sRecTime = sDateFormat.format(new java.util.Date());
    }
}