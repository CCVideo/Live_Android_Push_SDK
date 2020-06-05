package com.bokecc.sdk.mobile.push.example.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.chat.model.ChatUser;
import com.bokecc.sdk.mobile.push.core.DWPushConfig;
import com.bokecc.sdk.mobile.push.core.DWPushSession;
import com.bokecc.sdk.mobile.push.example.DWApplication;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.adapter.ChatAdapter;
import com.bokecc.sdk.mobile.push.example.adapter.EmojiAdapter;
import com.bokecc.sdk.mobile.push.example.adapter.PrivateChatAdapter;
import com.bokecc.sdk.mobile.push.example.adapter.PrivateUserAdapter;
import com.bokecc.sdk.mobile.push.example.base.BasePopupWindow;
import com.bokecc.sdk.mobile.push.example.base.activity.BaseActivity;
import com.bokecc.sdk.mobile.push.example.contract.PushContract;
import com.bokecc.sdk.mobile.push.example.listener.ClickItemTouchListener;
import com.bokecc.sdk.mobile.push.example.logging.LogcatHelper;
import com.bokecc.sdk.mobile.push.example.model.ChatEntity;
import com.bokecc.sdk.mobile.push.example.model.PrivateUser;
import com.bokecc.sdk.mobile.push.example.popup.CommonPopup;
import com.bokecc.sdk.mobile.push.example.presenter.PushPresenter;
import com.bokecc.sdk.mobile.push.example.util.DensityUtil;
import com.bokecc.sdk.mobile.push.example.util.EmojiUtil;
import com.bokecc.sdk.mobile.push.example.util.RoomUtils;
import com.bokecc.sdk.mobile.push.example.util.SoftKeyBoardState;
import com.bokecc.sdk.mobile.push.example.view.BaseOnItemTouch;
import com.bokecc.sdk.mobile.push.example.view.OnClickListener;
import com.bokecc.sdk.mobile.push.view.DWTextureView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bokecc.sdk.mobile.push.core.DWPushConfig.PORTRAIT;
import static com.bokecc.sdk.mobile.push.example.global.Config.SETTING_REQUEST_CODE;
import static com.bokecc.sdk.mobile.push.view.DWTextureView.MODE_OUTSIDE;

public class PushActivity extends BaseActivity<PushPresenter> implements PushContract.View {

    private static final String TAG = PushActivity.class.getSimpleName();

    public static final String KEY_PUSH_CONFIG = "cc_demo_push_config";

    @BindView(R.id.id_push_status)
    TextView mStatusText;
    @BindView(R.id.id_push_time)
    TextView mPushTime;
    @BindView(R.id.id_push_gl_surface)
    DWTextureView mTextureView;
    @BindView(R.id.id_push_temp_frame)
    FrameLayout mFrameLayout;
    @BindView(R.id.id_push_beautiful_window)
    View mBeautifulView;
    @BindView(R.id.id_beautiful_skin)
    DiscreteSeekBar mSkinBlurSeek;
    @BindView(R.id.id_beautiful_white)
    DiscreteSeekBar mWhiteSeek;
    @BindView(R.id.id_beautiful_pink)
    DiscreteSeekBar mPinkSeek;
    @BindView(R.id.id_push_volume_window)
    View mVolumeView;
    @BindView(R.id.id_volume_seek)
    DiscreteSeekBar mVolumeSeek;
    @BindView(R.id.id_push_username)
    TextView mUsername;
    @BindView(R.id.id_push_watch_count)
    TextView mRoomUserCount;
    @BindView(R.id.id_push_oper)
    ConstraintLayout mOperLayout;
    @BindView(R.id.id_chat_send_input_layout)
    RelativeLayout mChatInputLayout;
    @BindView(R.id.id_push_chat_list)
    RecyclerView mChatList;
    @BindView(R.id.id_private_chat_title)
    TextView mPrivateChatUserName;
    @BindView(R.id.id_private_chat_user_list)
    RecyclerView mPrivateChatUserList;
    @BindView(R.id.id_private_chat_user_layout)
    LinearLayout mPrivateChatUserLayout;
    @BindView(R.id.id_private_chat_list)
    RecyclerView mPrivateChatMsgList;
    @BindView(R.id.id_private_chat_msg_layout)
    LinearLayout mPrivateChatMsgLayout;
    @BindView(R.id.id_push_chat_layout)
    LinearLayout mChatLayout;
    @BindView(R.id.id_push_emoji_grid)
    GridView mEmojiGrid;
    @BindView(R.id.id_push_chat_emoji)
    ImageView mEmoji;
    @BindView(R.id.id_push_chat_content)
    EditText mChatInput;
    @BindView(R.id.id_push_beauty)
    ImageView mBeauty;
    @BindView(R.id.id_push_root)
    View mRoot;
    @BindView(R.id.id_push_mask_layer)
    FrameLayout mMaskLayer;
    @BindView(R.id.id_push_net)
    TextView mPushNet;
    @BindView(R.id.id_push_chat)
    ImageView mIvPushChat;
    @BindView(R.id.id_push_private_chat)
    ImageView mPrivateIcon;
    @BindView(R.id.id_push_record)
    ImageView mIvRecord;

    @BindView(R.id.id_push_layout)
    RelativeLayout mPushLayout;
    @BindView(R.id.id_preview_layout)
    RelativeLayout mPreviewLayout;
    @BindView(R.id.room_title)
    TextView mRoomTitle;
    @BindView(R.id.id_preview_beauty)
    ImageView mBeautySwitch;  // 美颜开关
    @BindView(R.id.id_push_stop_record)
    ImageView mPushStop;

    @BindView(R.id.id_record_status)
    TextView mRecordStatus;


    private InputMethodManager mInputMethodManager;

    // 软键盘是否显示
    private boolean isSoftInput = false;
    // emoji是否需要显示 emoji是否显示
    private boolean isEmoji = false, isEmojiShow = false;

    // 是否显示私聊用户列表
    private boolean isPrivateChatUser = false;
    // 是否显示私聊列表
    private boolean isPrivateChatMsg = false;

    private String mCurPrivateUserId = "";
    // 聊天集合
    private ArrayList<ChatEntity> mChatEntities;
    // 存放所有的私聊信息
    private ArrayList<ChatEntity> mPrivateChats;

    // 公聊数据适配器
    private ChatAdapter mChatAdapter;
    // 私聊用户列表适配器
    private PrivateUserAdapter mPrivateUserAdapter;
    // 私聊信息列表
    private PrivateChatAdapter mPrivateChatAdapter;

    private boolean isNoNeedShow;
    private ChatUser mTo;
    // 是否开启美颜
    private boolean isBeauty = true;
    // 前后摄像头
    private int cameraType;
    // 磨皮 粉嫩 美白
    private int mSkinLevel = 1, mPinkLevel = 1, mWhiteLevel = 1;

    private DWPushSession mPushSession;
    private DWPushConfig mPushConfig;

    /**
     * 退出对话框
     */
    private CommonPopup mExitPopup;
    // 是否开启了直播
    private boolean isPushing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if (DWApplication.mReportLog) {
            // 开始日志输出
            if (mPushSession != null && !TextUtils.isEmpty(mPushSession.getRoomId())) {
                LogcatHelper.getInstance(this).init(this, mPushSession.getRoomId());
                LogcatHelper.getInstance(this).start();
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_push;
    }

    @Override
    protected void setUpView(Bundle savedInstanceState) {
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 设置屏幕方向
        if (savedInstanceState != null) {
            mPushConfig = (DWPushConfig) savedInstanceState.getSerializable(STATE_SCORE);
        } else {
            mPushConfig = (DWPushConfig) getIntent().getSerializableExtra(KEY_PUSH_CONFIG);
        }

        if (mPushConfig.orientation == PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        // 设置适配屏幕比例
        initAspectRatio();
        // 获取DWPushSession实例
        mPushSession = DWPushSession.getInstance();
        mPresenter.setPushSession(mPushSession);
        // 显示预览界面
        initPreviewView();
        configPush(mPushConfig);


    }

    private void initPreviewView() {
        mPushLayout.setVisibility(View.GONE);
        mPreviewLayout.setVisibility(View.VISIBLE);
        // 显示直播间名称
        String titleContent = DWPushSession.getInstance().getLiveRoomName();
        mRoomTitle.setText(titleContent);

        mPushSession.setTextureView(mTextureView);
        mPresenter.prepare(mPushConfig);
        isPushing = false;
        mPresenter.setPushing(false);
    }


    private void initPushView() {
        if (isPushing) return;
        mPresenter.setPushing(true);
        isPushing = true;
        mPushLayout.setVisibility(View.VISIBLE);
        mPreviewLayout.setVisibility(View.GONE);
        // 显示主播名称
        mUsername.setText(String.format("主播： %s", mPushSession.getUserName()));

        // 初始化私聊数据集合
        mPrivateChats = new ArrayList<>();
        // 声音和美颜面板
        mBeautifulView.setVisibility(View.GONE);
        mVolumeView.setVisibility(View.GONE);
        mBeautifulView.setClickable(true);
        mVolumeView.setClickable(true);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBeautifulView.getLayoutParams();
        params.width = DensityUtil.dp2px(this, 300);
        mBeautifulView.setLayoutParams(params);
        params = (RelativeLayout.LayoutParams) mVolumeView.getLayoutParams();
        params.width = DensityUtil.dp2px(this, 300);
        mVolumeView.setLayoutParams(params);

        // 表情 Emoji
        EmojiAdapter emojiAdapter = new EmojiAdapter(this);
        emojiAdapter.bindData(EmojiUtil.imgs);
        mEmojiGrid.setAdapter(emojiAdapter);
        mEmojiGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == EmojiUtil.imgs.length - 1) {
                    mPresenter.deleteInputOne(mChatInput);
                } else {
                    mPresenter.addEmoji(mChatInput, position);
                }
            }
        });

        initVolumeAndBeautifulProgress();

        mPresenter.addChatRoomUserCountListener();
        mPresenter.loopForRoomUserCount();
        // 添加聊天回调
        mChatEntities = new ArrayList<>();
        mPresenter.addChatMsgListener();

        configRecycleView();
        addProgressListener();
        dealChatView();
        initStopWindow();
        initPrivateChatView();


    }


    // 设置屏幕适配方案
    private void initAspectRatio() {
        int screenWidth, screenHeight;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        //----------------页面占满全屏适配逻辑----------------------
        // 对于一些屏幕比较长的屏幕，16：9无法占满全屏，需要调用这个方法，占用全屏
        mTextureView.setAspectRatio(MODE_OUTSIDE, screenWidth, screenHeight);


        //------------页面 16 ：9 适配逻辑--------------------------
        // 如果需要设置16：9适配，这里解除注释
//        int baseSize;
//        if (screenWidth > screenHeight) {
//            baseSize = screenHeight;
//        } else {
//            baseSize = screenWidth;
//        }
//
//        int initialWidth, initialHeight;
//        if (mPushConfig.orientation == PORTRAIT) {
//            initialWidth = baseSize;
//            initialHeight = initialWidth / 9 * 16;
//            if (initialHeight > screenHeight) {
//                initialHeight = screenHeight;
//            }
//        } else {
//            initialHeight = baseSize;
//            initialWidth = initialHeight / 9 * 16;
//        }

//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(initialWidth, initialHeight);
//        layoutParams.gravity = Gravity.CENTER;
//        mRoot.setLayoutParams(layoutParams);
    }

    // 初始化私聊相关的View
    private void initPrivateChatView() {
        mPrivateChatUserList.setLayoutManager(new LinearLayoutManager(this));
        mPrivateUserAdapter = new PrivateUserAdapter(this);
        mPrivateChatUserList.setAdapter(mPrivateUserAdapter);
        mPrivateChatUserList.addOnItemTouchListener(new BaseOnItemTouch(mPrivateChatUserList, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                // 隐藏用户列表
                mMaskLayer.setVisibility(View.GONE);
                mPrivateChatUserLayout.setVisibility(View.GONE);
                int position = mPrivateChatUserList.getChildAdapterPosition(viewHolder.itemView);
                PrivateUser privateUser = mPrivateUserAdapter.getPrivateUsers().get(position);
                privateUser.setRead(true);
                mPrivateUserAdapter.notifyDataSetChanged();
                mPrivateIcon.setImageResource(R.drawable.push_private_msg);
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setUserId(privateUser.getId());
                chatEntity.setUserName(privateUser.getName());
                chatEntity.setUserAvatar(privateUser.getAvatar());
                click2PrivateChat(chatEntity, true);
            }
        }));

        mPrivateChatMsgList.setLayoutManager(new LinearLayoutManager(this));
        mPrivateChatAdapter = new PrivateChatAdapter(this);
        mPrivateChatMsgList.setAdapter(mPrivateChatAdapter);
        mPrivateChatMsgList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideEmoji();
                if (isSoftInput) {
                    mInputMethodManager.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    // 初始化停止直播的弹出界面
    private void initStopWindow() {
        mExitPopup = new CommonPopup(this);
        mExitPopup.setTip("停止直播");
        mExitPopup.setOutsideCancel(true);
        mExitPopup.setBackPressedCancel(true);
        mExitPopup.setOnCancelClickListener(new CommonPopup.OnCancelClickListener() {
            @Override
            public void onCancel() {
                mExitPopup.dismiss(null);
            }
        });
        mExitPopup.setOnOkClickListener(new CommonPopup.OnOkClickListener() {
            @Override
            public void onOk() {
                mPresenter.stop();
                mExitPopup.dismiss(new BasePopupWindow.OnDismissStatusListener() {
                    @Override
                    public void onDismissStart() {
                    }

                    @Override
                    public void onDismissEnd() {
                        exit();
                    }
                });
            }
        });
    }

    /**
     * 点击发起私聊
     */
    private void click2PrivateChat(ChatEntity chatEntity, boolean flag) {

        // 横屏模式下隐藏私聊模块
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            return;
        }

        if (flag) { // 私聊用户列表点击发起私聊
            goPrivateChat(chatEntity);
            mCurPrivateUserId = chatEntity.getUserId();
        } else {
            if (!chatEntity.isPublisher()) { // 如果当前被点击的用户不是主播，则进行私聊
                goPrivateChat(chatEntity);
                mCurPrivateUserId = chatEntity.getUserId();
            }
        }
    }

    /**
     * 处理聊天相关
     */
    private void dealChatView() {
        if (RoomUtils.isShowChat()) {
            mIvPushChat.setVisibility(View.VISIBLE);
            mPrivateIcon.setVisibility(View.VISIBLE);
        } else {
            mIvPushChat.setVisibility(View.GONE);
            mPrivateIcon.setVisibility(View.GONE);
        }

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 软键盘监听
        SoftKeyBoardState mSoftKeyBoardState = new SoftKeyBoardState(mRoot, false);
        mSoftKeyBoardState.setOnSoftKeyBoardStateChangeListener(new SoftKeyBoardState.OnSoftKeyBoardStateChangeListener() {
            @Override
            public void onChange(boolean isShow) {
                isSoftInput = isShow;
                if (!isSoftInput) { // 软键盘隐藏
                    if (isEmoji) {
                        mEmojiGrid.setVisibility(View.VISIBLE);// 避免闪烁
                        isEmojiShow = true; // 修改emoji显示标记
                        isEmoji = false; // 重置
                    } else {
                        dismissChatLayout(); // 隐藏聊天操作区域
                    }
                    if (!isPrivateChatMsg && !isEmojiShow) { // 私聊软键盘隐藏时，显示公聊列表
                        mChatList.setVisibility(View.VISIBLE);
                    }
                } else {
                    hideEmoji();
                    mChatList.setVisibility(View.GONE);
                }
            }
        });
    }

    // 隐藏直播进行聊天的区域
    private void dismissChatLayout() {
        mOperLayout.setVisibility(View.VISIBLE);
        mChatInput.setFocusableInTouchMode(false);
        mChatInput.clearFocus();
        mFrameLayout.setVisibility(View.GONE);
        mChatLayout.setVisibility(View.INVISIBLE);
        isSoftInput = false;
    }

    private void addProgressListener() {
        mVolumeSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                Log.d("dds_test", String.valueOf(value));
                mPresenter.updateVolume(value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                mPresenter.updateVolume(seekBar.getProgress());
            }
        });

        mSkinBlurSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                mSkinLevel = seekBar.getProgress();
                mPresenter.updateBeautifulLevel(mWhiteLevel, mSkinLevel, mPinkLevel);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                mSkinLevel = seekBar.getProgress();
                mPresenter.updateBeautifulLevel(mWhiteLevel, mSkinLevel, mPinkLevel);
            }
        });

        mWhiteSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                mWhiteLevel = seekBar.getProgress();
                mPresenter.updateBeautifulLevel(mWhiteLevel, mSkinLevel, mPinkLevel);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                mWhiteLevel = seekBar.getProgress();
                mPresenter.updateBeautifulLevel(mWhiteLevel, mSkinLevel, mPinkLevel);
            }
        });

        mPinkSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                mPinkLevel = seekBar.getProgress();
                mPresenter.updateBeautifulLevel(mWhiteLevel, mSkinLevel, mPinkLevel);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                mPinkLevel = seekBar.getProgress();
                mPresenter.updateBeautifulLevel(mWhiteLevel, mSkinLevel, mPinkLevel);
            }
        });
    }

    private void configPush(DWPushConfig pushConfig) {
        // 设置美颜的开关
        isBeauty = pushConfig.isBeauty;
        if (isBeauty) {
            mPushSession.openBeauty();
            mBeauty.setImageResource(R.drawable.push_beauty_open);
        } else {
            mPushSession.closeBeauty();
            mBeauty.setImageResource(R.drawable.push_beauty_close);
        }
        // 设置摄像头方向
        cameraType = pushConfig.cameraType;
    }

    //添加水印
    private void addIcon() {
        // 支持增加图片水印
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.em2_04);
        Rect rect = new Rect(300, 100, 300 + 36, 100 + 36);
        mPushSession.setIcon(bitmap, rect);
    }

    private void configRecycleView() {
        mChatList.addOnItemTouchListener(new ClickItemTouchListener(mChatList, new ClickItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 横屏模式下隐藏私聊模块
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return;
                }

                if (isNoNeedShow) {
                    isNoNeedShow = false;
                    return;
                }
                ChatEntity chatEntity = mChatEntities.get(position);
                // 主播对自己发起私聊 不被容许
                if (chatEntity.getUserId().equals(mPushSession.getUserId())) {
                    return;
                }
                showChatLayout();
                mTo = new ChatUser();
                mTo.setUserId(chatEntity.getUserId());
                mTo.setUserName(chatEntity.getUserName());
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onTouchEventUp() {
            }

            @Override
            public void onTouchEventDown() {
                if (!isSoftInput) {
                    if (mBeautifulView.getVisibility() == View.VISIBLE ||
                            mVolumeView.getVisibility() == View.VISIBLE) {
                        isNoNeedShow = true;
                    }
                    dismissVolume();
                }
            }
        }));
        mChatList.setLayoutManager(new LinearLayoutManager(this));
        mChatAdapter = new ChatAdapter(this);
        mChatAdapter.setChatEntities(mChatEntities);
        mChatList.setAdapter(mChatAdapter);
        mChatList.addOnItemTouchListener(new BaseOnItemTouch(mChatList, new OnClickListener() {
            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder) {
                // 横屏模式下隐藏私聊模块
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    return;
                }

                int position = mChatList.getChildAdapterPosition(viewHolder.itemView);
                ChatEntity chatEntity = mChatAdapter.getChatEntities().get(position);
                click2PrivateChat(chatEntity, false);
            }
        }));

    }

    private void showChatLayout() {
        mTo = null;
        mOperLayout.setVisibility(View.INVISIBLE);
        mFrameLayout.setVisibility(View.VISIBLE);
        mChatLayout.setVisibility(View.VISIBLE);
        mChatInput.setFocusableInTouchMode(true);
        mChatInput.requestFocus();
        mChatInputLayout.setVisibility(View.VISIBLE);
        mInputMethodManager.showSoftInput(mChatInput, 0);
    }


    @Override
    public void updateChat(ChatEntity chatEntity) {
        mChatEntities.add(chatEntity);
        mChatAdapter.notifyDataSetChanged();
        mChatList.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);// 进行定位
    }

    @Override
    public void updateChatInput() {
        mChatInput.setText("");
    }

    @Override
    public void updatePrivateChat(ChatEntity chatEntity) {
        // 如果当前界面是私聊信息界面直接在该界面进行数据更新
        if (isPrivateChatMsg && (chatEntity.isPublisher() || chatEntity.getUserId().equals(mCurPrivateUserId))) {
            mPrivateChatAdapter.add(chatEntity);
            mPrivateChatMsgList.smoothScrollToPosition(mPrivateChatAdapter.getItemCount() - 1);// 进行定位
        }
        PrivateUser privateUser = new PrivateUser();
        if (chatEntity.isPublisher()) {
            privateUser.setId(chatEntity.getReceiveUserId());
            privateUser.setName(chatEntity.getReceivedUserName());
            privateUser.setAvatar(chatEntity.getReceiveUserAvatar());
        } else {
            privateUser.setId(chatEntity.getUserId());
            privateUser.setName(chatEntity.getUserName());
            privateUser.setAvatar(chatEntity.getUserAvatar());
        }
        privateUser.setMsg(chatEntity.getMsg());
        privateUser.setTime(chatEntity.getTime());
        // 如果当前就在这个私聊界面，或者收到的私聊是主播发出去的，就设为已读
        privateUser.setRead((isPrivateChatMsg && chatEntity.getUserId().equals(mCurPrivateUserId)) || chatEntity.isPublisher());
        mPrivateUserAdapter.add(privateUser);
        if (!isAllPrivateChatRead()) {
            mPrivateIcon.setImageResource(R.drawable.push_private_msg_new);
        }
        mPrivateChats.add(chatEntity);
    }

    @Override
    public void updateRoomUserCount(int count) {
        mRoomUserCount.setText(String.format("在线人数： %s人", String.valueOf(count)));
    }

    @Override
    public void updatePushTime(final String time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                if (mPushTime != null) {
                    mPushTime.setText(time);
                }
            }
        });
    }

    @Override
    public void updatePushNetState(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                if (mPushNet != null) {
                    mPushNet.setText(String.format("网络状态：%s", state));
                }

            }
        });


    }

    private boolean isCancelRecord;

    @Override
    public void enableRecordMode() {
        if (!isCancelRecord) {
            mIvRecord.setVisibility(View.VISIBLE);
            mPushStop.setVisibility(View.GONE);
            final CommonPopup recordPopup = new CommonPopup(this);
            recordPopup.setOKText("录制");
            recordPopup.setCancelText("不录制");
            recordPopup.setTip("是否需要同时开始录制，不录制不会生成回放");
            recordPopup.setOnOkClickListener(new CommonPopup.OnOkClickListener() {
                @Override
                public void onOk() {

                    mPresenter.startRecord();
                    recordPopup.dismiss(null);


                }
            });
            recordPopup.setOnCancelClickListener(new CommonPopup.OnCancelClickListener() {
                @Override
                public void onCancel() {
                    isCancelRecord = true;
                    recordPopup.dismiss(null);
                }
            });
            recordPopup.show(findViewById(android.R.id.content));
        }
    }

    @Override
    public void disableRecordMode() {
        mIvRecord.setVisibility(View.GONE);
        mRecordStatus.setVisibility(View.GONE);
    }

    @Override
    public void updateRecordState(int res) {
        mIvRecord.setImageResource(res);
    }

    @Override
    public void enableStopButton() {
        mPushStop.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableStopButton() {
        mPushStop.setVisibility(View.GONE);
    }

    @Override
    public void updateRecordText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecordStatus.setText(text);
            }
        });

    }


    @Override
    protected PushPresenter getPresenter() {
        return new PushPresenter(this);
    }

    // 判断是否所有私聊信息全部读完
    private boolean isAllPrivateChatRead() {
        int i = 0;
        for (; i < mPrivateUserAdapter.getPrivateUsers().size(); i++) {
            if (!mPrivateUserAdapter.getPrivateUsers().get(i).isRead()) {
                break;
            }
        }
        return i >= mPrivateUserAdapter.getPrivateUsers().size();
    }

    // -----------------------生命周期---------------------------

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
        Log.d(TAG, "PushActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "PushActivity onResume");
        if (mPrivateIcon != null) {
            // 横屏模式下隐藏私聊模块
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mPrivateIcon.setVisibility(View.GONE);
            } else {
                if (RoomUtils.isShowChat()) {
                    mPrivateIcon.setVisibility(View.VISIBLE);
                } else {
                    mPrivateIcon.setVisibility(View.GONE);
                }
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "PushActivity onPause");

        mMaskLayer.setVisibility(View.GONE);
        hideEmoji();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        Log.d(TAG, "PushActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "PushActivity onDestroy");
        if (mPresenter != null) {
            mPresenter.onDestory();
        }
        if (mExitPopup != null) {
            mExitPopup.release();
        }
        if (DWApplication.mReportLog) {
            // 停止输出日志文件
            LogcatHelper.getInstance(this).stop();
        }
    }

    // -------------------------点击事件----------------------------


    @OnClick(R.id.id_push_mask_layer)
    void dismissAll() {
        mMaskLayer.setVisibility(View.GONE);
        if (isSoftInput) {
            mInputMethodManager.hideSoftInputFromWindow(mChatLayout.getWindowToken(), 0);
        }
        mOperLayout.setVisibility(View.VISIBLE);
        mChatLayout.setVisibility(View.GONE);
        hideEmoji();
        hidePrivateChatUserList();
        hidePrivateChatMsgList();
    }

    @OnClick(R.id.id_push_close)
    void onClose() {
        mExitPopup.show(findViewById(android.R.id.content));
    }

    @OnClick(R.id.id_push_volume)
    void onUpdateVolume() {
        showVolume();
    }

    @OnClick(R.id.id_push_camera)
    void swapCamera() { // 切换相机
        mPushSession.switchCamera();
    }

    @OnClick(R.id.id_push_beauty)
    void toggleBeauty() { // 美颜切换
        if (isBeauty) {
            mPushSession.closeBeauty();
            mBeauty.setImageResource(R.drawable.push_beauty_close);
        } else {
            mPushSession.openBeauty();
            mBeauty.setImageResource(R.drawable.push_beauty_open);
        }
        isBeauty = !isBeauty;
    }

    @OnClick(R.id.id_push_chat)
    void onChat() {
        showChatLayout();
    }

    @OnClick(R.id.id_private_chat_back)
    void backChatUser() { // 返回私聊用户列表
        if (isSoftInput) {
            mInputMethodManager.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
        }
        hidePrivateChatMsgList();
        showPrivateChatUserList();
    }

    @OnClick(R.id.id_push_record)
    void recordClick() {
        // 点击录制按钮
        mPresenter.startRecord();
        isCancelRecord = false;

    }

    @OnClick(R.id.id_push_stop_record)
    void stopRecord() {
        // 点击停止录制
        isCancelRecord = true;
        mPresenter.stopRecord();
    }

    @OnClick(R.id.id_to_push)
    void gotoPush() {
        initPushView();
        mPresenter.start(mPushConfig);


    }

    @OnClick(R.id.id_push_setting_back)
    void backExit() {
        onBackPressed();
    }

    @OnClick(R.id.id_preview_camera)
    void switchCamera() {
        mPushSession.switchCamera();
        cameraType = cameraType == 0 ? 1 : 0;
        mPushConfig.cameraType = cameraType;
    }

    @OnClick(R.id.id_preview_beauty)
    void togglePreviewBeauty() {
        if (isBeauty) {
            mPushSession.closeBeauty();
            isBeauty = false;
            mPushConfig.isBeauty = false;
            mBeautySwitch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.beauty_setting_close));
        } else {
            mPushSession.openBeauty();
            isBeauty = true;
            mPushConfig.isBeauty = true;
            mBeautySwitch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.beauty_setting_open));
        }
    }

    @OnClick(R.id.id_push_screen)
    void previewScreen() {
        if (mPushConfig.orientation == PORTRAIT) {
            mPushConfig.orientation = DWPushConfig.LANDSCAPE;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mPushConfig.orientation = DWPushConfig.PORTRAIT;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @OnClick(R.id.id_push_setting)
    void gotoSetting() {
        Bundle b = new Bundle();
        b.putSerializable(PushActivity.KEY_PUSH_CONFIG, mPushConfig);
        Intent i = new Intent(this, SettingActivity.class);
        i.putExtras(b);
        startActivityForResult(i, SETTING_REQUEST_CODE);
    }


    //---------------------------- 私聊相关 -------------------------------


    // 显示私聊用户列表
    @OnClick(R.id.id_push_private_chat)
    void openPrivateChatUserList() {
        showPrivateChatUserList();
    }

    // 关闭私聊用户列表
    @OnClick(R.id.id_private_chat_user_close)
    void closePrivateChatUserList() {
        hidePrivateChatUserList();
    }

    // 关闭私聊信息列表
    @OnClick(R.id.id_private_chat_close)
    void closePrivate() {
        hidePrivateChatMsgList();
    }

    // 跳转到私聊列表
    private void goPrivateChat(ChatEntity chatEntity) {
        mTo = null;
        mTo = new ChatUser();
        mTo.setUserId(chatEntity.getUserId());
        mTo.setUserName(chatEntity.getUserName());
        ArrayList<ChatEntity> toChatEntitys = new ArrayList<>();
        for (ChatEntity entity : mPrivateChats) {
            // 从私聊列表里面读取到 当前发起私聊的俩个用户聊天列表
            if (entity.isPublisher()) {
                if (entity.getReceiveUserId().equals(chatEntity.getUserId())) {
                    toChatEntitys.add(entity);
                }
            } else {
                if (entity.getUserId().equals(chatEntity.getUserId())) {
                    toChatEntitys.add(entity);
                }
            }
        }
        mPrivateChatAdapter.setDatas(toChatEntitys);
        showPrivateChatMsgList(chatEntity.getUserName());
    }


    /**
     * 显示私聊信息列表
     */
    public void showPrivateChatMsgList(final String username) {
        mOperLayout.setVisibility(View.INVISIBLE);
        mChatLayout.setVisibility(View.VISIBLE);
        mChatInput.setFocusableInTouchMode(true);
        mChatInputLayout.setVisibility(View.VISIBLE);
        mMaskLayer.setVisibility(View.VISIBLE);
        mPrivateChatUserName.setText(username);
        mPrivateChatMsgLayout.setVisibility(View.VISIBLE);
        if (mPrivateChatAdapter.getItemCount() - 1 > 0) {
            mPrivateChatMsgList.scrollToPosition(mPrivateChatAdapter.getItemCount() - 1);// 进行定位
        }
        isPrivateChatMsg = true;
    }

    // 隐藏私聊信息列表
    public void hidePrivateChatMsgList() {
        if (isPrivateChatMsg) {
            hideEmoji();
            if (isSoftInput) {
                mInputMethodManager.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
            }
            mChatList.setVisibility(View.VISIBLE);
            mChatInput.setFocusableInTouchMode(false);
            mChatInput.clearFocus();
            mMaskLayer.setVisibility(View.GONE);
            mChatLayout.setVisibility(View.INVISIBLE);
            mOperLayout.setVisibility(View.VISIBLE);
            mPrivateChatMsgLayout.setVisibility(View.GONE);
            isPrivateChatMsg = false;
        }
    }

    // 显示私聊用户列表
    private void showPrivateChatUserList() {
        mChatList.setVisibility(View.GONE);
        mMaskLayer.setVisibility(View.VISIBLE);
        mOperLayout.setVisibility(View.GONE); // 隐藏推流操作
        mChatLayout.setVisibility(View.VISIBLE); // 隐藏聊天操作
        mPrivateChatUserLayout.setVisibility(View.VISIBLE); // 显示私聊用户列表
        mPrivateChatMsgLayout.setVisibility(View.GONE); // 隐藏私聊信息列表
        mChatInputLayout.setVisibility(View.GONE); // 隐藏私聊输入框
        isPrivateChatUser = true;
    }

    // 隐藏私聊用户列表
    private void hidePrivateChatUserList() {
        if (isPrivateChatUser) {
            mMaskLayer.setVisibility(View.GONE);
            mOperLayout.setVisibility(View.VISIBLE);
            mChatLayout.setVisibility(View.INVISIBLE);
            mPrivateChatUserLayout.setVisibility(View.GONE);
            mChatList.setVisibility(View.VISIBLE);
            isPrivateChatUser = false;
        }
    }


    @OnClick(R.id.id_push_temp_frame)
    void onDismissHandle() {
        mInputMethodManager.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
        dismissVolume();
    }

    @OnClick(R.id.id_push_chat_send)
    void sendChat() {
        String content = mChatInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            toastOnUiThread("输入信息不能为空");
        } else {
            // 发送聊天
            mPresenter.sendChatMsg(content, mTo);
            // 隐藏输入法和emoji
            dismissChatLayout();
            hideEmoji();
            closePrivateChatUserList();
            closePrivate();
            mInputMethodManager.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.id_push_chat_emoji)
    void emoji() {
        if (isEmojiShow) {
            mEmojiGrid.setVisibility(View.GONE);
            isEmojiShow = false; // 修改emoji显示标记
            mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
            mInputMethodManager.showSoftInput(mChatInput, InputMethodManager.SHOW_FORCED);
        } else {
            showEmoji();
        }
    }

    /**
     * 显示emoji
     */
    public void showEmoji() {
        if (isSoftInput) {
            isEmoji = true; // 需要显示emoji
            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
        } else {
            mEmojiGrid.setVisibility(View.VISIBLE);// 避免闪烁
            isEmojiShow = true; // 修改emoji显示标记
        }

        if (!isSoftInput) {
            mChatList.setVisibility(View.GONE);
        }

        mMaskLayer.setVisibility(View.VISIBLE);
        mEmoji.setImageResource(R.drawable.push_chat_emoji);
    }

    /**
     * 隐藏emoji
     */
    public void hideEmoji() {
        if (isEmojiShow) { // 如果emoji显示
            mEmojiGrid.setVisibility(View.GONE);
            isEmojiShow = false; // 修改emoji显示标记
            mEmoji.setImageResource(R.drawable.push_chat_emoji_normal);
            if (!isSoftInput) {
                mChatList.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void initVolumeAndBeautifulProgress() {
        mVolumeSeek.setProgress(mPushSession.getVolume());
        mWhiteLevel = mPushSession.getWhiteLevel();
        mSkinLevel = mPushSession.getSkinBlurLevel();
        mPinkLevel = mPushSession.getPinkLevel();
        mWhiteSeek.setProgress(mWhiteLevel);
        mPinkSeek.setProgress(mPinkLevel);
        mSkinBlurSeek.setProgress(mSkinLevel);

    }

    private boolean isMute = false;// 连麦是否静音

    @Override
    public void showVolume() {
        // 显示音量显示面板
        // mVolumeView.setVisibility(View.VISIBLE);
        // mFrameLayout.setVisibility(View.VISIBLE);


        // 开启和关闭声音
        if (isMute) {
            ((ImageView) findViewById(R.id.id_push_volume)).setImageResource(R.drawable.push_volume);
            mPushSession.updateVolume(6);
        } else {
            ((ImageView) findViewById(R.id.id_push_volume)).setImageResource(R.drawable.push_volume_close);
            mPushSession.updateVolume(0);
        }
        isMute = !isMute;

    }

    @Override
    public void dismissVolume() {
        mVolumeView.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void updateStatus(int color, String message) {
        if (mStatusText != null) {
            mStatusText.setTextColor(color);
            mStatusText.setText(message);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Log.e(TAG, "onActivityResult data null");
                } else {
                    DWPushConfig pushConfig = (DWPushConfig) data.getSerializableExtra(PushActivity.KEY_PUSH_CONFIG);

                    // 根据设置判断是否打开美颜
                    if (pushConfig.isBeauty) {
                        isBeauty = true;
                        mPushSession.openBeauty();
                        mBeautySwitch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.beauty_setting_open));
                    } else {
                        isBeauty = false;
                        mPushSession.closeBeauty();
                        mBeautySwitch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.beauty_setting_close));
                    }
                    // 更新配置，设置前后摄像头的配置
                    if (cameraType != pushConfig.cameraType) {
                        // 摄像头配置更改了，切换摄像头，并更新信息
                        mPushSession.switchCamera();
                        cameraType = cameraType == 0 ? 1 : 0;  // 1 为 前置，0为后置
                        pushConfig.cameraType = cameraType;
                    }
                    if (pushConfig.orientation != mPushConfig.orientation) {
                        setRequestedOrientation(pushConfig.orientation == PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    mPushConfig = pushConfig;
                }
            } else {
                Log.e(TAG, "resultCode is not ok");
            }
        }
    }

    static final String STATE_SCORE = "pushConfig";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_SCORE, mPushConfig);
        super.onSaveInstanceState(outState);
    }

    // ---------------------处理返回键----------------------------
    @Override
    public void onBackPressed() {
        if (!isPushing) {
            super.onBackPressed();
            return;
        }
        if (isEmojiShow) {
            hideEmoji();
            return;
        }
        if (isPrivateChatMsg) {
            hidePrivateChatMsgList();
            showPrivateChatUserList();
            return;
        }
        if (isPrivateChatUser) {
            hidePrivateChatUserList();
            return;
        }
        if (mVolumeView.getVisibility() == View.VISIBLE) {
            dismissVolume();
            return;
        }
        mExitPopup.show(findViewById(android.R.id.content));
    }

}
