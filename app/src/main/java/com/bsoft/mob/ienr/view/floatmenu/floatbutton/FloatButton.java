package com.bsoft.mob.ienr.view.floatmenu.floatbutton;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bsoft.mob.ienr.view.floatmenu.FloatMenuManager;
import com.bsoft.mob.ienr.view.floatmenu.FloatMenuUtil;
import com.bsoft.mob.ienr.view.floatmenu.runner.IFloatAction;
import com.bsoft.mob.ienr.view.floatmenu.runner.OnceRunnable;
import com.bsoft.mob.ienr.view.floatmenu.runner.ScrollRunner;
import com.bsoft.mob.ienr.view.floatmenu.utils.MotionVelocityUtil;


public class FloatButton extends FrameLayout implements IFloatAction {

    private FloatMenuManager floatMenuManager;
    private ImageView imageView;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager windowManager;
    private boolean isFirst = true;
    private boolean isAdded = false;
    private int mTouchSlop;
    /**
     * flag a touch is click event
     */
    private boolean isClick;
    private int mDownX, mDownY, mLastX, mLastY;
    private int mSize;
    private ScrollRunner mRunner;
    private int mVelocityX, mVelocityY;
    private MotionVelocityUtil mVelocity;
    private boolean sleep = false;
    private FloatButtonCfg mConfig;
    private OnceRunnable mSleepRunnable = new OnceRunnable() {
        @Override
        public void onRun() {
            if (isAdded) {
                sleep = true;
                moveToEdge(false, sleep);
            }
        }
    };

    public FloatButton(Context context, FloatMenuManager floatMenuManager, FloatButtonCfg config) {
        super(context);
        this.floatMenuManager = floatMenuManager;
        mConfig = config;
        init(context);
    }

    private void init(Context context) {
        imageView = new ImageView(context);
        final Drawable icon = mConfig.mIcon;
        mSize = mConfig.mSize;
        imageView.setImageDrawable(icon);
        ///ViewCompat.setBackground(imageView, icon);
        addView(imageView, new ViewGroup.LayoutParams(mSize, mSize));
        mLayoutParams = FloatMenuUtil.getMyLayoutParams();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mRunner = new ScrollRunner(this);
        mVelocity = new MotionVelocityUtil(context);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            onConfigurationChanged(null);
        }
    }

    public void attachToWindow(WindowManager windowManager) {
        this.windowManager = windowManager;
        if (!isAdded) {
            windowManager.addView(this, mLayoutParams);
            isAdded = true;
        }
    }

    public void detachFromWindow(WindowManager windowManager) {
        this.windowManager = null;
        if (isAdded) {
            removeSleepRunnable();
            windowManager.removeView(this);
            isAdded = false;
            sleep = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width != 0 && height != 0 && isFirst) {
            isFirst = false;
            int deltaX = 0;//默认FloatButtonCfg.LEFT_BOTTOM
            int deltaY = floatMenuManager.mScreenHeight - height;//默认FloatButtonCfg.LEFT_BOTTOM
            switch (mConfig.mFirstShowPlace) {
                case FloatButtonCfg.LEFT_TOP:
                    deltaX = 0;
                    deltaY = height;
                    break;
                case FloatButtonCfg.LEFT_CENTER:
                    deltaX = 0;
                    deltaY = floatMenuManager.mScreenHeight / 2 - height;
                    break;
                case FloatButtonCfg.LEFT_BOTTOM:
                    deltaX = 0;
                    deltaY = floatMenuManager.mScreenHeight - height;
                    break;
                case FloatButtonCfg.RIGHT_TOP:
                    deltaX = floatMenuManager.mScreenWidth - width;
                    deltaY = height;
                    break;
                case FloatButtonCfg.RIGHT_CENTER:
                    deltaX = floatMenuManager.mScreenWidth - width;
                    deltaY = floatMenuManager.mScreenHeight / 2 - height;
                    break;
                case FloatButtonCfg.RIGHT_BOTTOM:
                    deltaX = floatMenuManager.mScreenWidth - width;
                    deltaY = floatMenuManager.mScreenHeight - height;
                    break;
            }
            //mFirstShowPlaceYOffset 修正
            deltaY = deltaY - mConfig.mFirstShowPlaceYOffset;
            onMove(deltaX, deltaY);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        floatMenuManager.onConfigurationChanged(newConfig);
        moveToEdge(false, false);
        postSleepRunnable();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        mVelocity.acquireVelocityTracker(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void touchDown(int x, int y) {
        mDownX = x;
        mDownY = y;
        mLastX = mDownX;
        mLastY = mDownY;
        isClick = true;
        removeSleepRunnable();
    }

    private void touchMove(int x, int y) {
        int totalDeltaX = x - mDownX;
        int totalDeltaY = y - mDownY;
        int deltaX = x - mLastX;
        int deltaY = y - mLastY;
        if (Math.abs(totalDeltaX) > mTouchSlop || Math.abs(totalDeltaY) > mTouchSlop) {
            isClick = false;
        }
        mLastX = x;
        mLastY = y;
        if (!isClick) {
            onMove(deltaX, deltaY);
        }
    }

    private void touchUp() {
        mVelocity.computeCurrentVelocity();
        mVelocityX = (int) mVelocity.getXVelocity();
        mVelocityY = (int) mVelocity.getYVelocity();
        mVelocity.releaseVelocityTracker();
        if (sleep) {
            wakeUp();
        } else {
            if (isClick) {
                onClick();
            } else {
                moveToEdge(true, false);
            }
        }
        mVelocityX = 0;
        mVelocityY = 0;
    }

    private void moveToX(boolean smooth, int destX) {
        final int screenHeight = floatMenuManager.mScreenHeight;
        int height = getHeight();
        int destY = 0;
        if (mLayoutParams.y < 0) {
            destY = 0 - mLayoutParams.y;
        } else if (mLayoutParams.y > screenHeight - height) {
            destY = screenHeight - height - mLayoutParams.y;
        }
        if (smooth) {
            int dx = destX - mLayoutParams.x;
            int duration = getScrollDuration(Math.abs(dx));
            mRunner.start(dx, destY, duration);
        } else {
            onMove(destX - mLayoutParams.x, destY);
            postSleepRunnable();
        }
    }

    private void wakeUp() {
        final int screenWidth = floatMenuManager.mScreenWidth;
        int width = getWidth();
        int buttonHalfWidth = width / 2;
        int centerX = (screenWidth / 2 - buttonHalfWidth);
        int destX;
        destX = mLayoutParams.x < centerX ? 0 : screenWidth - width;
        sleep = false;
        moveToX(true, destX);
    }

    private void moveToEdge(boolean smooth, boolean forceSleep) {


        final int screenWidth = floatMenuManager.mScreenWidth;
        int width = getWidth();
        int buttonHalfWidth = width / 2;
        int buttonHideWidth = buttonHalfWidth;
        if (mConfig.mEdge_hide_width_percent >= 0 && mConfig.mEdge_hide_width_percent <= 1) {
            buttonHideWidth = (int) (width * mConfig.mEdge_hide_width_percent);
        }
        int centerX = (screenWidth / 2 - buttonHalfWidth);
        int destX;
        final int minVelocity = mVelocity.getMinVelocity();
        if (mLayoutParams.x < centerX) {//左边
            sleep = forceSleep ? true : Math.abs(mVelocityX) > minVelocity && mVelocityX < 0 || mLayoutParams.x < 0;
            destX = sleep ? -buttonHideWidth : 0;
        } else {
            sleep = forceSleep ? true : Math.abs(mVelocityX) > minVelocity && mVelocityX > 0 || mLayoutParams.x > screenWidth - width;
            destX = sleep ? screenWidth - (width - buttonHideWidth) : screenWidth - width;
        }
        moveToX(smooth, destX);
    }

    private int getScrollDuration(int distance) {
        return (int) (250 * (1.0f * distance / 800));
    }

    private void onMove(int deltaX, int deltaY) {
        mLayoutParams.x += deltaX;
        mLayoutParams.y += deltaY;
        if (windowManager != null) {
            windowManager.updateViewLayout(this, mLayoutParams);
        }
    }

    public void onMove(int lastX, int lastY, int curX, int curY) {
        onMove(curX - lastX, curY - lastY);
    }

    @Override
    public void onDone() {
        postSleepRunnable();
    }

    private void moveTo(int x, int y) {
        mLayoutParams.x += x - mLayoutParams.x;
        mLayoutParams.y += y - mLayoutParams.y;
        if (windowManager != null) {
            windowManager.updateViewLayout(this, mLayoutParams);
        }
    }

    public int getSize() {
        return mSize;
    }

    private void onClick() {
        floatMenuManager.floatbuttonX = mLayoutParams.x;
        floatMenuManager.floatbuttonY = mLayoutParams.y;
        floatMenuManager.onFloatButtonClick();
    }

    private void removeSleepRunnable() {
        mSleepRunnable.removeSelf(this);
    }

    public void postSleepRunnable() {
        if (!sleep && isAdded) {
            mSleepRunnable.postDelaySelf(this, mConfig.mEdge_hide_delayMillis < 0 ? 3000 : mConfig.mEdge_hide_delayMillis);
        }
    }
}
