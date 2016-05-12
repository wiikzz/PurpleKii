package com.wiikzz.library.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.wiikzz.library.app.ActivityStack;

import java.lang.ref.SoftReference;

/**
 * Created by wiikii on 16/4/5.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public abstract  class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getName();
    protected KiiHandler mHandler = new KiiHandler(this);

    // 创建Handler
    private static class KiiHandler extends Handler {
        private final SoftReference<BaseActivity> mInstance;

        KiiHandler(BaseActivity baseActivity) {
            mInstance = new SoftReference<>(baseActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity entityInstance = mInstance.get();
            if(entityInstance != null) {
                entityInstance.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.instance().addActivity(this);

        initVariables();
        initViews(savedInstanceState);
        loadViewData();
    }

    // 初始化变量
    protected abstract void initVariables();

    // 初始化View
    protected abstract void initViews(Bundle savedInstanceState);

    // 加载需要显示的数据
    protected abstract void loadViewData();

    // 处理Handler发来的Message
    protected abstract void handleMessage(Message message);

    // 发送Message
    protected void sendMessage(int what) {
        sendMessage(what, null);
    }
    protected void sendMessage(int what, long delayTime) {
        sendMessage(what, null, delayTime);
    }
    protected void sendMessage(int what, Bundle bundle) {
        sendMessage(what, bundle, 0L);
    }
    protected void sendMessage(int what, Bundle bundle, long delayTime) {
        Message message = new Message();
        message.what = what;
        message.setData(bundle);

        if(delayTime > 0L) {
            mHandler.sendMessageDelayed(message, delayTime);
        } else {
            mHandler.sendMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.instance().finishActivity(this);
    }
}
