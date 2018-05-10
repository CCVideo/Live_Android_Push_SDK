package com.bokecc.sdk.mobile.push.example.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bokecc.sdk.mobile.push.core.DWPushSession;
import com.bokecc.sdk.mobile.push.core.listener.OnLoginStatusListener;
import com.bokecc.sdk.mobile.push.example.contract.LoginContract;
import com.bokecc.sdk.mobile.push.example.scan.MipcaActivityCapture;
import com.bokecc.sdk.mobile.push.exception.DWPushException;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    public static final int REQUEST_CODE_SCAN = 1;

    private LoginContract.View mLoginView;

    private static final String SP_NAME = "CC_PUSH_DEMO";
    private static final String KEY_USER_ID = "CC_PUSH_USER_ID";
    private static final String KEY_ROOM_ID = "CC_PUSH_ROOM_ID";
    private static final String KEY_USERNAME = "CC_PUSH_USERNAME";
    private static final String KEY_PASSWORD = "CC_PUSH_PASSWORD";
    private SharedPreferences mPreferences;

    private DWPushSession mPushSession;

    public LoginPresenter(@NonNull LoginContract.View loginView) {
        Context context = (Context) loginView;
        mLoginView = loginView;
        mPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        mPushSession = DWPushSession.getInstance();
    }

    @Override
    public void login() {
        final String userId = mLoginView.getUserId();
        final String roomId = mLoginView.getRoomId();
        final String username = mLoginView.getUsername();
        final String password = mLoginView.getPassword();

        // 判断密码是否为空
        if (TextUtils.isEmpty(password)) {
            mLoginView.showToast("密码不能为空");
            return;
        }

        // 展示loading效果
        mLoginView.showLoadingView();
        try {
            mPushSession.login(userId, roomId, username, password, new OnLoginStatusListener() {
                @Override
                public void failed(final DWPushException e) {
                    mLoginView.dissmissLoadingView(false);
                    mLoginView.showToast(e.getErrorMessage());
                }

                @Override
                public void successed() {
                    // 登录成功本地化数据
                    setLocalUserId(userId);
                    setLocalRoomId(roomId);
                    setLocalUsername(username);
                    setLocalPassword(password);
                    mLoginView.dissmissLoadingView(true);
                }
            });
        } catch (DWPushException e) {
            e.printStackTrace();
            mLoginView.dissmissLoadingView(false);
        }
    }

    @Override
    public void release() {
        mPushSession.release();
    }

    @Override
    public void scanCode() {
        mLoginView.goForResult(MipcaActivityCapture.class, REQUEST_CODE_SCAN);
    }

    @Override
    public String getLocalUserId() {
        return mPreferences.getString(KEY_USER_ID, "");
    }

    @Override
    public String getLocalRoomId() {
        return mPreferences.getString(KEY_ROOM_ID, "");
    }

    @Override
    public String getLocalUsername() {
        return mPreferences.getString(KEY_USERNAME, "");
    }

    @Override
    public String getLocalPassword() {
        return mPreferences.getString(KEY_PASSWORD, "");
    }

    @Override
    public void setLocalUserId(String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_USER_ID, value);
        editor.apply();
    }

    @Override
    public void setLocalRoomId(String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_ROOM_ID, value);
        editor.apply();
    }

    @Override
    public void setLocalUsername(String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_USERNAME, value);
        editor.apply();
    }

    @Override
    public void setLocalPassword(String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_PASSWORD, value);
        editor.apply();
    }

    @Override
    public Map<String, String> parseUrlForParams(String url) {
        Map<String, String> map = new HashMap<>();
        String param = url.substring(url.indexOf("?") + 1, url.length());
        String[] params = param.split("&");

        if (params.length < 2) {
            return null;
        }
        for (String p : params) {
            String[] en = p.split("=");
            map.put(en[0], en[1]);
        }

        return map;
    }

}
