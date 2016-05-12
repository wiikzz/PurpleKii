package com.wiikzz.library.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import java.lang.ref.SoftReference;

/**
 * Created by wiikii on 16/4/5.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public abstract class BaseFragment extends Fragment {
    protected final String TAG = getClass().getName();
    protected KiiHandler mHandler = new KiiHandler(this);

    // 创建Handler
    private static class KiiHandler extends Handler {
        private final SoftReference<BaseFragment> mInstance;

        KiiHandler(BaseFragment baseFragment) {
            mInstance = new SoftReference<>(baseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseFragment entityInstance = mInstance.get();
            if(entityInstance != null) {
                entityInstance.handleMessage(msg);
            }
        }
    }

    // 处理Handler发来的Message
    protected abstract void handleMessage(Message message);

    // 发送Message
    protected void sendMessage(int what) {
        sendMessage(what, null);
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

}
