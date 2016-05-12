package com.wiikzz.ikz.app;


import com.wiikzz.ikz.BuildConfig;
import com.wiikzz.library.app.BaseApplication;
import com.wiikzz.library.util.Logger;

/**
 * Created by wiikii on 16/4/5.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class AppApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        initConfig();
    }

    private void initConfig() {
        Logger.setDebug(BuildConfig.DEBUG);
    }
}
