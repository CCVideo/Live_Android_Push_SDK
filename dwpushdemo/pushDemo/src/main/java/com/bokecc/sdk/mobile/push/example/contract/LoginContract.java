package com.bokecc.sdk.mobile.push.example.contract;

import com.bokecc.sdk.mobile.push.example.base.BasePresenter;
import com.bokecc.sdk.mobile.push.example.base.BaseView;

import java.util.Map;

/**
 * 作者 bokecc.<br/>
 */
public interface LoginContract {

    interface View extends BaseView {
        /**
         * 更新界面显示根据扫码结果
         */
        void updateDisplayByScanResult(Map<String, String> params);

        /**
         * 更新输入框的默认显示样式
         */
        void updateEditHintStyle();

        /**
         * 初始化输入框数据
         */
        void initEditData();

        /**
         * 展示加载弹出框
         */
        void showLoadingView();

        /**
         * 隐藏加载弹出框
         *
         * @param isLoginSucceed 登录是否成功
         */
        void dismissLoadingView(boolean isLoginSucceed);

        void loginSuccess();

        /**
         * 获取用户id输入框的内容
         *
         * @return 用户id
         */
        String getUserId();

        /**
         * 获取直播房间id输入框的内容
         *
         * @return 房间id
         */
        String getRoomId();

        /**
         * 获取用户名输入框的内容
         *
         * @return 用户名
         */
        String getUsername();

        /**
         * 获取密码输入框的内容
         *
         * @return 密码
         */
        String getPassword();

    }

    interface Presenter extends BasePresenter {
        /**
         * 登录
         */
        void login();

        void release();

        /**
         * 扫码
         */
        void scanCode();

        /**
         * 获取本地用户id
         *
         * @return 用户id
         */
        String getLocalUserId();

        /**
         * 获取本地直播房间id
         *
         * @return 直播房间id
         */
        String getLocalRoomId();

        /**
         * 获取本地用户名
         *
         * @return 用户名
         */
        String getLocalUsername();

        /**
         * 获取本地用户密码
         *
         * @return 用户密码
         */
        String getLocalPassword();

        /**
         * 设置本地用户id
         */
        void setLocalUserId(String value);

        /**
         * 设置本地直播房间id
         */
        void setLocalRoomId(String value);

        /**
         * 设置本地用户名
         */
        void setLocalUsername(String value);

        /**
         * 设置本地密码
         */
        void setLocalPassword(String value);

        /**
         * 从地址中解析参数
         *
         * @param url 地址
         */
        Map<String, String> parseUrlForParams(String url);
    }

}
