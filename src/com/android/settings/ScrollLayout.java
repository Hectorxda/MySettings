package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 
 */
public class ScrollLayout extends ViewGroup {

    private static final String TAG = "ScrollLayout";
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mCurScreen;
    private int mDefaultScreen = 0;

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;

    private static final int SNAP_VELOCITY = 600;

    private int mTouchState = TOUCH_STATE_REST;
    private int mTouchSlop;
    private float mLastMotionX;
    private float mLastMotionY;
    private Settings2 mContext;
    private boolean isScroll;

    public ScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mScroller = new Scroller(context);
        mContext = (Settings2) context;
        mCurScreen = mDefaultScreen;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void attachViewToParent(View child, int index, LayoutParams params) {

        super.attachViewToParent(child, index, params);
    }

    @Override
    public void addView(View child) {

        super.addView(child);
    }

    @Override
    public void requestChildFocus(View child, View focused) {

        Log.d("requestChildFocus", "child = " + child);

        super.requestChildFocus(child, focused);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onLayout");
        if (changed) {
            int childLeft = 0;
            final int childCount = getChildCount();

            for (int i = 0; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE) {
                    final int childWidth = childView.getMeasuredWidth();
                    childView.layout(childLeft, 0, childLeft + childWidth,
                            childView.getMeasuredHeight());
                    childLeft += childWidth;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        // The children are given the same width and height as the scrollLayout
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        // Log.e(TAG, "moving to screen "+mCurScreen);
        scrollTo(mCurScreen * width, 0);
    }

    /**
     * According to the position of current layout scroll to the destination
     * page.
     */
    public void snapToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        // get the valid layout page
        Log.d("SSSSSS", "screen count = " + getChildCount());
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {

            final int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0,
                    Math.abs(delta) * 2);
            mCurScreen = whichScreen;
            Log.d("SSSSSS", "which screen = " + mCurScreen);
            invalidate();
        }
        startCurrentView();
    }

    public void setToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurScreen = whichScreen;
        scrollTo(whichScreen * getWidth(), 0);
    }

    public int getCurScreen() {
        return mCurScreen;
    }

    public View getNowScreen() {

        return this.getChildAt(mCurScreen);

    }

    private void startCurrentView() {

        String viewTag = (String) getChildAt(mCurScreen).getTag();

        Message message = new Message();

        if (TextUtils.equals(viewTag, Settings2.FIRST_INTENT_TAG)) {
            Intent firstIntent = new Intent(mContext, Settings.class);
            mContext.mLocalActivityManager.startActivity(
                    Settings2.FIRST_INTENT_TAG, firstIntent);
            message.what = Settings2.FIRST_VIEW;
        } else if (TextUtils.equals(viewTag, Settings2.SECOND_INTENT_TAG)) {
            Intent secondIntent = new Intent();
            secondIntent.setClassName("com.cyanogenmod.cmparts",
                    "com.cyanogenmod.cmparts.activities.MainActivity");
            mContext.mLocalActivityManager.startActivity(
                    Settings2.SECOND_INTENT_TAG, secondIntent);
            message.what = Settings2.SECOND_VIEW;
        } else {
            Intent thirdIntent = new Intent();
            thirdIntent.setClassName("com.cyanogenmod.cmparts",
                    "com.cyanogenmod.cmparts.activities.MyExtraToolsActivity");
            mContext.mLocalActivityManager.startActivity(
                    Settings2.THIRD_INTENT_TAG, thirdIntent);
            message.what = Settings2.THIRD_VIEW;
        }

        mContext.mHandler.sendMessage(message);
    }

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            Log.e(TAG, "event down!");
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mLastMotionX = x;
            mLastMotionY = y;
            break;

        case MotionEvent.ACTION_MOVE:

            int deltaX = (int) (mLastMotionX - x);
            int deltaY = (int) (mLastMotionY - y);
            mLastMotionX = x;
            mLastMotionY = y;
            enableScroll(deltaX);
            if (Math.abs(deltaY) - Math.abs(deltaX) <= -Math.abs(deltaX / 2)
                    && !isScroll) {
                scrollBy(deltaX, 0);
            }
            break;

        case MotionEvent.ACTION_UP:
            Log.e(TAG, "event : up");
            // if (mTouchState == TOUCH_STATE_SCROLLING) {
            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(3000);
            int velocityX = (int) velocityTracker.getXVelocity();

            Log.e(TAG, "velocityX:" + velocityX);

            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                // Fling enough to move left
                Log.e(TAG, "snap left");
                snapToScreen(mCurScreen - 1);
            } else if (velocityX < -SNAP_VELOCITY
                    && mCurScreen < getChildCount() - 1) {
                // Fling enough to move right
                Log.e(TAG, "snap right");
                snapToScreen(mCurScreen + 1);
            } else {
                snapToDestination();
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            // }
            mTouchState = TOUCH_STATE_REST;
            break;
        case MotionEvent.ACTION_CANCEL:
            mTouchState = TOUCH_STATE_REST;
            break;
        }
        postInvalidate();
        return true;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
        case MotionEvent.ACTION_MOVE:
            final int xDiff = (int) Math.abs(mLastMotionX - x);
            final int yDiff = (int) Math.abs(mLastMotionY - y);
            if (xDiff > mTouchSlop && yDiff - xDiff <= -xDiff / 2) {
                mTouchState = TOUCH_STATE_SCROLLING;

            }
            break;

        case MotionEvent.ACTION_DOWN:
            mLastMotionX = x;
            mLastMotionY = y;
            mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                    : TOUCH_STATE_SCROLLING;
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mTouchState = TOUCH_STATE_REST;
            break;
        }

        return mTouchState != TOUCH_STATE_REST;

    }

    @Override
    protected void onAttachedToWindow() {

        startCurrentView();
        super.onAttachedToWindow();
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {

        super.dispatchWindowFocusChanged(hasFocus);
    }

    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {

        super.dispatchWindowVisibilityChanged(visibility);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {

        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();
    }

    public void enableScroll(int deltaX) {
        if (deltaX < 0) {
            if (getScrollX() <= 20) {
                isScroll = true;
            } else {
                isScroll = false;
            }
        } else if (deltaX > 0) {
            int availableToScroll = getChildAt(getChildCount() - 1).getRight()
                    - getScrollX() - getWidth();
            if (availableToScroll <= 20) {
                isScroll = true;
            } else {
                isScroll = false;
            }
        }
    }
}
