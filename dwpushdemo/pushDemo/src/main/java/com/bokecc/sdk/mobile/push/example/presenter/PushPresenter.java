package com.bokecc.sdk.mobile.push.example.presenter;

import android.app.Activity;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.widget.EditText;
import android.widget.Toast;

import com.bokecc.sdk.mobile.push.chat.exception.ChatMsgIllegalException;
import com.bokecc.sdk.mobile.push.chat.listener.OnChatMsgListener;
import com.bokecc.sdk.mobile.push.chat.listener.OnChatRoomListener;
import com.bokecc.sdk.mobile.push.core.listener.OnHostModeListener;
import com.bokecc.sdk.mobile.push.chat.model.ChatMsg;
import com.bokecc.sdk.mobile.push.chat.model.ChatUser;
import com.bokecc.sdk.mobile.push.core.DWPushConfig;
import com.bokecc.sdk.mobile.push.core.DWPushSession;
import com.bokecc.sdk.mobile.push.core.listener.DWOnPushStatusListener;
import com.bokecc.sdk.mobile.push.core.listener.OnKickOutListener;
import com.bokecc.sdk.mobile.push.entity.LiveCurrentInfo;
import com.bokecc.sdk.mobile.push.entity.RoomInfo;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.contract.PushContract;
import com.bokecc.sdk.mobile.push.example.model.ChatEntity;
import com.bokecc.sdk.mobile.push.example.util.EmojiUtil;
import com.bokecc.sdk.mobile.push.logging.ELog;
import com.bokecc.sdk.mobile.push.tools.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者 ${bokecc}.<br/>
 */
public class PushPresenter implements PushContract.Presenter {

    private static final String TAG = PushPresenter.class.getSimpleName();

    private PushContract.View mPushView;
    private DWPushSession mPushSession;
    private Activity mActivity;

    private Timer mRoomTimer;
    private Timer mLiveTimeTimer; // 直播时间计时器
    private long mStartLiveTime; // 开始推流时间戳

    private Timer mRecordTimer;
    private long mRecordTime = 0;

    private int colors[] = new int[]{
            Color.parseColor("#FFFFFF"), // 白色
            Color.parseColor("#FFFF00"), // 黄色
            Color.parseColor("#00FF00"), // 绿色
            Color.parseColor("#EE9A00"), // 橙色
            Color.parseColor("#FF0000"), // 红色
    };

    private RoomInfo roomInfo;
    private boolean isPushing;  // 是否开始直播
    private boolean isPressRecord; // 是否按下录制按钮

    public PushPresenter(PushContract.View pushView) {
        mPushView = pushView;
        mActivity = (Activity) pushView;
    }

    public void setPushing(boolean pushing) {
        isPushing = pushing;
    }

    public void setPushSession(DWPushSession pushSession) {
        mPushSession = pushSession;
    }

    @Override
    public void addChatRoomUserCountListener() {
        mPushSession.setOnChatRoomListener(new OnChatRoomListener() {
            @Override
            public void onRoomUserCountUpdate(int count) {
                if (mRoomTimer == null) {
                    return;
                }
                mPushView.updateRoomUserCount(count);
            }
        });
    }

    @Override
    public void loopForRoomUserCount() {
        if (mRoomTimer != null) {
            mRoomTimer.cancel();
            mRoomTimer = null;
        }
        mRoomTimer = new Timer();
        mRoomTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mPushSession != null) {
                    mPushSession.getRoomUserCount();
                }
            }
        }, 0, 5 * 1000);
    }

    @Override
    public void deleteInputOne(EditText mInput) {
        Editable editable = mInput.getText();
        int length = editable.length();
        if (length <= 0) {
            return;
        }
        int arrowPosition = mInput.getSelectionStart();
        if (arrowPosition == 0) {
            return;
        }
        String subString = editable.toString().substring(0, arrowPosition);
        if (subString.length() >= 8) {
            int imgIndex = subString.lastIndexOf("[em2_");

            if ((imgIndex + 8) == arrowPosition) {
                if (EmojiUtil.pattern.matcher(editable.toString().substring(imgIndex, imgIndex + 8)).find()) {
                    editable.delete(arrowPosition - 8, arrowPosition);
                } else {
                    editable.delete(arrowPosition - 1, arrowPosition);
                }
            } else {
                editable.delete(arrowPosition - 1, arrowPosition);
            }
        } else {
            editable.delete(arrowPosition - 1, arrowPosition);
        }
    }

    @Override
    public void addEmoji(EditText mInput, int position) {
        String emojiStr = EmojiUtil.imgNames[position];
        int index = mInput.getSelectionStart();
        String content = mInput.getText().toString();
        String pre = content.substring(0, index);
        String next = content.substring(index, content.length());
        SpannableString ss = new SpannableString(pre + emojiStr + next);
        mInput.setText(EmojiUtil.parseFaceMsg(mActivity, ss));
        mInput.setSelection(index + emojiStr.length());
    }


    /**
     * 开始录制的计时任务
     */
    private void loopForRecordTime() {
        if (mRecordTimer != null) {
            mRecordTimer.cancel();
            mRecordTimer = null;
        }

        mRecordTimer = new Timer();
        mRecordTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRecordTime += 1000;
                mPushView.updateRecordText("录制中：" + formatTime(mRecordTime));
            }
        }, 1000, 1000);
    }

    /**
     * 开始计时任务
     */
    private void loopForLiveTime() {
        if (mLiveTimeTimer != null) {
            mLiveTimeTimer.cancel();
            mLiveTimeTimer = null;
        }
        mLiveTimeTimer = new Timer();
        mLiveTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mPushSession.isOpenHostMode()) {
                    mPushView.updatePushTime("推流中： " + formatTime(System.currentTimeMillis() - mStartLiveTime));
                } else {
                    mPushView.updatePushTime("直播中： " + formatTime(System.currentTimeMillis() - mStartLiveTime));
                }

            }
        }, 0, 1000);
    }

    /***
     * 格式化时间 —— 分：秒
     * @param time long类型转String
     * @return 转换后的时间
     */
    private String formatTime(long time) {

        long minute = 0;
        long second = time / 1000;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }

        String minuteStr = minute < 10 ? "0" + minute : "" + minute;
        String secondStr = second < 10 ? "0" + second : "" + second;
        return (minuteStr + ":" + secondStr);
    }

    @Override
    public void prepare(DWPushConfig pushConfig) {
        boolean flag = mPushSession.prepare(pushConfig);
        if (flag) {
            LogUtil.e(TAG, "配置完成");
        } else {
            LogUtil.e(TAG, "配置失败");
        }

    }

    @Override
    public void start(DWPushConfig pushConfig) {
        mPushView.showDialogLoading();
        mPushSession.start(pushConfig, new DWOnPushStatusListener() {
            @Override
            public void onConfigMessage(String liveId) {
                LogUtil.i(TAG, "liveid [ " + liveId + " ]");
            }

            @Override
            public void onSuccessed() {
                ELog.i(TAG, "onSucceeded:");
                mPushView.dismissDialogLoading();
                mPushView.updateStatus(colors[2], mActivity.getResources().getString(R.string.text_push_status_success));
                roomInfo = mPushSession.getRoomInfo();

                if (roomInfo.getManuallyRecordMode() == 1) {
                    // 手动录制开启
                    if (mPushSession.getRecordState() == DWPushSession.RecordState.IDLE || mPushSession.getRecordState() == DWPushSession.RecordState.STOP) {
                        mPushView.enableRecordMode();
                    }
                } else {
                    // 手动录制关闭
                    mPushView.disableRecordMode();
                }
            }

            @Override
            public void onFailed(String message) {
                ELog.i(TAG, "onFailed:" + message);
                mPushView.dismissDialogLoading();
                mPushView.updateStatus(colors[4],
                        mActivity.getResources().getString(R.string.text_push_status_failed));
                mPushView.showToast(message);
                mPushView.exit();
            }

            @Override
            public void onDisconnected() {
                ELog.i(TAG, "onDisconnected:");
                mPushView.updateStatus(colors[3],
                        mActivity.getResources().getString(R.string.text_push_status_disconnect));
            }

            @Override
            public void onReconnect() {
                ELog.i(TAG, "onReconnect:");
                mPushView.dismissDialogLoading();
                mPushView.updateStatus(colors[1],
                        mActivity.getResources().getString(R.string.text_push_status_reconnect));
            }

            @Override
            public void onClosed(int action) {
                ELog.i(TAG, "onClosed:");
                mPushView.updateStatus(colors[0],
                        mActivity.getResources().getString(R.string.text_push_status_close));
                if (action == DWPushSession.RTMP_CLOSE_NO_HEART_BEAT_ACTION) {
                    mPushView.showToast("心跳服务停止,连接关闭");
                    mPushView.exit();
                }
            }

            @Override
            public void onCurrentInfo(LiveCurrentInfo info) {
                float freeBufferPercent = info.freeBufferPercent;
                String state;
                if (freeBufferPercent > 0.88) {
                    state = "优";
                } else if (freeBufferPercent > 0.66) {
                    state = "良";
                } else if (freeBufferPercent > 0.33) {
                    state = "弱";
                } else {
                    state = "差";
                }

                mPushView.updatePushNetState(state);
            }

        });
        // 设置开始直播时间，并开始计时任务
        mStartLiveTime = System.currentTimeMillis();
        if (!mPushSession.isOpenHostMode()) {
            loopForLiveTime();
        }
    }

    @Override
    public void stop() {
        mPushSession.stop();
    }

    @Override
    public void switchCamera() {
        mPushSession.switchCamera();
    }

    @Override
    public void startRecord() {
        if (isPressRecord) return;
        if (!isPushing) return;
        DWPushSession.RecordState recordState = mPushSession.getRecordState();

        if (recordState == DWPushSession.RecordState.IDLE || recordState == DWPushSession.RecordState.STOP) {
            mPushSession.startRecord(new DWPushSession.RecordCallBack() {
                @Override
                public void onResult(boolean isSuccess, String msg) {
                    if (isSuccess) {
                        mPushView.updateRecordState(R.drawable.push_record_pause);
                        mPushView.enableStopButton();
                        mPushView.updateRecordText("录制中： " + formatTime(mRecordTime));
                        loopForRecordTime();
                    } else {
                        mPushView.updateRecordState(R.drawable.push_record_start);
                        mPushView.disableStopButton();
                    }
                    isPressRecord = false;
                }
            });
        } else if (recordState == DWPushSession.RecordState.RECORDING) {
            pauseRecord();
        } else if (recordState == DWPushSession.RecordState.PAUSE) {
            resumeRecord();

        }


    }

    @Override
    public void pauseRecord() {
        if (isPressRecord) return;
        if (!isPushing) return;
        mPushSession.pauseRecord(new DWPushSession.RecordCallBack() {
            @Override
            public void onResult(boolean isSuccess, String msg) {
                if (isSuccess) {
                    mPushView.updateRecordState(R.drawable.push_record_resume);
                    mPushView.enableStopButton();
                    mPushView.updateRecordText("暂停录制中：" + formatTime(mRecordTime));
                    if (mRecordTimer != null) {
                        mRecordTimer.cancel();
                        mRecordTimer = null;
                    }

                } else {
                    mPushView.updateRecordState(R.drawable.push_record_pause);
                    mPushView.enableStopButton();
                }
                isPressRecord = false;
            }
        });
    }

    @Override
    public void resumeRecord() {
        if (isPressRecord) return;
        if (!isPushing) return;
        mPushSession.resumeRecord(new DWPushSession.RecordCallBack() {
            @Override
            public void onResult(boolean isSuccess, String msg) {
                if (isSuccess) {
                    mPushView.updateRecordState(R.drawable.push_record_pause);
                    mPushView.enableStopButton();
                    loopForRecordTime();
                } else {
                    mPushView.updateRecordState(R.drawable.push_record_resume);
                    mPushView.enableStopButton();
                }
                isPressRecord = false;
            }
        });

    }

    @Override
    public void stopRecord() {
        if (isPressRecord) return;
        if (!isPushing) return;
        mPushSession.stopRecord(new DWPushSession.RecordCallBack() {
            @Override
            public void onResult(boolean isSuccess, String msg) {
                if (isSuccess) {
                    mPushView.updateRecordState(R.drawable.push_record_start);
                    mPushView.disableStopButton();
                    if (mRecordTimer != null) {
                        mRecordTimer.cancel();
                        mRecordTimer = null;
                    }
                    mRecordTime = 0;
                    mPushView.updateRecordText("暂无录制");
                } else {
                    mPushView.enableStopButton();
                }
                isPressRecord = false;
            }
        });

    }

    // ---------------------生命周期--------------------------

    @Override
    public void onResume() {
        if (!isPushing) return;
        mPushSession.onResume();
    }

    @Override
    public void onPause() {
        if (!isPushing) return;
        mPushSession.onPause();
    }

    @Override
    public void onDestory() {
        mPushSession.onDestory();
        if (mRoomTimer != null) {
            mRoomTimer.cancel();
            mRoomTimer = null;
        }
        if (mLiveTimeTimer != null) {
            mLiveTimeTimer.cancel();
            mLiveTimeTimer = null;
        }
        if (mRecordTimer != null) {
            mRecordTimer.cancel();
            mRecordTimer = null;
        }
        mPushSession = null;
    }

    // ---------------------生命周期--------------------------


    @Override
    public void updateVolume(int progress) {
        mPushSession.updateVolume(progress);
    }

    @Override
    public void updateBeautifulLevel(int whiteLevel, int skinLevel, int pinkLevel) {
        mPushSession.setBeautifulLevel(whiteLevel, skinLevel, pinkLevel);
    }

    @Override
    public void addChatMsgListener() {
        mPushSession.setOnChatMsgListener(new OnChatMsgListener() {
            @Override
            public void onReceivedPublic(ChatUser from, ChatMsg msg, boolean isPublisher) {
                mPushView.updateChat(getChatEntity(from, null, msg, isPublisher));
            }

            @Override
            public void onReceivedPrivate(ChatUser from, ChatUser to, ChatMsg msg, boolean isPublisher) {
                mPushView.updatePrivateChat(getChatEntity(from, to, msg, isPublisher));
            }

            @Override
            public void onError(String errorMsg) {
                mPushView.showToast(errorMsg);
            }
        });
    }

    // 添加主持人模式的回调
    public void addHostModeListener() {
        mPushSession.setOnHostModeListener(new OnHostModeListener() {
            @Override
            public void setMainSpeaker(boolean isMainSpeaker) {
                mPushView.setMainSpeaker(isMainSpeaker);

            }

            @Override
            public void endStream() {
                mPushView.endStream();
            }

            @Override
            public void publishStream() {
                mPushView.publishStream();


            }
        });
    }

    // 添加异地登录被踢出监听
    public void addKickOutListener() {
        mPushSession.setOnKickOutListener(new OnKickOutListener() {
            @Override
            public void onKickOut() {
                if (mPushView != null) {
                    mPushView.kickOut();
                }

            }
        });
    }

    /**
     * 获取聊天实体
     *
     * @param from        发送 {@link ChatUser}
     * @param to          接收 (公聊接收方传递null) {@link ChatUser}
     * @param msg         聊天信息 {@link ChatMsg}
     * @param isPublisher 发送方是否是主播
     * @return {@link ChatEntity}
     */
    private ChatEntity getChatEntity(ChatUser from, ChatUser to, ChatMsg msg, boolean isPublisher) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setUserId(from.getUserId());
        chatEntity.setUserName(from.getUserName());
        chatEntity.setPrivate((to != null));
        chatEntity.setReceiveUserId(to != null ? to.getUserId() : "");
        chatEntity.setReceivedUserName((to != null ? to.getUserName() : ""));
        chatEntity.setReceiveUserAvatar((to != null ? to.getUserAvatar() : ""));
        chatEntity.setPublisher(isPublisher);
        chatEntity.setMsg(msg.getMsg());
        chatEntity.setTime(msg.getTime());
        chatEntity.setUserAvatar(from.getUserAvatar());
        return chatEntity;
    }

    @Override
    public void sendChatMsg(String msg, ChatUser to) {
        boolean isPrivate = to != null;
        if (!isPrivate) {
            sendChatMsgToAll(msg);
        } else {
            sendChatMsgToOne(to.getUserId(), to.getUserName(), msg);
        }
        mPushView.updateChatInput();
    }

    private void sendChatMsgToAll(String msg) {
        try {
            mPushSession.sendChatMsgToAll(msg);
        } catch (ChatMsgIllegalException e) {
            Toast.makeText(mActivity, e.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendChatMsgToOne(String userId, String userName, String msg) {
        try {
            mPushSession.sendMsgToOne(userId, userName, msg);
        } catch (ChatMsgIllegalException e) {
            Toast.makeText(mActivity, e.getMsg(), Toast.LENGTH_SHORT).show();
        }
    }

}
