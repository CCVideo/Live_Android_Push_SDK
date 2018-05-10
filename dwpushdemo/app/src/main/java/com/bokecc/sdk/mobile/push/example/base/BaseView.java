package com.bokecc.sdk.mobile.push.example.base;

import android.os.Bundle;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public interface BaseView {

    /**
     * 显示Loading
     */
    void showLoading();

    /**
     * 隐藏Loading
     */
    void dismissLoading(BasePopupWindow.OnDismissStatusListener dismissStatusListener);

    void showDialogLoading();

    void dismissDialogLoading();

    /**
     * 显示Toast提示
     * @param msg 提示内容
     */
    void showToast(String msg);

    /**
     * 跳转activity
     */
    void go(Class clazz);

    /**
     * 跳转activity带参数
     * @param clazz
     * @param bundle
     */
    void go(Class clazz, Bundle bundle);

    /**
     * 跳转activity
     */
    void goForResult(Class clazz, int requestCode);

    /**
     * 跳转activity带参数
     * @param clazz
     * @param bundle
     */
    void goForResult(Class clazz, int requestCode, Bundle bundle);

    /**
     * 关闭activity
     */
    void exit();

}
