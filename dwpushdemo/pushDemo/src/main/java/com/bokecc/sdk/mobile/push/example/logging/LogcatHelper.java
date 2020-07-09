package com.bokecc.sdk.mobile.push.example.logging;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * cc推流log日志统计保存(日志等级：*:v , *:d , *:w , *:e , *:f , *:s)
 * 显示当前程序的E~I等级的日志.
 */
public class LogcatHelper {

    private static final String TAG = "LogcatHelper";

    private static LogcatHelper instance = null;
    private static String logcatPath;

    private LogDumper mLogDumper = null; // 日志输出线程
    private Context mContext;

    private int mPId;  // 本应用的PId
    private String mRoomId; // 当前直播的roomId

    // 私有构造函数 -- 单例模式
    private LogcatHelper(Context context) {
        mContext = context;
        mPId = android.os.Process.myPid();
    }

    // 获取日志统计帮助类
    public static LogcatHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LogcatHelper(context);
        }
        return instance;
    }

    // 初始化日志存储目录
    public boolean init(Context context, String roomId) {
        mRoomId = roomId;
        logcatPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ccpushlog";
        File file = new File(logcatPath);
        if (!file.exists()) {
            Log.e(TAG, "初始化日志存储目录");
            return file.mkdirs();
        } else {
            return true;
        }
    }

    // 开始抓取日志并生成日志文件
    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), logcatPath);
        mLogDumper.start();
    }

    //  停止抓取日志
    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    // 日志抓取线程
    private class LogDumper extends Thread {
        private Process mLogcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        private String mCmds = null;
        private String mPID;
        private FileOutputStream mOut = null;
        private File mLogFile;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                mLogFile = new File(dir, "ccpush-" + mRoomId + "-" + getFileName() + ".log");
                mOut = new FileOutputStream(mLogFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way"; //打印标签过滤信息
            mCmds = "logcat *:e *:w *:i | grep \"(" + mPID + ")\"";
        }

        public void stopLogs() {
            mRunning = false;
            Log.e(TAG, "推流日志输出结束");
            // LogReporter.getInstance(mContext).reportLog(mLogFile.getPath(), mLogFile.getName());
        }

        @Override
        public void run() {
            try {
                mLogcatProc = Runtime.getRuntime().exec(mCmds);
                mReader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (mOut != null && line.contains(mPID)) {
                        mOut.write((line + "\n").getBytes());
                    }
                }
                //Log.e(TAG, "推流日志输出结束，准备上传日志");
                //LogReporter.getInstance(mContext).reportLog(mLogFile.getPath(), mLogFile.getName());
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } finally {
                if (mLogcatProc != null) {
                    mLogcatProc.destroy();
                    mLogcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
                if (mOut != null) {
                    try {
                        mOut.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                    mOut = null;
                }
            }
        }
    }

    /**
     * 根据当前时间生成文件名的时间部分
     * "ccpushlog-" + getFileName() + ".log"
     */
    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(System.currentTimeMillis()));
    }
}

