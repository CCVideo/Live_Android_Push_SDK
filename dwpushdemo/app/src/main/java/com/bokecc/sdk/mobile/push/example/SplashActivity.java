package com.bokecc.sdk.mobile.push.example;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.bokecc.sdk.mobile.push.example.activity.LoginActivity;
import com.bokecc.sdk.mobile.push.example.base.Config;
import com.bokecc.sdk.mobile.push.example.base.activity.BaseNoPresenterActivity;

public class SplashActivity extends BaseNoPresenterActivity {

    private boolean isLaunchFromWeb;
    private String webData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DWApplication.mAppStatus = 0; // 当前状态正常
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            isLaunchFromWeb  = true;
            webData = intent.getData().toString();
        }
    }

    private void toLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("from_web", true);
        intent.putExtra("data", webData);
        startActivity(intent);
        finish();
    }


    @Override
    protected void setUpView(Bundle savedInstanceState) {
        //取消状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLaunchFromWeb) {
                    toLoginPage();
                } else {
                    go(LoginActivity.class);
                    finish();
                }
            }
        }, Config.SPLASH_STAY_TIME);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

}
