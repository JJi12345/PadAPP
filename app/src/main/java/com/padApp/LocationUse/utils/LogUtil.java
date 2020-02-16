package com.padApp.LocationUse.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日志工具类
 */
public class LogUtil {
    private static final String TAG = "heweisoft";


    private static int OUTPUT_LOG_LEVEL= Log.DEBUG;//当前输出日志级别，只有大于等于此级别的日志才保存到文件

    private static Boolean LOG_WRITE_TO_FILE=true;// 日志写入文件开关

    private static String LOG_PATH_SDCARD_DIR= FileUtil.getSDPath()+"/heweisoft/log/";// 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数
    private static String LOG_FILE_NAME = "Log.txt";// 本类输出的日志文件名称
    private static SimpleDateFormat logContentSuffixSdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");// 日志内容时间前缀的输出格式
    private static SimpleDateFormat logFileSuffixSdf = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

    private static final String logFileDateformat = "yyyy-MM-dd";
    private static final String contentFileDateformat = "yyyy-MM-dd HH:mm:ss";
    private static ThreadLocal<DateFormat> fileThreadLocal = new ThreadLocal<DateFormat>();
    private static ThreadLocal<DateFormat> contentThreadLocal = new ThreadLocal<DateFormat>();


    public static DateFormat getLogFileDateFormat()
    {
        DateFormat df = fileThreadLocal.get();
        if(df==null){
            df = new SimpleDateFormat(logFileDateformat);
            fileThreadLocal.set(df);
        }
        return df;
    }

    public static DateFormat getContentFileDateFormat()
    {
        DateFormat df = contentThreadLocal.get();
        if(df==null){
            df = new SimpleDateFormat(contentFileDateformat);
            contentThreadLocal.set(df);
        }
        return df;
    }



    /**
     * 创建日期根路径
     */
    public static void createLogRootDir(){
        File file = new File(LOG_PATH_SDCARD_DIR);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * 删除日志文件
     */
    public static void deleteLogFile(){
        File dir = new File(LOG_PATH_SDCARD_DIR);
        if(dir.exists()){
            final File[] files = dir.listFiles();
            for(File file:files){
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }

    /**
     * 修改日志级别
     * @param outputLogLevel
     */
    public static void changeOutputLogLevel(int outputLogLevel){
        OUTPUT_LOG_LEVEL=outputLogLevel;
    }


    public static void v(String paramString) {
        Log.v(TAG, paramString);
    }

    public static void v(String paramString1, String paramString2) {
        Log.v(paramString1, paramString2);
        if(LOG_WRITE_TO_FILE
                && OUTPUT_LOG_LEVEL<= Log.VERBOSE){
            //写入日志文件
            writeLogToFile("VERBOSE",paramString1,paramString2);
        }
    }

    public static void d(String paramString) {
        Log.d(TAG, paramString);
    }

    public static void d(String paramString1, String paramString2) {
        Log.d(paramString1, paramString2);
        if(LOG_WRITE_TO_FILE
                && OUTPUT_LOG_LEVEL<= Log.DEBUG){
            //写入日志文件
            writeLogToFile("DEBUG",paramString1,paramString2);
        }
    }

    public static void i(String paramString) {
        Log.i(TAG, paramString);
    }

    public static void i(String paramString1, String paramString2) {
        Log.d(paramString1, paramString2);
        if(LOG_WRITE_TO_FILE
                && OUTPUT_LOG_LEVEL<= Log.INFO){
            //写入日志文件
            writeLogToFile("INFO",paramString1,paramString2);
        }
    }
    public static void e(String paramString) {
        Log.e(TAG, paramString);
    }

    public static void e(String paramString1, String paramString2) {
        if(null==paramString2){
            return;
        }
        Log.e(paramString1, paramString2);
        if(LOG_WRITE_TO_FILE
                && OUTPUT_LOG_LEVEL<= Log.ERROR){
            //写入日志文件
            writeLogToFile("ERROR",paramString1,paramString2);
        }
    }

    public static void w(String paramString) {
        Log.w(TAG, paramString);
    }

    public static void w(String paramString1, String paramString2) {
        Log.w(paramString1, paramString2);
        if(LOG_WRITE_TO_FILE
                && OUTPUT_LOG_LEVEL<= Log.WARN){
            //写入日志文件
            writeLogToFile("WARN",paramString1,paramString2);
        }
    }



    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private static void writeLogToFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
        Date now = new Date();
//        String needWriteFile = logFileSuffixSdf.format(now);
        String needWriteFile = getLogFileDateFormat().format(now);
//        String needWriteMessage = logContentSuffixSdf.format(now) + "    " + mylogtype
//                + "    " + tag + "    " + text;

        String needWriteMessage = getContentFileDateFormat().format(now) + "    " + mylogtype
                + "    " + tag + "    " + text;

        File file = new File(LOG_PATH_SDCARD_DIR, needWriteFile
                + LOG_FILE_NAME);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 删除制定的日志文件
     */
    public static void delFile() {// 删除日志文件
        String needDelFiel = logFileSuffixSdf.format(getDateBefore());
        File file = new File(LOG_PATH_SDCARD_DIR, needDelFiel + LOG_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }

}

