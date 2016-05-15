package com.wiikzz.ikz.ui.activity;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;

import com.wiikzz.ikz.R;
import com.wiikzz.library.ui.BaseActivity;
import com.wiikzz.library.util.Logger;
import com.wiikzz.library.util.SharedPrefUtil;

public class SplashActivity extends BaseActivity {
    // 兼顾开屏及跳转

    private final int MSG_GOTO_GUIDE = 0x1010;
    private final int MSG_GOTO_MAIN = 0x1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        loadViewData();
        judgeJumpTo();
    }

    @Override
    protected void initVariables() {
        // nothing to do
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void loadViewData() {
        // nothing to do
    }

    private void judgeJumpTo() {
        if(SharedPrefUtil.getValue(this, "zzz", false)) {
            sendMessage(MSG_GOTO_MAIN, 3000L);
        } else {
            sendMessage(MSG_GOTO_GUIDE, 3000L);
        }
    }

    @Override
    protected void handleMessage(Message message) {
        if(message.what == MSG_GOTO_GUIDE) {
            // TODO: 16/4/5 goto guide page
            Logger.d(TAG, "goto guide page");

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(message.what == MSG_GOTO_MAIN) {
            // TODO: 16/4/5 goto main page
            Logger.d(TAG, "goto main page");

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
