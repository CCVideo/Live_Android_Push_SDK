package com.bokecc.sdk.mobile.push.example.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.core.DWPushSession;
import com.bokecc.sdk.mobile.push.entity.SpeedRtmpNode;
import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.adapter.SelectAdapter;
import com.bokecc.sdk.mobile.push.example.adapter.ServerRecycleAdapter;
import com.bokecc.sdk.mobile.push.example.adapter.StringRecycleAdapter;
import com.bokecc.sdk.mobile.push.example.global.Config;
import com.bokecc.sdk.mobile.push.example.popup.TxtLoadingPopup;
import com.bokecc.sdk.mobile.push.example.util.DensityUtil;
import com.bokecc.sdk.mobile.push.example.view.BaseOnItemTouch;
import com.bokecc.sdk.mobile.push.example.view.OnClickListener;
import com.bokecc.sdk.mobile.push.example.view.RecycleViewDivider;
import com.bokecc.sdk.mobile.push.tools.DWRtmpNodeTool;

import java.util.ArrayList;

/**
 * Radio 选择界面 用于选择摄像头、分辨率、服务器
 *
 * @author CC
 */
public class SelectActivity extends AppCompatActivity implements View.OnClickListener {

    private int mType;
    private int mSelPosition = 0;

    private SelectAdapter mAdapter;

    private View mRoot;
    private ImageView mBackView;
    private TextView mTitleView, mListRight, mTipView;
    private RecyclerView mSelectDatas;
    private TxtLoadingPopup mLoadingPopup;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        mType = getIntent().getExtras().getInt(Config.SELECT_TYPE);
        mSelPosition = getIntent().getExtras().getInt(Config.SELECT_POSITION);

        setContentView(R.layout.activity_select);
        mRoot = getWindow().getDecorView().findViewById(android.R.id.content);
        mBackView = (ImageView) findViewById(R.id.id_list_back);
        mTitleView = (TextView) findViewById(R.id.id_list_title);
        mListRight = (TextView) findViewById(R.id.id_list_right);

        mTipView = (TextView) findViewById(R.id.id_select_tip);

        // 初始化RecyclerView
        mSelectDatas = (RecyclerView) findViewById(R.id.id_select_datas);
        mSelectDatas.setLayoutManager(new LinearLayoutManager(this));
        mSelectDatas.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 1),
                Color.parseColor("#E8E8E8"), DensityUtil.dp2px(this, 19), DensityUtil.dp2px(this, 19)));

        mBackView.setOnClickListener(this);

        ArrayList<String> datas = new ArrayList<>();

        switch (mType) {
            case Config.SELECT_TYPE_CAMERA:
                mTipView.setText(getResources().getString(R.string.select_camera_tip));
                mTitleView.setText(getResources().getString(R.string.camera));

                mAdapter = new StringRecycleAdapter(this);
                datas.add("前置摄像头");
                datas.add("后置摄像头");
                mAdapter.bindDatas(datas);

                break;
            case Config.SELECT_TYPE_RESOLUTION:
                mTipView.setText(getResources().getString(R.string.select_resolution_tip));
                mTitleView.setText(getResources().getString(R.string.resolution));

                mAdapter = new StringRecycleAdapter(this);
                datas.add("360P");
                datas.add("480P");
                datas.add("720P");
                mAdapter.bindDatas(datas);

                break;
            case Config.SELECT_TYPE_SERVER:
                mTipView.setText(getResources().getString(R.string.select_server_tip));
                mTitleView.setText(getResources().getString(R.string.server));
                mListRight.setText("测速");

                mAdapter = new ServerRecycleAdapter(this);
                mAdapter.bindDatas(DWPushSession.getInstance().getAllRtmpNodes());

                mLoadingPopup = new TxtLoadingPopup(this);
                mLoadingPopup.setKeyBackCancel(true);
                mLoadingPopup.setOutsideCancel(true);
                mLoadingPopup.setTipValue("正在测速...");

                mListRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showLoading();
                        DWRtmpNodeTool.testSpeedForRtmpNodes(new DWRtmpNodeTool.OnTestSpeedFinishListener() {
                            @Override
                            public void onFinish(final ArrayList<SpeedRtmpNode> rtmpNodes) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoading();
                                        updateServers(rtmpNodes);
                                    }
                                });
                            }

                            @Override
                            public void onError(String s) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoading();
                                    }
                                });
                            }
                        });
                    }
                });

                break;
            default:
                throw new RuntimeException("SelectActivity error type");
        }

        mSelectDatas.addOnItemTouchListener(new BaseOnItemTouch(mSelectDatas,
                new OnClickListener() {
                    @Override
                    public void onClick(RecyclerView.ViewHolder viewHolder) {
                        mSelPosition = mSelectDatas.getChildAdapterPosition(viewHolder.itemView);
                        mAdapter.setSelPosition(mSelPosition);
                        final Intent data = new Intent();
                        data.putExtra(Config.SELECT_TYPE, mType);
                        data.putExtra(Config.SELECT_POSITION, mSelPosition);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finishWithData(mType, data);
                            }
                        }, 300L);
                    }
                }));

        mAdapter.setSelPosition(mSelPosition); // 设置默认选中
        mSelectDatas.setAdapter(mAdapter);
    }

    public void updateServers(ArrayList<SpeedRtmpNode> rtmpNodes) {
        // 查找推荐节点位置
        for (SpeedRtmpNode rtmp : rtmpNodes) {
            if (rtmp.isRecommend()) {
                mSelPosition = rtmp.getIndex();
                break;
            }
        }
        mAdapter.setSelPosition(mSelPosition);
        mAdapter.bindDatas(rtmpNodes);
        mAdapter.notifyDataSetChanged();
    }

    public void showLoading() {
        mLoadingPopup.show(mRoot);
    }

    public void dismissLoading() {
        mLoadingPopup.dismiss();
    }

    public void finishWithData(int resultCode, Intent data) {
        setResult(resultCode, data);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_list_back:
                Intent data = new Intent();
                data.putExtra(Config.SELECT_TYPE, mType);
                data.putExtra(Config.SELECT_POSITION, mSelPosition);
                setResult(mType, data);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(Config.SELECT_TYPE, mType);
        data.putExtra(Config.SELECT_POSITION, mSelPosition);
        setResult(mType, data);
        finish();
    }
}
