package com.bokecc.sdk.mobile.push.example.logging;

import android.content.Context;
import android.util.Log;

import com.bokecc.sdk.mobile.push.global.Constant;

import java.io.File;

/**
 * 日志汇报类
 *
 * @author CC
 */
public class LogReporter {

    private static final String TAG = "LogReporter";

    private static LogReporter instance;
    private Context context;
    private FTPClientFunctions ftpClient;

    // 私有构造函数
    private LogReporter(Context context) {
        this.context = context;
    }

    // 获取日志汇报类
    public static LogReporter getInstance(Context context) {
        if (instance == null) {
            instance = new LogReporter(context);
        }
        return instance;
    }

    /**
     * 上传日志信息
     *
     * @param sourceFilePath 源文件目录
     * @param descFileName   文件名称
     */
    public void reportLog(final String sourceFilePath, final String descFileName) {
        // 网络操作，但开一个线程进行处理
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO 可以首先去判断一下网络
                ftpClient = new FTPClientFunctions();
                boolean connectResult = ftpClient.ftpConnect(Constant.getFtpServer(), Constant.getFtpUsername(), Constant.getFtpPassword(), Constant.getFtpPort());
                if (connectResult) {
                    boolean changeDirResult = ftpClient.ftpChangeDir("/ccpush");
                    if (changeDirResult) {
                        boolean uploadResult = ftpClient.ftpUpload(sourceFilePath, descFileName, "");
                        if (uploadResult) {
                            Log.w(TAG, "上传日志成功");
                            boolean disConnectResult = ftpClient.ftpDisconnect();
                            if(disConnectResult) {
                                Log.e(TAG, "关闭ftp连接成功");
                            } else {
                                Log.e(TAG, "关闭ftp连接失败");
                            }
                        } else {
                            Log.w(TAG, "上传日志失败");
                        }
                        // 尝试删除日志文件
                        File file = new File(sourceFilePath);
                        if(file.exists() && file.delete()) {
                            Log.w(TAG, "删除日志文件成功");
                        }
                    } else {
                        Log.w(TAG, "切换ftp目录失败");
                    }

                } else {
                    Log.w(TAG, "连接ftp服务器失败");
                }
            }
        }).start();
    }

}
