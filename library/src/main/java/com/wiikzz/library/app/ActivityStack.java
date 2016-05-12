package com.wiikzz.library.app;

import android.content.Context;

import com.wiikzz.library.ui.BaseActivity;
import com.wiikzz.library.util.Logger;

import java.util.Stack;

/**
 * Created by wiikii on 15/9/28.
 */
public final class ActivityStack {
    private static boolean sUseStackMode = false;
    private static Stack<BaseActivity> sActivityStack;
    private static final ActivityStack __INSTANCE = new ActivityStack();

    private ActivityStack() {}

    public static ActivityStack instance() {
        return __INSTANCE;
    }

    public static void initActivityStack(boolean useStackMode) {
        sUseStackMode = useStackMode;
    }

    /**
     * 获取当前Activity栈中元素个数
     */
    public int getCount() {
        if(sActivityStack == null) {
            return 0;
        } else {
            return sActivityStack.size();
        }
    }

    /**
     * 添加Activity到栈
     */
    public void addActivity(BaseActivity activity) {
        if(!sUseStackMode) {
            return;
        }

        if (sActivityStack == null) {
            sActivityStack = new Stack<BaseActivity>();
        }

        sActivityStack.add(activity);
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    public BaseActivity topActivity() {
        if(!sUseStackMode) {
            return null;
        }

        if (sActivityStack == null) {
            throw new NullPointerException(
                    "Activity stack is Null,your Activity must extend BaseActivity");
        }

        if (sActivityStack.isEmpty()) {
            return null;
        }

        return sActivityStack.lastElement();
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    public BaseActivity findActivity(Class<?> cls) {
        if(!sUseStackMode) {
            return null;
        }

        if(sActivityStack == null || sActivityStack.isEmpty()) {
            return null;
        }

        BaseActivity activity = null;
        for (BaseActivity aty : sActivityStack) {
            if (aty.getClass().equals(cls)) {
                activity = aty;
                break;
            }
        }
        return activity;
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    public void finishActivity() {
        if(!sUseStackMode) {
            return;
        }

        if(sActivityStack == null || sActivityStack.isEmpty()) {
            return;
        }

        BaseActivity activity = sActivityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(BaseActivity activity) {
        if(!sUseStackMode) {
            return;
        }

        if (activity != null) {
            sActivityStack.remove(activity);
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Class<?> cls) {
        if(!sUseStackMode) {
            return;
        }

        if(sActivityStack == null || sActivityStack.isEmpty()) {
            return;
        }

        for (BaseActivity activity : sActivityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    public void finishOthersActivity(Class<?> cls) {
        if(!sUseStackMode) {
            return;
        }

        if(sActivityStack == null || sActivityStack.isEmpty()) {
            return;
        }

        for (BaseActivity activity : sActivityStack) {
            if (!(activity.getClass().equals(cls))) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        if(!sUseStackMode) {
            return;
        }

        if(sActivityStack == null || sActivityStack.isEmpty()) {
            return;
        }

        for (int i = 0, size = sActivityStack.size(); i < size; i++) {
            if (null != sActivityStack.get(i)) {
                sActivityStack.get(i).finish();
            }
        }

        sActivityStack.clear();
    }

    public void applicationExit(Context context) {
        if(!sUseStackMode) {
            Logger.e("ActivityStack", "useStackMode is closed! Turn it on!");
            return;
        }

        try {
            finishAllActivity();
            Runtime.getRuntime().exit(0);
        } catch (Exception e) {
            Runtime.getRuntime().exit(-1);
        }
    }
}
