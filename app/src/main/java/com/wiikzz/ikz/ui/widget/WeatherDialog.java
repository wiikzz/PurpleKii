package com.wiikzz.ikz.ui.widget;

/**
 * Created by wiikii on 16/2/4.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wiikzz.ikz.R;
import com.wiikzz.ikz.util.SystemUtils;

/**
 * Created by wiikii on 15/11/9.
 */
public class WeatherDialog implements View.OnClickListener {

    public enum WeatherDialogType {
        WEATHER_DIALOG_TYPE_NORMAL, // TextView
        WEATHER_DIALOG_TYPE_LISTV,  // ListView
        WEATHER_DIALOG_TYPE_TIMESET,// TimePicker
    }

    public enum WeatherButtonStyle {
        WEATHER_BUTTON_STYLE_GREY("#666666", R.drawable.shape_button_grey_selector),
        WEATHER_BUTTON_STYLE_BLUE("#3097fd", R.drawable.shape_button_blue_selector),
        WEATHER_BUTTON_STYLE_RED("#ef4141", R.drawable.shape_button_red_selector);

        private int color;
        private int bgResId;
        WeatherButtonStyle(String colorString, int backgroundResourceId) {
            color = Color.parseColor(colorString);
            bgResId = backgroundResourceId;
        }

        public int getColor() {
            return color;
        }

        public int getBackgroundResId() {
            return bgResId;
        }
    }

    private Dialog mDialog;

    private int mDisplayWidth;
    private int mDisplayMargin;
    private int mHeaderViewMargin;

    private CharSequence mTitleText;
    private CharSequence mContentText;
    private CharSequence mConfirmButtonText;
    private CharSequence mCancelButtonText;

    private WeatherButtonStyle mConfirmButtonStyle;
    private WeatherButtonStyle mCancelButtonStyle;

    private boolean mShowCancelButton;
    private boolean mShowCloseButton;
    private boolean mCanceledOutside;
    private boolean mShowTitleLayout;
    private int mContentGravity;
    private WeatherDialogType mContentType;
    private int mContentTextSize;

    private TextView mTitleView;
    private TextView mConfirmView;
    private TextView mCancelView;
    private View mCloseView;
    private View mDialogContentView;
    private View mPopupHeaderView;
    private View mPopupTitleView;
    private TextView mContentTextView;
    private ListView mContentListView;
    private TimePicker mContentTimePicker;

    private OnDialogClickListener mCancelListener;
    private OnDialogClickListener mConfirmListener;
    private OnDialogClickListener mDismissListener;

    public interface OnDialogClickListener {
        void onClick(WeatherDialog dialog);
    }

    public WeatherDialog(Context context) {
        mDialog = new Dialog(context, R.style.PopupDialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(mDialogContentView);

                // 设置宽度
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.width = mDisplayWidth - 2 * mDisplayMargin;
                getWindow().setAttributes(layoutParams);

                if(mContentType == WeatherDialogType.WEATHER_DIALOG_TYPE_NORMAL) {
                    mContentTextView.setText(mContentText);
                    mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                            mContentTextSize == 0 ? 15 : mContentTextSize);
                    mContentTextView.setTextColor(Color.parseColor("#666666"));
                    mContentTextView.setGravity(mContentGravity);
                    mContentTextView.setVisibility(View.VISIBLE);
                    mContentListView.setVisibility(View.GONE);
                    mContentTimePicker.setVisibility(View.GONE);
                } else if(mContentType == WeatherDialogType.WEATHER_DIALOG_TYPE_LISTV) {
                    mContentTextView.setVisibility(View.GONE);
                    mContentListView.setVisibility(View.VISIBLE);
                    mContentTimePicker.setVisibility(View.GONE);
                } else if(mContentType == WeatherDialogType.WEATHER_DIALOG_TYPE_TIMESET) {
                    mContentTextView.setVisibility(View.GONE);
                    mContentListView.setVisibility(View.GONE);
                    mContentTimePicker.setVisibility(View.VISIBLE);
                }

                // set popup header layout params
                if(mShowTitleLayout) {
                    ViewGroup.MarginLayoutParams headerLayoutParams = (ViewGroup.MarginLayoutParams) mPopupHeaderView.getLayoutParams();
                    int headerViewMarginBottom = mHeaderViewMargin;
                    if(mContentType == WeatherDialogType.WEATHER_DIALOG_TYPE_LISTV) {
                        headerViewMarginBottom = 0;
                    }

                    headerLayoutParams.setMargins(mHeaderViewMargin, 0, mHeaderViewMargin, headerViewMarginBottom);
                    mPopupTitleView.setVisibility(View.VISIBLE);
                } else {
                    ViewGroup.MarginLayoutParams headerLayoutParams = (ViewGroup.MarginLayoutParams) mPopupHeaderView.getLayoutParams();
                    headerLayoutParams.setMargins(mHeaderViewMargin, mHeaderViewMargin, mHeaderViewMargin, 0);

                    mPopupTitleView.setVisibility(View.GONE);
                }

                mTitleView.setText(mTitleText);
                mConfirmView.setText(mConfirmButtonText);
                mCancelView.setText(mCancelButtonText);

                mConfirmView.setTextColor(mConfirmButtonStyle.getColor());
                mConfirmView.setBackgroundResource(mConfirmButtonStyle.getBackgroundResId());
                mCancelView.setTextColor(mCancelButtonStyle.getColor());
                mCancelView.setBackgroundResource(mCancelButtonStyle.getBackgroundResId());

                mConfirmView.setOnClickListener(WeatherDialog.this);
                mCancelView.setOnClickListener(WeatherDialog.this);
                mCloseView.setOnClickListener(WeatherDialog.this);

                mCancelView.setVisibility(mShowCancelButton ? View.VISIBLE : View.GONE);
                mCloseView.setVisibility(mShowCloseButton ? View.VISIBLE : View.GONE);

                this.setCanceledOnTouchOutside(mCanceledOutside);
                this.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mDismissListener != null) {
                            mDismissListener.onClick(WeatherDialog.this);
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

        mDialogContentView = LayoutInflater.from(context).inflate(R.layout.dialog_weather, null);
        mPopupHeaderView = mDialogContentView.findViewById(R.id.popup_header_layout);
        mPopupTitleView = mDialogContentView.findViewById(R.id.popup_title_layout);
        mTitleView = (TextView) mDialogContentView.findViewById(R.id.popup_title_text);
        mConfirmView = (TextView) mDialogContentView.findViewById(R.id.popup_button_confirm);
        mCancelView = (TextView) mDialogContentView.findViewById(R.id.popup_button_cancel);
        mCloseView = mDialogContentView.findViewById(R.id.popup_title_close);

        mContentTextView = (TextView) mDialogContentView.findViewById(R.id.popup_content_textview);
        mContentListView = (ListView) mDialogContentView.findViewById(R.id.popup_content_listview);
        mContentTimePicker = (TimePicker) mDialogContentView.findViewById(R.id.popup_content_timepicker);

        // set default value
        mTitleText = context.getString(R.string.kii_string_dialog_default_title);
        mContentText = context.getString(R.string.kii_string_dialog_loading);
        mConfirmButtonText = context.getString(R.string.kii_string_dialog_confirm);
        mCancelButtonText = context.getString(R.string.kii_string_dialog_cancel);
        mConfirmButtonStyle = WeatherButtonStyle.WEATHER_BUTTON_STYLE_BLUE;
        mCancelButtonStyle = WeatherButtonStyle.WEATHER_BUTTON_STYLE_GREY;
        mShowCancelButton = false;
        mShowCloseButton = true;
        mCanceledOutside = true;
        mShowTitleLayout = true;
        mContentGravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        mContentType = WeatherDialogType.WEATHER_DIALOG_TYPE_NORMAL;

        mDisplayWidth = SystemUtils.getScreenWidth(context);
        mDisplayMargin = SystemUtils.dip2px(context, 30);
        mHeaderViewMargin = SystemUtils.dip2px(context, 15);
    }

    public WeatherDialog setTitleText(CharSequence titleText) {
        if(!TextUtils.isEmpty(titleText)) {
            this.mTitleText = titleText;
        }

        return this;
    }

    public WeatherDialog setContentText(CharSequence contentText) {
        return setContentText(contentText, 15);
    }

    public WeatherDialog setContentText(CharSequence contentText, int textSize) {
        if(!TextUtils.isEmpty(contentText)) {
            this.mContentText = contentText;
            this.mContentTextSize = textSize;
        }

        return this;
    }

    public WeatherDialog setContentType(WeatherDialogType type) {
        this.mContentType = type;
        return this;
    }

    public WeatherDialog setContentGravity(int gravity) {
        this.mContentGravity = gravity;
        return this;
    }

    public WeatherDialog setConfirmButtonText(CharSequence confirmText) {
        return setConfirmButtonText(confirmText, WeatherButtonStyle.WEATHER_BUTTON_STYLE_BLUE);
    }

    public WeatherDialog setConfirmButtonText(CharSequence confirmText, WeatherButtonStyle style) {
        if(!TextUtils.isEmpty(confirmText)) {
            this.mConfirmButtonText = confirmText;
        }

        this.mConfirmButtonStyle = style;
        return this;
    }

    public WeatherDialog setCancelButtonText(CharSequence cancelText) {
        return setCancelButtonText(cancelText, WeatherButtonStyle.WEATHER_BUTTON_STYLE_GREY);
    }

    public WeatherDialog setCancelButtonText(CharSequence cancelText, WeatherButtonStyle style) {
        if(!TextUtils.isEmpty(cancelText)) {
            this.mCancelButtonText = cancelText;
            this.mShowCancelButton = true;
        }

        this.mCancelButtonStyle = style;
        return this;
    }

    public WeatherDialog setConfirmButtonStyle(WeatherButtonStyle style) {
        this.mConfirmButtonStyle = style;
        return this;
    }

    public WeatherDialog setCancelButtonStyle(WeatherButtonStyle style) {
        this.mCancelButtonStyle = style;
        return this;
    }

    public WeatherDialog setShowCancelButton(boolean show) {
        this.mShowCancelButton = show;
        return this;
    }

    public WeatherDialog setShowCloseButton(boolean show) {
        this.mShowCloseButton = show;
        return this;
    }

    public WeatherDialog setShowHeaderView(boolean show) {
        this.mShowTitleLayout = show;
        return this;
    }

    public WeatherDialog setOnConfirmListener(OnDialogClickListener listener) {
        this.mConfirmListener = listener;
        return this;
    }

    public WeatherDialog setOnCancelListener(OnDialogClickListener listener) {
        this.mCancelListener = listener;
        return this;
    }

    public WeatherDialog setOnDismissListener(OnDialogClickListener listener) {
        this.mDismissListener = listener;
        return this;
    }

    public WeatherDialog setCanceledOutside(boolean canceledOutside) {
        this.mCanceledOutside = canceledOutside;
        return this;
    }

    public TextView getContentTextView() {
        return mContentTextView;
    }

    public ListView getContentListView() {
        return mContentListView;
    }

    public TimePicker getContentTimePicker() {
        return mContentTimePicker;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.popup_button_confirm) {
            mDialog.dismiss();
            if(mConfirmListener != null) {
                mConfirmListener.onClick(this);
            }
        } else if(v.getId() == R.id.popup_button_cancel) {
            mDialog.cancel();
            if(mCancelListener != null) {
                mCancelListener.onClick(this);
            }
        } else if(v.getId() == R.id.popup_title_close) {
            mDialog.cancel();
            if(mCancelListener != null) {
                mCancelListener.onClick(this);
            }
        }
    }

    public void show() {
        mDialog.show();
    }

    public static WeatherDialog getListDialog(Context context, CharSequence title, CharSequence contentText,
                                          CharSequence confirmString, CharSequence cancelString,
                                          OnDialogClickListener confirmListener) {
        WeatherDialog weatherDialog = new WeatherDialog(context).setTitleText(title)
                .setContentText(contentText)
                .setContentType(WeatherDialogType.WEATHER_DIALOG_TYPE_LISTV)
                .setConfirmButtonText(confirmString)
                .setCancelButtonText(cancelString)
                .setOnConfirmListener(confirmListener);
        return weatherDialog;
    }

    public static WeatherDialog getTimePickerDialog(Context context, CharSequence title, CharSequence contentText,
                                          CharSequence confirmString, CharSequence cancelString,
                                          OnDialogClickListener confirmListener) {
        WeatherDialog weatherDialog = new WeatherDialog(context).setTitleText(title)
                .setContentText(contentText)
                .setContentType(WeatherDialogType.WEATHER_DIALOG_TYPE_TIMESET)
                .setConfirmButtonText(confirmString)
                .setCancelButtonText(cancelString)
                .setOnConfirmListener(confirmListener);
        return weatherDialog;
    }

    public static WeatherDialog getNormalDialog(Context context, CharSequence title, CharSequence contentText,
                                            CharSequence confirmString, CharSequence cancelString,
                                            OnDialogClickListener confirmListener) {
        return getNormalDialog(context, title, contentText, confirmString, cancelString, confirmListener, null, null);
    }

    public static WeatherDialog getNormalDialog(Context context, CharSequence title, CharSequence contentText,
                                            CharSequence confirmString, CharSequence cancelString,
                                            OnDialogClickListener confirmListener, OnDialogClickListener cancelListener) {
        return getNormalDialog(context, title, contentText, confirmString, cancelString, confirmListener, cancelListener, null);
    }

    public static WeatherDialog getNormalDialog(Context context, CharSequence title, CharSequence contentText,
                                  CharSequence confirmString, CharSequence cancelString,
                                  OnDialogClickListener confirmListener, OnDialogClickListener cancelListener,
                                  OnDialogClickListener dismissListener) {
        WeatherDialog weatherDialog = new WeatherDialog(context).setTitleText(title)
                .setContentText(contentText)
                .setContentType(WeatherDialogType.WEATHER_DIALOG_TYPE_NORMAL)
                .setConfirmButtonText(confirmString)
                .setCancelButtonText(cancelString)
                .setOnConfirmListener(confirmListener)
                .setOnCancelListener(cancelListener)
                .setOnDismissListener(dismissListener);
        return weatherDialog;
    }
}