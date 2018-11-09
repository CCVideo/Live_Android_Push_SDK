package com.bokecc.sdk.mobile.push.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.bokecc.sdk.mobile.push.core.DWPushConfig;
import com.bokecc.sdk.mobile.push.core.DWPushSession;
import com.bokecc.sdk.mobile.push.core.Observer;
import com.bokecc.sdk.mobile.push.entity.SpeedRtmpNode;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.base.BaseOnTitleClickListener;
import com.bokecc.sdk.mobile.push.example.base.activity.TitleActivity;
import com.bokecc.sdk.mobile.push.example.contract.HomeContract;
import com.bokecc.sdk.mobile.push.example.global.Config;
import com.bokecc.sdk.mobile.push.example.presenter.HomePresenter;
import com.bokecc.sdk.mobile.push.example.view.SettingItemLayout;
import com.bokecc.sdk.mobile.push.example.view.ToggleButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 直播推流设置界面
 *
 * @author CC
 */
public class SettingActivity extends TitleActivity<HomePresenter, SettingActivity.HomeViewHolder> implements HomeContract.View, Observer {

    // 当前的帧率
    private int mFpsCurrentValue = 16;

    //当前的码率
    private int mBitrateCurrentValue = 550;

    // 使用美颜
    private boolean mUseBeauty = true;

    // 方向索引
    private int mOrientationIndex = 0;

    // 摄像头索引
    private int mCameraTypeIndex = 0;

    // 分辨率索引
    private int mResolutionIndex = 0;

    // 服务端节点
    private int mServerPos;

    // 推荐节点的索引
    private int mRecommendIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        String titleContent = "";
        if (DWPushSession.getInstance() != null && !TextUtils.isEmpty(DWPushSession.getInstance().getLiveRoomName())) {
            titleContent = DWPushSession.getInstance().getLiveRoomName();
        } else {
            titleContent = "设置";
        }

        // 设置界面 Title
        setTitleStatus(TitleImageStatus.SHOW, R.drawable.title_back, titleContent, TitleImageStatus.DISMISS, R.drawable.login_qrcode,
                new BaseOnTitleClickListener() {
                    @Override
                    public void onLeftClick() {
                        mPresenter.exit();
                    }
                }
        );

        mRecommendIndex = DWPushSession.getInstance().getRecommendIndex();
    }

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected HomeViewHolder getViewHolder() {
        return new HomeViewHolder(getContentView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        DWPushSession.getInstance().register(this);
        if (mPresenter == null) {
            mPresenter = getPresenter();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DWPushSession.getInstance().unregister(null, this);
    }

    /**
     * 获取最大码率
     *
     * @return 最大码率
     */
    private int getMaxBitrate() {
        return DWPushSession.getInstance().getMaxBitrate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 || data == null) {
            return;
        }
        int progress = data.getIntExtra(SeekBarActivity.KEY_CURRENT_VALUE, 0);
        String value = Integer.toString(progress);
        switch (resultCode) {
            case Config.RESULT_CODE_FPS:
                mFpsCurrentValue = progress;
                mViewHolder.mFps.setValue(value + "帧/秒");
                break;
            case Config.RESULT_CODE_BITRATE:
                mBitrateCurrentValue = progress;
                mViewHolder.mBitrate.setValue(value + "kbs");
                break;
            case Config.SELECT_TYPE_CAMERA:
                mCameraTypeIndex = data.getIntExtra(Config.SELECT_POSITION, 0);
                mViewHolder.mCamera.setValue(mCameraTypeIndex == 0 ? "前置摄像头" : "后置摄像头");
                break;

            case Config.SELECT_TYPE_RESOLUTION:
                mResolutionIndex = data.getIntExtra(Config.SELECT_POSITION, 0);
                switch (mResolutionIndex) {
                    case 0:
                        mViewHolder.mResolution.setValue("360P");
                        break;
                    case 1:
                        mViewHolder.mResolution.setValue("480P");
                        break;
                    case 2:
                        mViewHolder.mResolution.setValue("720P");
                        break;
                    default:
                        break;
                }
                break;
            case Config.SELECT_TYPE_SERVER:
                mServerPos = data.getIntExtra(Config.SELECT_POSITION, 0);
                SpeedRtmpNode rtmpNode = DWPushSession.getInstance().getAllRtmpNodes().get(mServerPos);
                mViewHolder.mServer.setValue(rtmpNode.getDesc());
                break;
        }
    }

    @Override
    public void update() {
        finish();
        go(LoginActivity.class);
    }

    class HomeViewHolder extends TitleActivity.ViewHolder {
        @BindView(R.id.id_item_orientation)
        ToggleButton mOrientation;
        @BindView(R.id.id_item_beauty)
        ToggleButton mBeauty;
        @BindView(R.id.id_setting_camera)
        SettingItemLayout mCamera;
        @BindView(R.id.id_setting_resolution)
        SettingItemLayout mResolution;
        @BindView(R.id.id_setting_fps)
        SettingItemLayout mFps;
        @BindView(R.id.id_setting_bitrate)
        SettingItemLayout mBitrate;
        @BindView(R.id.id_setting_server)
        SettingItemLayout mServer;

        @BindView(R.id.id_push_btn)
        Button mPushBtn;

        public HomeViewHolder(View view) {
            super(view);
            // 初始化各个模块的界面
            initCameraWindow();
            initOrientationWindow();
            initResolutionWindow();
            initBeautyWindow();
            initFpsWindow();
            initBitrateWindow();
            initServerWindow();
        }

        // 初始化横屏模式
        private void initOrientationWindow() {
            mOrientation.setCheckedImmediately(mOrientationIndex == 1);
            mOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mOrientationIndex = isChecked ? 1 : 0;
                }
            });
        }

        // 初始化美颜模式
        private void initBeautyWindow() {
            mBeauty.setCheckedImmediately(mUseBeauty);
            mBeauty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mUseBeauty = isChecked;
                }
            });
        }

        // 初始化摄像头选项模式
        private void initCameraWindow() {
            mCamera.setValue(mCameraTypeIndex == 0 ? "前置摄像头" : "后置摄像头");
            mCamera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Config.SELECT_TYPE, Config.SELECT_TYPE_CAMERA);
                    bundle.putInt(Config.SELECT_POSITION, mCameraTypeIndex);
                    goForResult(SelectActivity.class, Config.SETTING_REQUEST_CODE, bundle);
                }
            });
        }

        // 初始化分辨率选项模式
        private void initResolutionWindow() {
            String mResolutionDesc = "";
            if (mResolutionIndex == 0) {
                mResolutionDesc = "360P";
            } else if (mResolutionIndex == 1) {
                mResolutionDesc = "480P";
            } else if (mResolutionIndex == 2) {
                mResolutionDesc = "720P";
            }
            mResolution.setValue(mResolutionDesc);
            mResolution.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Config.SELECT_TYPE, Config.SELECT_TYPE_RESOLUTION);
                    bundle.putInt(Config.SELECT_POSITION, mResolutionIndex);
                    goForResult(SelectActivity.class, Config.SETTING_REQUEST_CODE, bundle);
                }
            });
        }

        // 初始化帧率选项模式
        private void initFpsWindow() {
            mFps.setValue(String.valueOf(mFpsCurrentValue + "帧/秒"));

            mFps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Config.SELECT_TYPE, Config.SELECT_TYPE_FPS);
                    bundle.putInt(SeekBarActivity.KEY_MIN, 10);
                    bundle.putInt(SeekBarActivity.KEY_MAX, 30);
                    bundle.putInt(SeekBarActivity.KEY_VALUE, mFpsCurrentValue);
                    bundle.putInt(SeekBarActivity.KEY_RESULT_CODE, Config.REQUEST_CODE_FPS);
                    bundle.putString(SeekBarActivity.KEY_TITLE, "帧率");
                    goForResult(SeekBarActivity.class, Config.REQUEST_CODE_FPS, bundle);
                }
            });
        }

        // 初始化码率选项模式
        private void initBitrateWindow() {
            // 如果当前默认的码率超过了用户当前账号支持的最大码率，则修正一下
            if (mBitrateCurrentValue > getMaxBitrate()) {
                mBitrateCurrentValue = getMaxBitrate();
            }
            mBitrate.setValue(String.valueOf(mBitrateCurrentValue + "kbs"));
            mBitrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Config.SELECT_TYPE, Config.SELECT_TYPE_BITRATE);
                    bundle.putInt(SeekBarActivity.KEY_MIN, 400);
                    bundle.putInt(SeekBarActivity.KEY_MAX, getMaxBitrate());
                    bundle.putInt(SeekBarActivity.KEY_VALUE, mBitrateCurrentValue);
                    bundle.putInt(SeekBarActivity.KEY_RESULT_CODE, Config.REQUEST_CODE_BITRATE);
                    bundle.putString(SeekBarActivity.KEY_TITLE, "码率");
                    goForResult(SeekBarActivity.class, Config.REQUEST_CODE_BITRATE, bundle);
                }
            });
        }

        // 初始化服务器选项
        private void initServerWindow() {
            mServerPos = DWPushSession.getInstance().getRecommendIndex();
            SpeedRtmpNode rtmpNode = DWPushSession.getInstance().getRtmpNodes().get(mServerPos);
            mServer.setValue(rtmpNode.getDesc());
            mServer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Config.SELECT_TYPE, Config.SELECT_TYPE_SERVER);
                    bundle.putInt(Config.SELECT_POSITION, mServerPos);
                    goForResult(SelectActivity.class, Config.SETTING_REQUEST_CODE, bundle);
                }
            });
        }

        @OnClick(R.id.id_push_btn)
        void startPush() {
            Bundle bundle = new Bundle();
            DWPushConfig pushConfig = new DWPushConfig.DWPushConfigBuilder().
                    fps(mFpsCurrentValue).
                    beauty(mUseBeauty).
                    bitrate(mBitrateCurrentValue).
                    orientation(mOrientationIndex == 0 ? DWPushConfig.PORTRAIT : DWPushConfig.LANDSCAPE).
                    cameraType(mCameraTypeIndex == 0 ? DWPushConfig.CAMERA_FRONT : DWPushConfig.CAMERA_BACK).
                    videoResolution(mResolutionIndex == 0 ? DWPushConfig.RESOLUTION_LOW :
                            (mResolutionIndex == 1) ? DWPushConfig.RESOLUTION_SD : DWPushConfig.RESOLUTION_HD).
                    rtmpNodeIndex(mServerPos).
                    build();
            bundle.putSerializable(PushActivity.KEY_PUSH_CONFIG, pushConfig);
            go(PushActivity.class, bundle);
        }
    }
}
