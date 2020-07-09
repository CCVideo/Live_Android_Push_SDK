package com.bokecc.sdk.mobile.push.example.base.activity;

import android.os.Bundle;

import com.bokecc.sdk.mobile.push.example.base.BasePresenter;

public abstract class BaseNoPresenterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isNeedPresenter = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BasePresenter getPresenter() {
        throw new UnsupportedOperationException();
    }
}
