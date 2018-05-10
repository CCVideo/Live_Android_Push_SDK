package com.bokecc.sdk.mobile.push.example;

import android.app.Application;

import com.bokecc.sdk.mobile.push.DWPushEngine;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class DWApplication extends Application {

    public static int mAppStatus = -1; // 表示 force_kill

    public static boolean mReportLog = false; // 是否上传日志到服务器 -- CC 用于日志分析

    @Override
    public void onCreate() {
        super.onCreate();

        // CC 推流配置初始化
        DWPushEngine.init(this, true, true);

        // 崩溃日志日志汇报模块 初始化 bugly
        CrashReport.initCrashReport(getApplicationContext(), "1400007292", true);
    }

}
