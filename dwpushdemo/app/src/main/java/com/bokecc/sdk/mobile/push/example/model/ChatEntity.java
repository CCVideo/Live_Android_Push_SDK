package com.bokecc.sdk.mobile.push.example.model;

/**
 * 作者 ${郭鹏飞}.<br/>
 */

public class ChatEntity {
    /**
     * 发送该条信息的用户id
     */
    private String mUserId;
    /**
     * 发送该条信息的用户名
     */
    private String mUserName;
    /**
     * 该条信息是否是私聊
     */
    private boolean isPrivate;
    /**
     * 该条信息是私聊的情况下 是否是主播发送的
     */
    private boolean isPublisher;

    /**
     * 发送该条信息的用户头像
     */
    private String mUserAvatar;

    /**
     *该条信息是私聊的情况下 接收方的id
     */
    private String mReceiveUserId;
    
    /**
     *该条信息是私聊的情况下 接收方的用户名
     */
    private String mReceivedUserName;

    /**
     * 该条信息是私聊的情况下 接收方的头像地址
     */
    private String mReceiveUserAvatar;

    /**
     * 聊天内容
     */
    private String mMsg;
    /**
     * 聊天时间
     */
    private String mTime;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isPublisher() {
        return isPublisher;
    }

    public void setPublisher(boolean publisher) {
        isPublisher = publisher;
    }

    public String getReceiveUserId() {
        return mReceiveUserId;
    }

    public void setReceiveUserId(String mReceiveUserId) {
        this.mReceiveUserId = mReceiveUserId;
    }

    public String getReceivedUserName() {
        return mReceivedUserName;
    }

    public void setReceivedUserName(String receivedUserName) {
        mReceivedUserName = receivedUserName;
    }

    public String getUserAvatar() {
        return mUserAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        mUserAvatar = userAvatar;
    }

    public String getReceiveUserAvatar() {
        return mReceiveUserAvatar;
    }

    public void setReceiveUserAvatar(String receiveUserAvatar) {
        mReceiveUserAvatar = receiveUserAvatar;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
