package com.wiikzz.ikz.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiikzz.ikz.R;
import com.wiikzz.ikz.util.SystemUtils;

public class LoadingDialog {
    private Dialog mDialog;

    private int mDisplayWidth;
    private int mDisplayMargin;

    private CharSequence mTitleText;
    private CharSequence mMessageText;

    private boolean mCanceledOutside;

    private TextView mTitleView;
    private TextView mContentView;
    private ImageView mLoadingView;

    private View mDialogContentView;
    private RotateAnimation mAnimation;

    private OnDialogEventListener mDismissListener;
    private OnDialogEventListener mCancelListener;;

    public interface OnDialogEventListener {
        void onClick(LoadingDialog dialog);
    }

    public LoadingDialog(Context context) {
        mDialog = new Dialog(context, R.style.AppTheme_PopupDialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(mDialogContentView);

                // 设置宽度
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.width = mDisplayWidth - 2 * mDisplayMargin;
                getWindow().setAttributes(layoutParams);

                mTitleView.setText(mTitleText);
                mContentView.setText(mMessageText);

                // 开始动画
                mLoadingView.setAnimation(mAnimation);
                mAnimation.startNow();

                this.setCanceledOnTouchOutside(mCanceledOutside);
                this.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mAnimation != null) {
                            mAnimation.cancel();
                        }

                        if (mDismissListener != null) {
                            mDismissListener.onClick(LoadingDialog.this);
                        }
                    }
                });

                this.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(mCancelListener != null) {
                            mCancelListener.onClick(LoadingDialog.this);
                        }
                    }
                });
            }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(!mCanceledOutside) {
                        return true;
                    }
                }

                return super.onKeyDown(keyCode, event);
            }
        };

        mDialogContentView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        mTitleView = (TextView) mDialogContentView.findViewById(R.id.popup_title_text);
        mContentView = (TextView) mDialogContentView.findViewById(R.id.popup_content_textview);
        mLoadingView = (ImageView) mDialogContentView.findViewById(R.id.popup_content_imageview);

        // set default value
        mTitleText = context.getString(R.string.kii_string_dialog_default_title);
        mCanceledOutside = true;
        mAnimation = new RotateAnimation(0f,3600f,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(8000L);
        mAnimation.setRepeatCount(Animation.INFINITE);

        mDisplayWidth = SystemUtils.getScreenWidth(context);
        mDisplayMargin = SystemUtils.dip2px(context, 30);
    }

    public LoadingDialog setTitleText(CharSequence titleText) {
        if(!TextUtils.isEmpty(titleText)) {
            this.mTitleText = titleText;
        }

        return this;
    }

    public LoadingDialog setMessageText(CharSequence messageText) {
        if(!TextUtils.isEmpty(messageText)) {
            this.mMessageText = messageText;
        }

        return this;
    }

    public LoadingDialog setOnCanceledListener(OnDialogEventListener listener) {
        this.mDismissListener = listener;
        return this;
    }

    public LoadingDialog setOnDissmissListener(OnDialogEventListener listener) {
        this.mCancelListener = listener;
        return this;
    }

    public LoadingDialog setCanceledOutside(boolean canceledOutside) {
        this.mCanceledOutside = canceledOutside;
        return this;
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public static LoadingDialog getProgressDialog(Context context, String title, String content) {
        LoadingDialog loadingDialog = new LoadingDialog(context);
        loadingDialog.setTitleText(title);
        loadingDialog.setMessageText(content);
        return loadingDialog;
    }
}
