package com.bokecc.sdk.mobile.push.example.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.sdk.mobile.push.DWPushEngine;
import com.bokecc.sdk.mobile.push.core.DWPushConfig;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.SplashActivity;
import com.bokecc.sdk.mobile.push.example.base.BaseOnTitleClickListener;
import com.bokecc.sdk.mobile.push.example.base.activity.TitleActivity;
import com.bokecc.sdk.mobile.push.example.contract.LoginContract;
import com.bokecc.sdk.mobile.push.example.logging.LogReporter2;
import com.bokecc.sdk.mobile.push.example.popup.TxtLoadingPopup;
import com.bokecc.sdk.mobile.push.example.presenter.LoginPresenter;
import com.bokecc.sdk.mobile.push.example.scan.qr_codescan.MipcaActivityCapture;
import com.bokecc.sdk.mobile.push.example.view.LoginLineLayout;

import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/***
 * 直播推流登录直播间界面
 * @author CC
 */
public class LoginActivity extends TitleActivity<LoginPresenter, LoginActivity.LoginViewHolder> implements LoginContract.View {

    // ---------------- 读取配置信息 - 测试账号信息 -------------------
    @BindString(R.string.test_userid)
    String mUerIdValue;
    @BindString(R.string.test_roomid)
    String mRoomIdValue;
    @BindString(R.string.test_username)
    String mUsernameValue;
    @BindString(R.string.test_passwd)
    String mPasswordValue;

    // -----------------------------------------------------------
    private boolean isCreateFromWeb;
    private boolean autoLogin;
    private String webData;

    private TxtLoadingPopup mLoadingPopup;
//    private boolean isSuccessed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 判断是否是从网页启动的应用
        isCreateFromWeb = getIntent().getBooleanExtra("from_web", false);
        if (isCreateFromWeb) {
            webData = getIntent().getStringExtra("data");
            Log.e("LoginActivity", "onCreate，Receive Web Launch data ： " + webData);
        }
        super.onCreate(savedInstanceState);

        // 添加日志上报功能
        LogReporter2.initReport(getApplication());
    }

    @Override
    protected void setUpView(Bundle savedInstanceState) {
        super.setUpView(savedInstanceState);
        setTitleStatus(TitleImageStatus.DISMISS, 0, "登录直播间", TitleImageStatus.SHOW, R.drawable.nav_ic_code,
                new BaseOnTitleClickListener() {
                    @Override
                    public void onRightClick() {
                        mPresenter.scanCode();
                    }
                });
        updateEditHintStyle();
        if (isCreateFromWeb) {
            parseWebData(webData);
        } else {
            initEditData();
        }
        doPermissionCheck();

        ((TextView) findViewById(R.id.version)).setText(String.format("获得场景视频 v%s", DWPushEngine.getInstance().getVersionName()));
    }

    /**
     * 解析数据
     */
    private void parseWebData(String data) {
        if (data != null && data.startsWith("cclive://")) {
            String keyData = data.substring(9);
            String[] keys = keyData.split("/");
            if (keys.length == 4) {
                setLoginData(keys[0], keys[1], keys[2], keys[3]);
                // 如果数据是全的，可以执行一次登陆操作
                autoLogin = true;
                Toast.makeText(LoginActivity.this, "即将自动登录！", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 判断当前activity是否活跃
                        if (isDestroyed() || isFinishing()) {
                            return;
                        }
                        // 执行登录操作
                        mPresenter.login();
                        Toast.makeText(LoginActivity.this, "开始自动登录！", Toast.LENGTH_SHORT).show();
                        autoLogin = false;
                    }
                }, 1000);

            } else if (keys.length == 3) {
                setLoginData(keys[0], keys[1], keys[2], "");
            } else if (keys.length == 2) {
                setLoginData(keys[0], keys[1], "", "");
            } else if (keys.length == 1) {
                if (keys[0] != null && !keys[0].isEmpty()) {
                    setLoginData(keys[0], "", "", "");
                } else {
                    setLoginData("", "", "", "");
                }
            } else {
                Log.e("LoginActivity", "invalid web data");
            }
        }
    }

    private void setLoginData(String userId, String roomId, String username, String passWord) {
        mViewHolder.mUserid.setText(userId);
        mViewHolder.mRoomid.setText(roomId);
        mViewHolder.mUsername.setText(username);
        mViewHolder.mPasswd.setText(passWord);
    }

    /**
     * 进行权限检测
     */
    private void doPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
            // 申请 相机 麦克风权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    private TextWatcher myTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mViewHolder.mLogin.setEnabled(isNewLoginButtonEnabled(mViewHolder.mUserid, mViewHolder.mRoomid, mViewHolder.mUsername, mViewHolder.mPasswd));
        }
    };


    public static boolean isNewLoginButtonEnabled(LoginLineLayout... views) {
        for (int i = 0; i < views.length; i++) {
            if ("".equals(views[i].getText().trim())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Allow
                //Toast.makeText(LoginActivity.this, "Permission Allow", Toast.LENGTH_SHORT).show();
            } else {
                // Permission Denied
                //Toast.makeText(LoginActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected LoginPresenter getPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginViewHolder getViewHolder() {
        return new LoginViewHolder(getContentView());
    }

    @Override
    public void updateDisplayByScanResult(Map<String, String> params) {
        if (params == null) return;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equalsIgnoreCase("roomid")) {
                mViewHolder.mRoomid.setText(value);
            } else {
                mViewHolder.mUserid.setText(value);
            }
        }
    }

    @Override
    public void updateEditHintStyle() {
        mViewHolder.mUserid.setHint(getResources().getString(R.string.login_uid_hint)).addOnTextChangeListener(myTextWatcher);
        mViewHolder.mRoomid.setHint(getResources().getString(R.string.login_roomid_hint)).addOnTextChangeListener(myTextWatcher);
        mViewHolder.mUsername.setHint(getResources().getString(R.string.login_name_hint)).addOnTextChangeListener(myTextWatcher);
        mViewHolder.mPasswd.setHint(getResources().getString(R.string.login_t_password_hint)).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD).addOnTextChangeListener(myTextWatcher);
        mViewHolder.mUsername.maxEditTextLength = 20; // 用户名最大为20
    }

    @Override
    public void initEditData() {
        /**
         * 数据来源优先级
         * 1. 读取本地记录数据 <br/>
         * 2. 读取配置信息 <br/>
         * 3. 显示提示语 - 用户手动输入信息 <br/>
         */
        String userIdValue = mPresenter.getLocalUserId();
        String roomIdValue = mPresenter.getLocalRoomId();
        String usernameValue = mPresenter.getLocalUsername();
        String passwordValue = mPresenter.getLocalPassword();
        if (TextUtils.isEmpty(userIdValue) || TextUtils.isEmpty(roomIdValue) ||
                TextUtils.isEmpty(usernameValue) || TextUtils.isEmpty(passwordValue)) {
            userIdValue = mUerIdValue;
            roomIdValue = mRoomIdValue;
            usernameValue = mUsernameValue;
            passwordValue = mPasswordValue;
            if (TextUtils.isEmpty(userIdValue) || TextUtils.isEmpty(roomIdValue) ||
                    TextUtils.isEmpty(usernameValue) || TextUtils.isEmpty(passwordValue)) {
                return;
            }
        }
        mViewHolder.mUserid.setText(userIdValue);
        mViewHolder.mRoomid.setText(roomIdValue);
        mViewHolder.mUsername.setText(usernameValue);
        mViewHolder.mPasswd.setText(passwordValue);
    }

    @Override
    public void showLoadingView() {
        mLoadingPopup = new TxtLoadingPopup(this);
        mLoadingPopup.setKeyBackCancel(false);
        mLoadingPopup.setOutsideCancel(false);
        mLoadingPopup.setTipValue("正在登录...");
        mLoadingPopup.show(mViewHolder.mLogin);
    }


    /**
     * 登陆成功，跳转到预览界面
     */
    private void startPreview() {
        isCreateFromWeb = false;
        // 设置默认的推流参数，并跳转到推流界面
        Bundle bundle = new Bundle();
        DWPushConfig pushConfig = new DWPushConfig.DWPushConfigBuilder()
                .fps(20)
                .beauty(true)
                .bitrate(400)
                .orientation(DWPushConfig.PORTRAIT)
                .cameraType(DWPushConfig.CAMERA_FRONT)
                .videoResolution(DWPushConfig.RESOLUTION_HD)
                .rtmpNodeIndex(0)
                .build();
        bundle.putSerializable(PushActivity.KEY_PUSH_CONFIG, pushConfig);

        // 跳转到推流预览界面
        go(PushActivity.class, bundle);

    }

    @Override
    public void dismissLoadingView(boolean isLoginSucceed) {
//        isSuccessed = isLoginSucceed;
        mLoadingPopup.dismiss();
    }

    @Override
    public void loginSuccess() {
        startPreview();
    }

    @Override
    public String getUserId() {
        return mViewHolder.mUserid.getText();
    }

    @Override
    public String getRoomId() {
        return mViewHolder.mRoomid.getText();
    }

    @Override
    public String getUsername() {
        return mViewHolder.mUsername.getText();
    }

    @Override
    public String getPassword() {
        return mViewHolder.mPasswd.getText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginPresenter.REQUEST_CODE_SCAN) {
            if (resultCode == MipcaActivityCapture.RESULT_OK && data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                Map<String, String> params = mPresenter.parseUrlForParams(result);
                updateDisplayByScanResult(params);
            }
        }
    }

    public class LoginViewHolder extends TitleActivity.ViewHolder {
        @BindView(R.id.lll_login_push_uid)
        LoginLineLayout mUserid;
        @BindView(R.id.lll_login_push_roomid)
        LoginLineLayout mRoomid;
        @BindView(R.id.lll_login_push_name)
        LoginLineLayout mUsername;
        @BindView(R.id.lll_login_push_password)
        LoginLineLayout mPasswd;
        @BindView(R.id.id_login_submit)
        Button mLogin;

        public LoginViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_login_submit)
        void login() {
            // 自动登陆中不做任何处理
            if (autoLogin) {
                return;
            }
            mPresenter.login();
        }
    }

    @Override
    protected void protectApp() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 判断是否是从网页启动的应用
        boolean fromWeb = intent.getBooleanExtra("from_web", false);
        if (fromWeb) {
            String webData = intent.getStringExtra("data");
            Log.e("LoginActivity", "onNewIntent，Receive Web Launch data ： " + webData);
            parseWebData(webData);
        }

        String action = intent.getStringExtra("action");
        if (!TextUtils.isEmpty(action) && action.equals("force_kill")) {
            protectApp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.release();
            mPresenter = null;
        }
    }
}
