package com.xiaowei.arouterdemo;

import android.app.Application;

import com.xiaowei.arouter.ARouter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().init(this);
    }
}
