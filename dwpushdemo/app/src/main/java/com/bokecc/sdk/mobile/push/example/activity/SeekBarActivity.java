package com.bokecc.sdk.mobile.push.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.push.example.R;
import com.bokecc.sdk.mobile.push.example.global.Config;

/**
 * 拖动条界面 用于设置帧率、码率
 *
 * @author CC
 */
public class SeekBarActivity extends AppCompatActivity {

    public static final String KEY_MIN = "cc_demo_seek_bar_min";
    public static final String KEY_MAX = "cc_demo_seek_bar_max";
    public static final String KEY_VALUE = "cc_demo_seek_bar_value";
    public static final String KEY_TITLE = "cc_demo_seek_bar_title";
    public static final String KEY_RESULT_CODE = "cc_demo_seek_bar_result_code";
    public static final String KEY_CURRENT_VALUE = "cc_demo_current_value";

    private int type, min, max, value, resultCode;
    private String title, unit;

    private SeekBar mSeekBar;
    private ImageView mBackView;
    private TextView mTitleView, mSeedTip, mSeekValue, mSeekTip, mSeekMin, mSeekMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_bar);
        getIntentData();
        initToolBar();
        initSeek();
    }

    // 获取传递过来的参数
    private void getIntentData() {
        type = getIntent().getExtras().getInt(Config.SELECT_TYPE);
        min = getIntent().getIntExtra(KEY_MIN, 0);
        max = getIntent().getIntExtra(KEY_MAX, 0);
        value = getIntent().getIntExtra(KEY_VALUE, 0);
        title = getIntent().getStringExtra(KEY_TITLE);
        resultCode = getIntent().getIntExtra(KEY_RESULT_CODE, 0);
    }

    // 初始化ToolBar
    private void initToolBar() {
        mBackView = (ImageView) findViewById(R.id.id_list_back);
        mTitleView = (TextView) findViewById(R.id.id_list_title);
        mTitleView.setText(title);

        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit(SeekBarActivity.this, resultCode, KEY_CURRENT_VALUE, mSeekBar.getProgress() + min);
                finish();
            }
        });
    }

    // 初始化拖动条模块
    private void initSeek() {
        mSeedTip = (TextView) findViewById(R.id.id_seek_tip);
        mSeekValue = (TextView) findViewById(R.id.id_seek_value);
        mSeekBar = (SeekBar) findViewById(R.id.id_seek_bar);
        mSeekMin = (TextView) findViewById(R.id.id_seek_min_value);
        mSeekMax = (TextView) findViewById(R.id.id_seek_max_value);

        if (type == Config.SELECT_TYPE_FPS) {
            mSeedTip.setText("拖动滑块调整帧率");
            unit = "帧/秒";
        } else if (type == Config.SELECT_TYPE_BITRATE) {
            mSeedTip.setText("拖动滑块调整码率");
            unit = "kbs";
        }

        mSeekBar.setMax(max - min);
        mSeekBar.setProgress(value - min);
        mSeekMin.setText(String.valueOf(min));
        mSeekMax.setText(String.valueOf(max));
        mSeekValue.setText(value + unit);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekValue.setText(String.valueOf(progress + min) + unit);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void commit(SeekBarActivity activity, int resultCode, String key, int value) {
        Intent intent = activity.getIntent();
        intent.putExtra(key, value);
        activity.setResult(resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        commit(SeekBarActivity.this, resultCode, KEY_CURRENT_VALUE, mSeekBar.getProgress() + min);
        finish();
    }
}
