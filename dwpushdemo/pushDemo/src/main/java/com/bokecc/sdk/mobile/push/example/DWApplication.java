package com.bokecc.sdk.mobile.push.example;

import android.app.Application;

import com.bokecc.sdk.mobile.push.DWPushEngine;

/**
 * 作者 ${bokecc}.<br/>
 */
public class DWApplication extends Application {


    // demo层是否上传日志到服务器 --  用于日志分析
    public static boolean mReportLog = !BuildConfig.DEBUG;

    @Override
    public void onCreate() {
        super.onCreate();

        // CC 推流配置初始化
        DWPushEngine.init(this, true, true);
    }

}
