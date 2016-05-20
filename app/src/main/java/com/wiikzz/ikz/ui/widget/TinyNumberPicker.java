package com.wiikzz.ikz.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wiikzz.ikz.R;

import java.util.Locale;

/**
 * Created by wiikii on 16/5/18.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class TinyNumberPicker extends LinearLayout {

    //The coefficient by which to adjust (divide) the max fling velocity.
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    //The number of items show in the selector wheel.
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    //The the duration for adjusting the selector wheel.
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    //Constant for unspecified size.
    private static final int SIZE_UNSPECIFIED = -1;
    //The default unscaled height of the selection divider.
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    //The default unscaled distance between the selection dividers.
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    //The index of the middle selector item.
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = SELECTOR_WHEEL_ITEM_COUNT / 2;
    //The duration of scrolling while snapping to a given position.
    private static final int SNAP_SCROLL_DURATION = 300;

    private final int mTextSize;

    private int mMinValue;
    private int mMaxValue;
    private int mValue; //Current value of this TinyNumberPicker

    //The min height of this widget.
    private final int mMinHeight;
    //The max height of this widget.
    private final int mMaxHeight;
    //The max width of this widget.
    private final int mMinWidth;
    //The max width of this widget.
    private int mMaxWidth;

    private OnValueChangeListener mOnValueChangeListener;
    private OnScrollListener mOnScrollListener;

    private final TextView mInputText;

    private int mTouchSlop;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    private final int[] mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
    private final Paint mSelectorWheelPaint;

    //Divider for showing item to be selected while scrolling
    private final Drawable mSelectionDivider;
    //The height of the selection divider.
    private final int mSelectionDividerHeight;
    //The distance between the two selection dividers.
    private final int mSelectionDividersDistance;
    //Flag whether to compute the max width.
    private final boolean mComputeMaxWidth;
    //The height of the gap between text elements if the selector wheel.
    private int mSelectorTextGapHeight;
    //The top of the top selection divider.
    private int mTopSelectionDividerTop;
    //The bottom of the bottom selection divider.
    private int mBottomSelectionDividerBottom;

    //The height of a selector element (text + gap).
    private int mSelectorElementHeight;
    //The initial offset of the scroll selector.
    private int mInitialScrollOffset = Integer.MIN_VALUE;
    //The current offset of the scroll selector.
    private int mCurrentScrollOffset;
    //The {@link Scroller} responsible for flinging the selector.
    private final TinyScroller mFlingScroller;
    //The {@link Scroller} responsible for adjusting the selector.
    private final TinyScroller mAdjustScroller;
    //The previous Y coordinate while scrolling the selector.
    private int mPreviousScrollerY;
    //The Y position of the last down event.
    private float mLastDownEventY;
    //The time of the last down event.
    private long mLastDownEventTime;
    //The Y position of the last down or move event.
    private float mLastDownOrMoveEventY;
    //Determines speed during touch scrolling.
    private VelocityTracker mVelocityTracker;
    //The current scroll state of the number picker.
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;


    //Flag whether to perform a click on tap.
    private boolean mPerformClickOnTap;
    //Flag whether the selector should wrap around.
    private boolean mWrapSelectorWheel;
    //User choice on whether the selector wheel should be wrapped.
    private boolean mWrapSelectorWheelPreferred = true;

    //Cache for the string representation of selector indices.
    private final SparseArray<String> mSelectorIndexToStringCache = new SparseArray<>();

    //The values to be displayed instead the indices.
    private String[] mDisplayedValues;

    public interface OnValueChangeListener {
        void onValueChange(TinyNumberPicker picker, int oldVal, int newVal);
    }

    public interface OnScrollListener {
        //The view is not scrolling.
        public static int SCROLL_STATE_IDLE = 0;

        //The user is scrolling using touch, and his finger is still on the screen.
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        //The user had previously been scrolling using touch and performed a fling.
        public static int SCROLL_STATE_FLING = 2;

        //Callback invoked while the number picker scroll state has changed.
        public void onScrollStateChange(TinyNumberPicker view, int scrollState);
    }

    public TinyNumberPicker(Context context) {
        this(context, null);
    }

    public TinyNumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tinyNumberPickerStyle);
    }

    public TinyNumberPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        // process style attributes
        TypedArray attributesArray = context.obtainStyledAttributes(
                attrs, R.styleable.tinyNumberPickerAttrs, defStyle, 0);

        final int defSelectionDividerHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT,
                getResources().getDisplayMetrics());

        final Drawable selectionDivider = attributesArray.getDrawable(
                R.styleable.tinyNumberPickerAttrs_selectionDivider);
        if (selectionDivider != null) {
            selectionDivider.setCallback(this);
            if (selectionDivider.isStateful()) {
                selectionDivider.setState(getDrawableState());
            }
        }
        mSelectionDivider = selectionDivider;

        mSelectionDividerHeight = attributesArray.getDimensionPixelSize(
                R.styleable.tinyNumberPickerAttrs_selectionDividerHeight, defSelectionDividerHeight);

        final int defSelectionDividerDistance = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE,
                getResources().getDisplayMetrics());
        mSelectionDividersDistance = attributesArray.getDimensionPixelSize(
                R.styleable.tinyNumberPickerAttrs_selectionDividersDistance, defSelectionDividerDistance);

        mMinHeight = attributesArray.getDimensionPixelSize(
                R.styleable.tinyNumberPickerAttrs_internalMinHeight, SIZE_UNSPECIFIED);

        mMaxHeight = attributesArray.getDimensionPixelSize(
                R.styleable.tinyNumberPickerAttrs_internalMaxHeight, SIZE_UNSPECIFIED);
        if (mMinHeight != SIZE_UNSPECIFIED && mMaxHeight != SIZE_UNSPECIFIED
                && mMinHeight > mMaxHeight) {
            throw new IllegalArgumentException("minHeight > maxHeight");
        }

        mMinWidth = attributesArray.getDimensionPixelSize(
                R.styleable.tinyNumberPickerAttrs_internalMinWidth, SIZE_UNSPECIFIED);

        mMaxWidth = attributesArray.getDimensionPixelSize(
                R.styleable.tinyNumberPickerAttrs_internalMaxWidth, SIZE_UNSPECIFIED);
        if (mMinWidth != SIZE_UNSPECIFIED && mMaxWidth != SIZE_UNSPECIFIED
                && mMinWidth > mMaxWidth) {
            throw new IllegalArgumentException("minWidth > maxWidth");
        }

        mComputeMaxWidth = (mMaxWidth == SIZE_UNSPECIFIED);

        attributesArray.recycle();

        // By default LinearLayout that we extend is not drawn. This is
        // its draw() method is not called but dispatchDraw() is called
        // directly (see ViewGroup.drawChild()). However, this class uses
        // the fading edge effect implemented by View and we need our
        // draw() method to be called. Therefore, we declare we will draw.
        setWillNotDraw(false);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tiny_number_picker, this, true);

        // input text
        mInputText = (TextView) findViewById(R.id.tiny_number_picker_input);

        // initialize constants
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity()
                / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
        mTextSize = (int) mInputText.getTextSize();

        // create the selector wheel paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mTextSize);
        paint.setTypeface(mInputText.getTypeface());
        ColorStateList colors = mInputText.getTextColors();
        int color = colors.getColorForState(ENABLED_STATE_SET, Color.WHITE);
        paint.setColor(color);
        mSelectorWheelPaint = paint;

        // create the fling and adjust scroller
        mFlingScroller = new TinyScroller(getContext(), null, true);
        mAdjustScroller = new TinyScroller(getContext(), new DecelerateInterpolator(2.5f));

        updateInputTextView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // If not explicitly specified this view is important for accessibility.
            if (getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
                setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int msrdWdth = getMeasuredWidth();
        final int msrdHght = getMeasuredHeight();

        // Input text centered horizontally.
        final int inptTxtMsrdWdth = mInputText.getMeasuredWidth();
        final int inptTxtMsrdHght = mInputText.getMeasuredHeight();
        final int inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / 2;
        final int inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2;
        final int inptTxtRight = inptTxtLeft + inptTxtMsrdWdth;
        final int inptTxtBottom = inptTxtTop + inptTxtMsrdHght;
        mInputText.layout(inptTxtLeft, inptTxtTop, inptTxtRight, inptTxtBottom);

        if (changed) {
            // need to do all this when we know our size
            initializeSelectorWheel();
            initializeFadingEdges();
            mTopSelectionDividerTop = (getHeight() - mSelectionDividersDistance) / 2
                    - mSelectionDividerHeight;
            mBottomSelectionDividerBottom = mTopSelectionDividerTop + 2 * mSelectionDividerHeight
                    + mSelectionDividersDistance;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try greedily to fit the max width and height.
        final int newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec, mMaxWidth);
        final int newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec, mMaxHeight);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
        // Flag if we are measured with width or height less than the respective min.
        final int widthSize = resolveSizeAndStateRespectingMinSize(mMinWidth, getMeasuredWidth(),
                widthMeasureSpec);
        final int heightSize = resolveSizeAndStateRespectingMinSize(mMinHeight, getMeasuredHeight(),
                heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == SIZE_UNSPECIFIED) {
            return measureSpec;
        }
        final int size = MeasureSpec.getSize(measureSpec);
        final int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return measureSpec;
            case MeasureSpec.AT_MOST:
                return MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), MeasureSpec.EXACTLY);
            case MeasureSpec.UNSPECIFIED:
                return MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.EXACTLY);
            default:
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
        }
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a MeasureSpec. Tries to respect the min size, unless a different size
     * is imposed by the constraints.
     *
     * @param minSize The minimal desired size.
     * @param measuredSize The currently measured size.
     * @param measureSpec The current measure spec.
     * @return The resolved size and state.
     */
    private int resolveSizeAndStateRespectingMinSize(
            int minSize, int measuredSize, int measureSpec) {
        if (minSize != SIZE_UNSPECIFIED) {
            final int desiredWidth = Math.max(minSize, measuredSize);
            return resolveSizeAndState(desiredWidth, measureSpec, 0);
        } else {
            return measuredSize;
        }
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        final int result;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                result = size;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x = (getRight() - getLeft()) / 2;
        float y = mCurrentScrollOffset;

        // draw the selector wheel
        int[] selectorIndices = mSelectorIndices;
        for (int i = 0; i < selectorIndices.length; i++) {
            int selectorIndex = selectorIndices[i];
            String scrollSelectorValue = mSelectorIndexToStringCache.get(selectorIndex);
            // Do not draw the middle item if input is visible since the input
            // is shown only if the wheel is static and it covers the middle
            // item. Otherwise, if the user starts editing the text via the
            // IME he may see a dimmed version of the old value intermixed
            // with the new one.
            if ((i != SELECTOR_MIDDLE_ITEM_INDEX) || (mInputText.getVisibility() != VISIBLE)) {
                canvas.drawText(scrollSelectorValue, x, y, mSelectorWheelPaint);
            }
            y += mSelectorElementHeight;
        }

        // draw the selection dividers
        if (mSelectionDivider != null) {
            // draw the top divider
            int topOfTopDivider = mTopSelectionDividerTop;
            int bottomOfTopDivider = topOfTopDivider + mSelectionDividerHeight;
            mSelectionDivider.setBounds(0, topOfTopDivider, getRight(), bottomOfTopDivider);
            mSelectionDivider.draw(canvas);

            // draw the bottom divider
            int bottomOfBottomDivider = mBottomSelectionDividerBottom;
            int topOfBottomDivider = bottomOfBottomDivider - mSelectionDividerHeight;
            mSelectionDivider.setBounds(0, topOfBottomDivider, getRight(), bottomOfBottomDivider);
            mSelectionDivider.draw(canvas);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInputText.setVisibility(View.INVISIBLE);
                mLastDownOrMoveEventY = mLastDownEventY = event.getY();
                mLastDownEventTime = event.getEventTime();
                mPerformClickOnTap = false;
                // Make sure we support flinging inside scrollables.
                getParent().requestDisallowInterceptTouchEvent(true);
                if (!mFlingScroller.isFinished()) {
                    mFlingScroller.forceFinished(true);
                    mAdjustScroller.forceFinished(true);
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                } else if (!mAdjustScroller.isFinished()) {
                    mFlingScroller.forceFinished(true);
                    mAdjustScroller.forceFinished(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                float currentMoveY = event.getY();
                if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    int deltaDownY = (int) Math.abs(currentMoveY - mLastDownEventY);
                    if (deltaDownY > mTouchSlop) {
                        onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                    }
                } else {
                    int deltaMoveY = (int) ((currentMoveY - mLastDownOrMoveEventY));
                    scrollBy(0, deltaMoveY);
                    invalidate();
                }
                mLastDownOrMoveEventY = currentMoveY;
            } break;
            case MotionEvent.ACTION_UP: {
                VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                    fling(initialVelocity);
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
                } else {
                    int eventY = (int) event.getY();
                    int deltaMoveY = (int) Math.abs(eventY - mLastDownEventY);
                    long deltaTime = event.getEventTime() - mLastDownEventTime;
                    if (deltaMoveY <= mTouchSlop && deltaTime < ViewConfiguration.getTapTimeout()) {
                        if (mPerformClickOnTap) {
                            mPerformClickOnTap = false;
                            performClick();
                        } else {
                            int selectorIndexOffset = (eventY / mSelectorElementHeight)
                                    - SELECTOR_MIDDLE_ITEM_INDEX;
                            if (selectorIndexOffset > 0) {
                                changeValueByOne(true);
                            } else if (selectorIndexOffset < 0) {
                                changeValueByOne(false);
                            }
                        }
                    } else {
                        ensureScrollWheelAdjusted();
                    }
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            } break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        TinyScroller scroller = mFlingScroller;
        if (scroller.isFinished()) {
            scroller = mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currentScrollerY = scroller.getCurrY();
        if (mPreviousScrollerY == 0) {
            mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currentScrollerY - mPreviousScrollerY);
        mPreviousScrollerY = currentScrollerY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mInputText.setEnabled(enabled);
    }

    @Override
    public void scrollBy(int x, int y) {
        int[] selectorIndices = mSelectorIndices;
        if (!mWrapSelectorWheel && y > 0
                && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= mMinValue) {
            mCurrentScrollOffset = mInitialScrollOffset;
            return;
        }
        if (!mWrapSelectorWheel && y < 0
                && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= mMaxValue) {
            mCurrentScrollOffset = mInitialScrollOffset;
            return;
        }
        mCurrentScrollOffset += y;
        while (mCurrentScrollOffset - mInitialScrollOffset > mSelectorTextGapHeight) {
            mCurrentScrollOffset -= mSelectorElementHeight;
            decrementSelectorIndices(selectorIndices);
            setValueInternal(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX], true);
            if (!mWrapSelectorWheel && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] <= mMinValue) {
                mCurrentScrollOffset = mInitialScrollOffset;
            }
        }
        while (mCurrentScrollOffset - mInitialScrollOffset < -mSelectorTextGapHeight) {
            mCurrentScrollOffset += mSelectorElementHeight;
            incrementSelectorIndices(selectorIndices);
            setValueInternal(selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX], true);
            if (!mWrapSelectorWheel && selectorIndices[SELECTOR_MIDDLE_ITEM_INDEX] >= mMaxValue) {
                mCurrentScrollOffset = mInitialScrollOffset;
            }
        }
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return mCurrentScrollOffset;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return (mMaxValue - mMinValue + 1) * mSelectorElementHeight;
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    /**
     * Flings the selector with the given <code>velocityY</code>.
     */
    private void fling(int velocityY) {
        mPreviousScrollerY = 0;

        if (velocityY > 0) {
            mFlingScroller.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        }

        invalidate();
    }

    /**
     * Increments the <code>selectorIndices</code> whose string representations
     * will be displayed in the selector.
     */
    private void incrementSelectorIndices(int[] selectorIndices) {
        for (int i = 0; i < selectorIndices.length - 1; i++) {
            selectorIndices[i] = selectorIndices[i + 1];
        }
        int nextScrollSelectorIndex = selectorIndices[selectorIndices.length - 2] + 1;
        if (mWrapSelectorWheel && nextScrollSelectorIndex > mMaxValue) {
            nextScrollSelectorIndex = mMinValue;
        }
        selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    /**
     * Decrements the <code>selectorIndices</code> whose string representations
     * will be displayed in the selector.
     */
    private void decrementSelectorIndices(int[] selectorIndices) {
        for (int i = selectorIndices.length - 1; i > 0; i--) {
            selectorIndices[i] = selectorIndices[i - 1];
        }
        int nextScrollSelectorIndex = selectorIndices[1] - 1;
        if (mWrapSelectorWheel && nextScrollSelectorIndex < mMinValue) {
            nextScrollSelectorIndex = mMaxValue;
        }
        selectorIndices[0] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    /**
     * Callback invoked upon completion of a given <code>scroller</code>.
     */
    private void onScrollerFinished(TinyScroller scroller) {
        if (scroller == mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
        } else {
            if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                updateInputTextView();
            }
        }
    }

    /**
     * Handles transition to a given <code>scrollState</code>
     */
    private void onScrollStateChange(int scrollState) {
        if (mScrollState == scrollState) {
            return;
        }
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    /**
     * Sets the listener to be notified on change of the current value.
     *
     * @param onValueChangedListener The listener.
     */
    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        mOnValueChangeListener = onValueChangedListener;
    }

    /**
     * Set listener to be notified for scroll state changes.
     *
     * @param onScrollListener The listener.
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = mSelectorIndices;
        int totalTextHeight = selectorIndices.length * mTextSize;
        float totalTextGapHeight = (getBottom() - getTop()) - totalTextHeight;
        float textGapCount = selectorIndices.length;
        mSelectorTextGapHeight = (int) (totalTextGapHeight / textGapCount + 0.5f);
        mSelectorElementHeight = mTextSize + mSelectorTextGapHeight;
        // Ensure that the middle item is positioned the same as the text in
        // mInputText
        int editTextTextPosition = mInputText.getBaseline() + mInputText.getTop();
        mInitialScrollOffset = editTextTextPosition
                - (mSelectorElementHeight * SELECTOR_MIDDLE_ITEM_INDEX);
        mCurrentScrollOffset = mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength((getBottom() - getTop() - mTextSize) / 2);
    }

    /**
     * Resets the selector indices and clear the cached string representation of
     * these indices.
     */
    private void initializeSelectorWheelIndices() {
        mSelectorIndexToStringCache.clear();
        int[] selectorIndices = mSelectorIndices;
        int current = getValue();
        for (int i = 0; i < mSelectorIndices.length; i++) {
            int selectorIndex = current + (i - SELECTOR_MIDDLE_ITEM_INDEX);
            if (mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    /**
     * Ensures we have a cached string representation of the given <code>
     * selectorIndex</code> to avoid multiple instantiations of the same string.
     */
    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        SparseArray<String> cache = mSelectorIndexToStringCache;
        String scrollSelectorValue = cache.get(selectorIndex);
        if (scrollSelectorValue != null) {
            return;
        }
        if (selectorIndex < mMinValue || selectorIndex > mMaxValue) {
            scrollSelectorValue = "";
        } else {
            if (mDisplayedValues != null) {
                int displayedValueIndex = selectorIndex - mMinValue;
                scrollSelectorValue = mDisplayedValues[displayedValueIndex];
            } else {
                scrollSelectorValue = formatNumberWithLocale(selectorIndex);
            }
        }
        cache.put(selectorIndex, scrollSelectorValue);
    }

    /**
     * Computes the max width if no such specified as an attribute.
     */
    private void tryComputeMaxWidth() {
        if (!mComputeMaxWidth) {
            return;
        }

        int maxTextWidth = 0;
        if (mDisplayedValues == null) {
            float maxDigitWidth = 0;
            for (int i = 0; i <= 9; i++) {
                final float digitWidth = mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                if (digitWidth > maxDigitWidth) {
                    maxDigitWidth = digitWidth;
                }
            }
            int numberOfDigits = 0;
            int current = mMaxValue;
            while (current > 0) {
                numberOfDigits++;
                current = current / 10;
            }
            maxTextWidth = (int) (numberOfDigits * maxDigitWidth);
        } else {
            final int valueCount = mDisplayedValues.length;
            for (int i = 0; i < valueCount; i++) {
                final float textWidth = mSelectorWheelPaint.measureText(mDisplayedValues[i]);
                if (textWidth > maxTextWidth) {
                    maxTextWidth = (int) textWidth;
                }
            }
        }
        maxTextWidth += mInputText.getPaddingLeft() + mInputText.getPaddingRight();
        if (mMaxWidth != maxTextWidth) {
            if (maxTextWidth > mMinWidth) {
                mMaxWidth = maxTextWidth;
            } else {
                mMaxWidth = mMinWidth;
            }
            invalidate();
        }
    }

    public String[] getDisplayedValues() {
        return mDisplayedValues;
    }

    public void setDisplayedValues(String[] displayedValues) {
        if (mDisplayedValues == displayedValues) {
            return;
        }
        mDisplayedValues = displayedValues;
        if (mDisplayedValues != null) {
            // Allow text entry rather than strictly numeric entry.
            mInputText.setRawInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        } else {
            mInputText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        }
        updateInputTextView();
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    private boolean updateInputTextView() {
        String text = (mDisplayedValues == null) ? formatNumberWithLocale(mValue)
                : mDisplayedValues[mValue - mMinValue];
        if (!TextUtils.isEmpty(text) && !text.equals(mInputText.getText().toString())) {
            mInputText.setText(text);
            return true;
        }

        return false;
    }

    public int getSelectedPos(String value) {
        if (mDisplayedValues == null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Ignore as if it's not a number we don't care
            }
        } else {
            for (int i = 0; i < mDisplayedValues.length; i++) {
                // Don't force the user to type in jan when ja will do
                value = value.toLowerCase();
                if (mDisplayedValues[i].toLowerCase().startsWith(value)) {
                    return mMinValue + i;
                }
            }

            /*
             * The user might have typed in a number into the month field i.e.
             * 10 instead of OCT so support that too.
             */
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {

                // Ignore as if it's not a number we don't care
            }
        }
        return mMinValue;
    }

    private boolean moveToFinalScrollerPosition(TinyScroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        int futureScrollOffset = (mCurrentScrollOffset + amountToScroll) % mSelectorElementHeight;
        int overshootAdjustment = mInitialScrollOffset - futureScrollOffset;
        if (overshootAdjustment != 0) {
            if (Math.abs(overshootAdjustment) > mSelectorElementHeight / 2) {
                if (overshootAdjustment > 0) {
                    overshootAdjustment -= mSelectorElementHeight;
                } else {
                    overshootAdjustment += mSelectorElementHeight;
                }
            }
            amountToScroll += overshootAdjustment;
            scrollBy(0, amountToScroll);
            return true;
        }
        return false;
    }

    private void changeValueByOne(boolean increment) {
        mInputText.setVisibility(View.INVISIBLE);
        if (!moveToFinalScrollerPosition(mFlingScroller)) {
            moveToFinalScrollerPosition(mAdjustScroller);
        }
        mPreviousScrollerY = 0;
        if (increment) {
            mFlingScroller.startScroll(0, 0, 0, -mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            mFlingScroller.startScroll(0, 0, 0, mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    /**
     * Notifies the listener, if registered, of a change of the value of this
     * TinyNumberPicker.
     */
    private void notifyChange(int previous, int current) {
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener.onValueChange(this, previous, mValue);
        }
    }

    /**
     * @return The wrapped index <code>selectorIndex</code> value.
     */
    private int getWrappedSelectorIndex(int selectorIndex) {
        if (selectorIndex > mMaxValue) {
            return mMinValue + (selectorIndex - mMaxValue) % (mMaxValue - mMinValue) - 1;
        } else if (selectorIndex < mMinValue) {
            return mMaxValue - (mMinValue - selectorIndex) % (mMaxValue - mMinValue) + 1;
        }
        return selectorIndex;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        mWrapSelectorWheelPreferred = wrapSelectorWheel;
        updateWrapSelectorWheel();

    }

    private void updateWrapSelectorWheel() {
        final boolean wrappingAllowed = (mMaxValue - mMinValue) >= mSelectorIndices.length;
        mWrapSelectorWheel = wrappingAllowed && mWrapSelectorWheelPreferred;
    }

    public void setValue(int value) {
        setValueInternal(value, false);
    }

    /**
     * Sets the current value of this NumberPicker.
     *
     * @param current The new value of the NumberPicker.
     * @param notifyChange Whether to notify if the current value changed.
     */
    private void setValueInternal(int current, boolean notifyChange) {
        if (mValue == current) {
            return;
        }
        // Wrap around the values if we go past the start or end
        if (mWrapSelectorWheel) {
            current = getWrappedSelectorIndex(current);
        } else {
            current = Math.max(current, mMinValue);
            current = Math.min(current, mMaxValue);
        }
        int previous = mValue;
        mValue = current;
        updateInputTextView();
        if (notifyChange) {
            notifyChange(previous, current);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    public int getValue() {
        return mValue;
    }

    public void setMinValue(int minValue) {
        if (mMinValue == minValue) {
            return;
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        mMinValue = minValue;
        if (mMinValue > mValue) {
            mValue = mMinValue;
        }
        updateWrapSelectorWheel();
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        if (mMaxValue == maxValue) {
            return;
        }
        if (maxValue < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        mMaxValue = maxValue;
        if (mMaxValue < mValue) {
            mValue = mMaxValue;
        }
        updateWrapSelectorWheel();
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    /**
     * Ensures that the scroll wheel is adjusted i.e. there is no offset and the
     * middle element is in the middle of the widget.
     *
     * @return Whether an adjustment has been made.
     */
    private boolean ensureScrollWheelAdjusted() {
        // adjust to the closest value
        int deltaY = mInitialScrollOffset - mCurrentScrollOffset;
        if (deltaY != 0) {
            mPreviousScrollerY = 0;
            if (Math.abs(deltaY) > mSelectorElementHeight / 2) {
                deltaY += (deltaY > 0) ? -mSelectorElementHeight : mSelectorElementHeight;
            }
            mAdjustScroller.startScroll(0, 0, 0, deltaY, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
            invalidate();
            return true;
        }
        return false;
    }

    static private String formatNumberWithLocale(int value) {
        return String.format(Locale.getDefault(), "%02d", value);
    }
}
