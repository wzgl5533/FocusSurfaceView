package com.cymaybe.foucssurfaceview.activity;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * 作者：dell on 2018/2/8 16:20
 * 描述：
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
