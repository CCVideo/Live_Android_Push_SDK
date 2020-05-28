package com.bokecc.sdk.mobile.push.example.base.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bokecc.sdk.mobile.push.example.DWApplication;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.activity.LoginActivity;
import com.bokecc.sdk.mobile.push.example.base.BasePopupWindow;
import com.bokecc.sdk.mobile.push.example.base.BasePresenter;
import com.bokecc.sdk.mobile.push.example.base.BaseView;
import com.bokecc.sdk.mobile.push.example.popup.LoadingPopup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected Dialog mLoadingDialog; // Dialog
    private LoadingPopup mLoadingPopup; // PopupWindow 注意:不能在onCreate调用
    private Unbinder mUnbinder;
    protected T mPresenter;

    protected boolean isNeedPresenter = true; // // 是否需要P层

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DWApplication.mAppStatus == -1) { // 如果被强杀不执行初始化操作
            protectApp();
        } else {
            doBeforeSetContentView();
            setContentView(getLayoutId());
            mUnbinder = ButterKnife.bind(this);
            if (isNeedPresenter) {
                mPresenter = getPresenter();// 如果需要P层 对P进行实例化
            }
            setUpView(savedInstanceState);
        }
    }

    /**
     * 在setContentView之前进行设置
     */
    protected void doBeforeSetContentView(){
        // ignore 子类需要时重写
    }

    /**
     * 应用被强杀 重启APP
     */
    protected void protectApp() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("action", "force_kill");
        startActivity(intent);
    }

    /**
     * 获取布局id
     */
    protected abstract int getLayoutId();

    /**
     * 进行界面操作
     * @param savedInstanceState
     */
    protected abstract void setUpView(Bundle savedInstanceState);

    /**
     * 获取 {@link BasePresenter}
     */
    protected abstract T getPresenter();

    @Override
    public void go(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    public void go(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void goForResult(Class clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void goForResult(Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode, bundle);
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void showDialogLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        mLoadingDialog = new AlertDialog.Builder(this).
                setView(view).
                setCancelable(true).
                create();
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.show();
        WindowManager.LayoutParams params = mLoadingDialog.getWindow().getAttributes();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        params.width = dm.widthPixels / 2; // 宽度
        mLoadingDialog.getWindow().setAttributes(params);
    }

    /**
     * 隐藏对话框
     */
    @Override
    public void dismissDialogLoading() {
        if (mLoadingDialog == null) {
            return;
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

    @Override
    public void showLoading() {
        mLoadingPopup = new LoadingPopup(this);
        mLoadingPopup.setOutsideCancel(false);
        mLoadingPopup.setBackPressedCancel(false);
        mLoadingPopup.show(findViewById(android.R.id.content));
    }

    @Override
    public void dismissLoading(BasePopupWindow.OnDismissStatusListener dismissStatusListener) {
        if (mLoadingPopup == null) {
            return;
        }
        if (mLoadingPopup.isShowing()) {
            mLoadingPopup.dismiss(dismissStatusListener);
        }
        mLoadingPopup = null;
    }

    @Override
    public void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // 在主线程上展示toast
    public void toastOnUiThread(final String msg) {
        // 判断是否处在UI线程
        if (!checkOnMainThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToast(msg);
                }
            });
        } else {
            showToast(msg);
        }
    }

    // 判断当前的线程是否是UI线程
    protected boolean checkOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading(null);
        dismissDialogLoading();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }
}
