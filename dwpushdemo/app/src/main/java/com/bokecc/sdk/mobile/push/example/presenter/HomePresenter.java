package com.bokecc.sdk.mobile.push.example.presenter;

import com.bokecc.sdk.mobile.push.example.contract.HomeContract;

/**
 * 作者 ${郭鹏飞}.<br/>
 */
public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View mHomeView;

    public HomePresenter(HomeContract.View homeView) {
        mHomeView = homeView;
    }

    public void exit() {
        mHomeView.exit();
    }

}
