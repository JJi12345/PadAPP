package com.padApp.LocationUse.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.UUID;


/**
 * byte工具转换类
 */
public class ByteUtil {
    public static byte[] intToByte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >> 24);// 最高位,无符号右移。
        return targets;
    }

    public static int byteToInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < 4; i++) {
            bLoop = bRefArr[i];
            iOutcome = iOutcome | (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    public static char[] getChars (byte[] bytes) {
        Charset cs = Charset.forName ("gbk");
        ByteBuffer bb = ByteBuffer.allocate (bytes.length);
        bb.put (bytes);
        bb.flip ();
        CharBuffer cb = cs.decode (bb);
        return cb.array();
    }

    /**
     * uuid转换为byte数组
     * @param uuid uuid数据
     * @return
     */
    public static byte[] uuidToByte(UUID uuid){
        final long mostSignificantBits = uuid.getMostSignificantBits();
        final long leastSignificantBits = uuid.getLeastSignificantBits();
        byte buf[]=new byte[16];
        int len=8;//long占8个字节
        for(int i=0;i<len;i++){//处理mostSignificantBits值
            buf[i]=(byte)(mostSignificantBits >> 8 * i & 0xFF);
        }
        for(int i=8;i<len+8;i++){//处理leastSignificantBits值
            buf[i]=(byte)(leastSignificantBits >> 8 * (i-8) & 0xFF);
        }
        return buf;
    }

    /**
     * 获取指定UUID
     * @param bytes
     * @return
     */
    public static String byteToUUID(byte[] bytes){
        if(null==bytes
                || bytes.length!=16){
            return "";
        }
        long mostSignificantBits = 0;
        // 低位在前
        byte bLoop;
        for (int i = 0; i < 8; i++) {
            bLoop = bytes[i];
            mostSignificantBits = mostSignificantBits | (bLoop & 0xFF) << (8 * i);
        }

        long leastSignificantBits=0;
        for (int i = 8; i < 16; i++) {
            bLoop = bytes[i];
            leastSignificantBits = leastSignificantBits | (bLoop & 0xFF) << (8 * (i-8));
        }

        UUID uuid=new UUID(mostSignificantBits,leastSignificantBits);
        return uuid.toString();
    }

    /*
  * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
   * @param src byte[] data
   * @return hex string
   */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 去掉末尾空白字符
     * @param bytes
     * @return
     */
    public static byte[] trim(byte[] bytes){
        byte[] result=new byte[0];
        if(null!=bytes
                && bytes.length>0){
            int fileNameLen=bytes.length;
            for(int i=fileNameLen-1;i>=0;i--){
                final byte b = bytes[i];
                if(b!=0){
                    result=new byte[i+1];
                    System.arraycopy(bytes,0,result,0,i);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
