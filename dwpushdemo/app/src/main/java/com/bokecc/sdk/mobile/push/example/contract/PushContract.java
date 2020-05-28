package com.bokecc.sdk.mobile.push.example.contract;

import android.widget.EditText;

import com.bokecc.sdk.mobile.push.chat.model.ChatUser;
import com.bokecc.sdk.mobile.push.core.DWPushConfig;
import com.bokecc.sdk.mobile.push.example.base.BasePresenter;
import com.bokecc.sdk.mobile.push.example.base.BaseView;
import com.bokecc.sdk.mobile.push.example.model.ChatEntity;

/**
 * 作者 ${bokecc}.<br/>
 */
public interface PushContract {

    interface View extends BaseView {

        void initVolumeAndBeautifulProgress();

        void showVolume();

        void dismissVolume();

        // 显示连接状态 规定 绿色连接成功 红色连接失败 黄色重连 白色未连接 蓝色正在连接/连接断开
        void updateStatus(int color, String message);

        void updateChat(ChatEntity chatEntity);

        void updateChatInput();

        void updatePrivateChat(ChatEntity chatEntity);

        void updateRoomUserCount(int count);

        void updatePushTime(String time);

        void updatePushNetState(String state);

        // 显示手动录制按钮
        void enableRecordMode();

        // 隐藏手动录制按钮
        void disableRecordMode();

        void updateRecordState(int res);

        void enableStopButton();

        void disableStopButton();

        void updateRecordText(String text);
    }

    interface Presenter extends BasePresenter {

        /**
         * 添加聊天室人数获取监听
         */
        void addChatRoomUserCountListener();

        /**
         * 获取直播间人数
         */
        void loopForRoomUserCount();

        /**
         * 准备推流
         *
         * @param pushConfig 推流配置
         */
        void prepare(DWPushConfig pushConfig);

        /**
         * 开始推流
         */
        void start(DWPushConfig pushConfig);

        /**
         * 停止推流
         */
        void stop();

        /**
         * 切换摄像头
         */
        void switchCamera();

        //  生命周期
        void onResume();

        void onPause();

        void onDestory();
        // 生命周期

        /**
         * 更新音量
         *
         * @param volume 音量
         */
        void updateVolume(int volume);

        /**
         * 设置美颜级别
         *
         * @param whiteLevel 美白
         * @param skinLevel  磨皮
         * @param pinkLevel  粉嫩
         */
        void updateBeautifulLevel(int whiteLevel, int skinLevel, int pinkLevel);

        /**
         * 添加聊天监听
         */
        void addChatMsgListener();

        /**
         * 发送聊天
         *
         * @param msg 聊天内容
         * @param to  私聊接收方
         */
        void sendChatMsg(String msg, ChatUser to);

        /**
         * 删除输入框的一个元素
         */
        void deleteInputOne(EditText mInput);

        /**
         * 添加emoji表情
         */
        void addEmoji(EditText mInput, int position);


        /**
         * 开始录制
         */
        void startRecord();

        /**
         * 暂停录制
         */
        void pauseRecord();

        /**
         * 恢复录制
         */
        void resumeRecord();

        /**
         * 停止录制
         */
        void stopRecord();

    }

}
