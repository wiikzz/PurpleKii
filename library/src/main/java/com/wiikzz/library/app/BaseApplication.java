package com.wiikzz.library.app;

import android.app.Application;

/**
 * Created by wiikii on 16/4/5.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyUtil.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        VolleyUtil.destroy();
    }

    public void exitApp() {
        ActivityStack.instance().applicationExit(this);
    }

}
