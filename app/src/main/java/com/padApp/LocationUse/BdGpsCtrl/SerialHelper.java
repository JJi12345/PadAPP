package com.padApp.LocationUse.BdGpsCtrl;

import android_serialport_api.SerialPort;
import com.padApp.LocationUse.bean.ComBean;
import com.padApp.LocationUse.cache.DataListener;
import com.padApp.LocationUse.message.*;
import com.padApp.LocationUse.utils.LogUtil;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * 串口辅助工具类
 */
public abstract class SerialHelper {
    private final static String TAG = SerialHelper.class.getSimpleName();

    public static final int INVALIDE_INDEX = -1;//无效索引
    public static final byte CMD_START_SYMBOL = 0x24;//命令开始符号位字符"$";
    public static final byte CMD_STOP_1_SYMBOL = 0x0D;//命令结束1符号位字符"/r",换行;
    public static final byte CMD_STOP_2_SYMBOL = 0x0A;//命令结束2符号位字符"/n"，回车;

    public static final byte CMD_START_9_AXIS_1_SYMBOL = (byte) 0xAA;//九轴命令开始符号位字符1"0xAA";
    public static final byte CMD_START_9_AXIS_2_SYMBOL = 0x33;//九轴命令开始符号位字符2"0x33";
    public static final byte CMD_STOP_9_AXIS_SYMBOL = (byte) 0xFF;//九轴命令结束符号位字符"0xFF"

    public static final byte CMD_START_HIGH_COUNT_1_SYMBOL = (byte) 0xAA;//高度计数命令开始符号位字符1"0xAA";
    public static final byte CMD_START_HIGH_COUNT_2_SYMBOL = 0x77;//高度计数命令开始符号位字符2"0x77";
    public static final byte CMD_STOP_HIGH_COUNT_SYMBOL = (byte) 0xFF;//高度计数命令结束符号位字符"0xFF"

    private static int GNSS_ORDER = 0;
    private static int IMU_ORDER = 0;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private String sPort = "/dev/s3c2410_serial0";
    private int iBaudRate = 9600;
    private boolean _isOpen = false;
    private byte[] _bLoopData = new byte[]{0x30};
    private int iDelay = 500;
    private ByteArrayOutputStream _baoStream = new ByteArrayOutputStream();

    private List<DataListener> _listenerList = new ArrayList<>();

    //----------------------------------------------------
    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    public SerialHelper() {
        this("/dev/ttyS1", 9600);
    }

    public SerialHelper(String sPort) {
        this(sPort, 9600);
    }

    public SerialHelper(String sPort, String sBaudRate) {
        this(sPort, Integer.parseInt(sBaudRate));
    }

    //----------------------------------------------------
    public void open() throws SecurityException, IOException, InvalidParameterException {

        try {
            mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
        }catch (Exception e){

            e.printStackTrace();
        }

        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
        _isOpen = true;
    }

    //----------------------------------------------------
    public void close() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        _isOpen = false;
    }

    //----------------------------------------------------
    public void send(byte[] bOutArray) {
        try {
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------
    public void sendHex(String sHex) {
        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    //----------------------------------------------------
    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }

    /**
     * 添加数据监听器
     *
     * @param listener
     */
    public void addDataListener(DataListener listener) {
        if (!_listenerList.contains(listener)) {
            _listenerList.add(listener);
        }
    }

    /**
     * 移除数据监听器
     *
     * @param listener
     */
    public void removeDataListener(DataListener listener) {
        _listenerList.remove(listener);
    }

    /**
     * 清空数据监听器
     */
    public void clearDataListener() {
        _listenerList.clear();
    }


    /**
     * 获取命令开始符号位索引
     *
     * @param buffer 缓存数据
     * @return 开始索引
     */
    protected int getCmdStartIndex(byte[] buffer) {
        if (null != buffer
                && buffer.length > 0) {
            for (int i = 0; i < buffer.length; i++) {
                if (buffer[i] == CMD_START_SYMBOL) {
                    return i;
                }
            }
        }
        return INVALIDE_INDEX;
    }

//    protected int getCmdStartIndex(byte[] buffer) {
//        if (null != buffer
//                && buffer.length > 2) {
//            for (int i = 0; i < buffer.length; i++) {
//                if (buffer[i] == 0xAA && (i + 1) < buffer.length && (buffer[i + 1] == 0x33 || buffer[i + 1] == 0x77)) {
//                    return i;
//                }
//            }
//        }
//        return INVALIDE_INDEX;
//    }

    /**
     * 获取命令结束符号位索引
     *
     * @param buffer 缓存数据
     * @return 结束符索引
     */
    protected int getCmdStopIndex(int startIndex, byte[] buffer) {
        if (null != buffer
                && buffer.length >= 2 && startIndex < buffer.length && startIndex >= 0) {
            for (int i = startIndex; i < buffer.length; i++) {
                if (buffer[i] == CMD_STOP_2_SYMBOL
                        && i > 0
                        && buffer[i - 1] == CMD_STOP_1_SYMBOL) {
                    //判断命令/r/n结束符
                    return i;
                }
            }
        }
        return INVALIDE_INDEX;
    }

//    protected int getCmdStopIndex(int startIndex, byte[] buffer) {
//        if (null != buffer
//                && buffer.length >= 2 && startIndex < buffer.length && startIndex >= 0) {
//            for (int i = startIndex; i < buffer.length; i++) {
//                if (buffer[i] == 0xFF) {
//                    //判断命令0xFF结束符
//                    return i;
//                }
//            }
//        }
//        return INVALIDE_INDEX;
//    }

    /**
     * 通知收到数据
     *
     * @param data
     */
    private void onDataResponse(AbstractData data) {
        //int r = (int) (2 * Math.random());
        if (data instanceof GNGGAData || data instanceof GNRMCData || data instanceof GPGGAData || data instanceof GPRMCData || data instanceof GBGGAData || data instanceof GBRMCData) {
            int order = GNSS_ORDER++ % 0xFF;
            data.setOrder((byte) order);
        }
        if (data instanceof Axis9Data) {
            int order = IMU_ORDER++ % 0xFF;
            data.setOrder((byte) order);
        }
        // Log.d(TAG, "发送数据:" + data.toString());
        for (DataListener listener : _listenerList) {
            if ((data instanceof GNGGAData || data instanceof GNRMCData || data instanceof GPGGAData || data instanceof GPRMCData || data instanceof GBGGAData || data instanceof GBRMCData) || data instanceof Axis9Data) {
                //缓存数据
                listener.onDataReceived(data);
            }
        }
    }

    /**
     * 分析数据
     *
     * @param buffer
     * @return
     */
    public byte[] analyse(byte[] buffer) {
        if (null != buffer
                && buffer.length > 0) {
            try {
//                final String text = new String(buffer, "utf-8");
                final int startSymbolIndex = getCmdStartIndex(buffer);//开始符号索引
                int endSymbolIndex = -1;
                if (startSymbolIndex >= 0) {
                    //找到匹配协议数据开始符号索引
                    endSymbolIndex = getCmdStopIndex(startSymbolIndex, buffer);//结束符号索引
                }
                if (startSymbolIndex >= 0
                        && endSymbolIndex > 0
                        && endSymbolIndex > startSymbolIndex) {
                    int len = endSymbolIndex + 1 - startSymbolIndex;
                    byte[] cmdBuffer = new byte[len];
                    System.arraycopy(buffer, startSymbolIndex, cmdBuffer, 0, len);
                    final AbstractData data = DataFactory.createData(cmdBuffer);
                    if (null != data) {
                        onDataResponse(data);//通知收到数据
                    }
                    final int length = buffer.length;
                    if (length - 1 - endSymbolIndex > 0) {
                        //还有多余的数据，继续解析下一个命令
                        int remainLen = length - 1 - endSymbolIndex;
                        byte[] remainBuffer = new byte[remainLen];
                        System.arraycopy(buffer, endSymbolIndex + 1, remainBuffer, 0, remainLen);
                        return analyse(remainBuffer);//继续分析剩余数据
                    } else {
                        //没有多余的数据
                        return new byte[0];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println();
//                LogUtil.d(TAG, e.getMessage());
            }


        }
        return buffer;
    }


    //----------------------------------------------------
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null) return;
                    byte[] buffer = new byte[512];
                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        ComBean ComRecData = new ComBean(sPort, buffer, size);
//                        onDataReceived(ComRecData);

                        try {
                            _baoStream.write(buffer, 0, size);

                            final byte[] bytes = _baoStream.toByteArray();
                            final byte[] remainBytes = analyse(bytes);
                            _baoStream.reset();
                            if (null != remainBytes
                                    && remainBytes.length > 0) {
                                _baoStream.write(remainBytes, 0, remainBytes.length);
                            }

                        } catch (Exception e) {
                            LogUtil.e(TAG, e.getMessage());
                        }
                    }
                    try {
                        Thread.sleep(50);//延时50ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //----------------------------------------------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;// 控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                send(getbLoopData());
                try {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    //----------------------------------------------------
    public int getBaudRate() {
        return iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (_isOpen) {
            return false;
        } else {
            iBaudRate = iBaud;
            return true;
        }
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    //----------------------------------------------------
    public String getPort() {
        return sPort;
    }

    public boolean setPort(String sPort) {
        if (_isOpen) {
            return false;
        } else {
            this.sPort = sPort;
            return true;
        }
    }

    //----------------------------------------------------
    public boolean isOpen() {
        return _isOpen;
    }

    //----------------------------------------------------
    public byte[] getbLoopData() {
        return _bLoopData;
    }

    //----------------------------------------------------
    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    //----------------------------------------------------
    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    //----------------------------------------------------
    public void setHexLoopData(String sHex) {
        this._bLoopData = MyFunc.HexToByteArr(sHex);
    }

    //----------------------------------------------------
    public int getiDelay() {
        return iDelay;
    }

    //----------------------------------------------------
    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    //----------------------------------------------------
    public void startSend() {
        if (mSendThread != null) {
            mSendThread.setResume();
        }
    }

    //----------------------------------------------------
    public void stopSend() {
        if (mSendThread != null) {
            mSendThread.setSuspendFlag();
        }
    }

    //----------------------------------------------------
    protected abstract void onDataReceived(ComBean ComRecData);
}
