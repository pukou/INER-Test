package com.bsoft.mob.ienr.view.menus;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.fragment.BatchLifeSymptomFragment;
import com.bsoft.mob.ienr.fragment.user.LifeSymptomFragment;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.service.FloatingWindowService;
import com.bsoft.mob.ienr.service.LogoutService;

public class SlidingActivityHelper {

    private Activity mActivity;

    private SlidingMenu mSlidingMenu;

    private View mViewAbove;

    private View mViewBehind;

    private boolean mBroadcasting = false;

    private boolean mOnPostCreateCalled = false;

    private boolean mEnableSlide = true;

    // 是否是主页面
    private boolean isMainView = true;

    public void setIsMainView(boolean isMainView) {
        this.isMainView = isMainView;
    }

    public boolean isMainView() {
        return isMainView;
    }

    /**
     * Instantiates a new SlidingActivityHelper.
     *
     * @param activity the associated activity
     */
    public SlidingActivityHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * Sets mSlidingMenu as a newly inflated SlidingMenu. Should be called
     * within the activitiy's onCreate()
     *
     * @param savedInstanceState the saved instance state (unused)
     */
    public void onCreate(Bundle savedInstanceState) {
        /*mSlidingMenu = (SlidingMenu) LayoutInflater.from(mActivity).inflate(
                R.layout.slidingmenumain, null);*/
        ifNeedInit();
        mSlidingMenu.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private void ifNeedInit() {
        if (mSlidingMenu == null) {
            mSlidingMenu = new SlidingMenu(mActivity);
        }
    }

    /**
     * Further SlidingMenu initialization. Should be called within the
     * activitiy's onPostCreate()
     *
     * @param savedInstanceState the saved instance state (unused)
     */
    public void onPostCreate(Bundle savedInstanceState) {
        if (mViewBehind == null || mViewAbove == null) {
            throw new IllegalStateException(
                    "Both setBehindContentView must be called "
                            + "in onCreate in addition to setContentView.");
        }

        mOnPostCreateCalled = true;

        mSlidingMenu.attachToActivity(mActivity,
                mEnableSlide ? SlidingMenu.SLIDING_WINDOW
                        : SlidingMenu.SLIDING_CONTENT);

        final boolean open;
        final boolean secondary;
        if (savedInstanceState != null) {
            open = savedInstanceState.getBoolean("SlidingActivityHelper.open");
            secondary = savedInstanceState
                    .getBoolean("SlidingActivityHelper.secondary");
        } else {
            open = false;
            secondary = false;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (open) {
                    if (secondary) {
                        mSlidingMenu.showSecondaryMenu(false);
                    } else {
                        mSlidingMenu.showMenu(false);
                    }
                } else {
                    mSlidingMenu.showContent(false);
                }
            }
        });
    }

    /**
     * Controls whether the ActionBar slides along with the above view when the
     * menu is opened, or if it stays in place.
     *
     * @param slidingActionBarEnabled True if you want the ActionBar to slide along with the
     *                                SlidingMenu, false if you want the ActionBar to stay in place
     */
    public void setSlidingActionBarEnabled(boolean slidingActionBarEnabled) {
        if (mOnPostCreateCalled)
            throw new IllegalStateException(
                    "enableSlidingActionBar must be called in onCreate.");
        mEnableSlide = slidingActionBarEnabled;
    }

    /**
     * Finds a view that was identified by the id attribute from the XML that
     * was processed in onCreate(Bundle).
     *
     * @param id the resource id of the desired view
     * @return The view if found or null otherwise.
     */
    public View findViewById(int id) {
        View v;
        if (mSlidingMenu != null) {
            v = mSlidingMenu.findViewById(id);
            if (v != null)
                return v;
        }
        return null;
    }

    /**
     * Called to retrieve per-instance state from an activity before being
     * killed so that the state can be restored in onCreate(Bundle) or
     * onRestoreInstanceState(Bundle) (the Bundle populated by this method will
     * be passed to both).
     *
     * @param outState Bundle in which to place your saved state.
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("SlidingActivityHelper.open",
                mSlidingMenu.isMenuShowing());
        outState.putBoolean("SlidingActivityHelper.secondary",
                mSlidingMenu.isSecondaryMenuShowing());
    }

    /**
     * Register the above content view.
     *
     * @param v      the above content view to register
     * @param params LayoutParams for that view (unused)
     */
    public void registerAboveContentView(View v, LayoutParams params) {
        if (!mBroadcasting)
            mViewAbove = v;
    }

    /**
     * Set the activity content to an explicit view. This view is placed
     * directly into the activity's view hierarchy. It can itself be a complex
     * view hierarchy. When calling this method, the layout parameters of the
     * specified view are ignored. Both the width and the height of the view are
     * set by default to MATCH_PARENT. To use your own layout parameters, invoke
     * setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     * instead.
     *
     * @param v The desired content to display.
     */
    public void setContentView(View v) {
        mBroadcasting = true;
        mActivity.setContentView(v);
    }

    /**
     * Set the behind view content to an explicit view. This view is placed
     * directly into the behind view 's view hierarchy. It can itself be a
     * complex view hierarchy.
     *
     * @param view         The desired content to display.
     * @param layoutParams Layout parameters for the view. (unused)
     */
    public void setBehindContentView(View view, LayoutParams layoutParams) {
        mViewBehind = view;
        ifNeedInit();
        mSlidingMenu.setMenu(mViewBehind);
    }

    /**
     * Gets the SlidingMenu associated with this activity.
     *
     * @return the SlidingMenu associated with this activity.
     */
    public SlidingMenu getSlidingMenu() {
        return mSlidingMenu;
    }

    /**
     * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
     */
    public void toggle() {
        mSlidingMenu.toggle();
        // if (mSlidingMenu.isMenuShowing()) {
        // showContent();
        // } else {
        // showMenu();
        // }
    }

    /**
     * Close the SlidingMenu and show the content view.
     */
    public void showContent() {
        mSlidingMenu.showContent();
    }

    /**
     * Open the SlidingMenu and show the menu view.
     */
    public void showMenu() {
        mSlidingMenu.showMenu();
    }

    /**
     * Open the SlidingMenu and show the secondary menu view. Will default to
     * the regular menu if there is only one.
     */
    public void showSecondaryMenu() {
        mSlidingMenu.showSecondaryMenu();
    }

    /**
     * On key up.
     *
     * @param keyCode the key code
     * @param event   the event
     * @return true, if successful
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //1  左侧 展开状态下=》直接提示退出
            //2  左侧 未展开状态下=》隐藏键盘等=》展开 =》直接提示退出
            //3  右侧 展开状态下=》直接结束
            //4  右侧 未展开状态下=》隐藏键盘等=》展开 =》直接结束
            if (mActivity != null) {
                Fragment fragment = ((FragmentActivity) mActivity)
                        .getSupportFragmentManager().findFragmentById(
                                R.id.id_frame_layout);
                if (isMainView()) {
                    //1/2
                    if (mSlidingMenu.isMenuShowing()) {
                        showExitDialog();
                        return true;
                    } else {
                        //======
                        if (fragment instanceof LifeSymptomFragment) {
                            LifeSymptomFragment lifeSymptomFragment = (LifeSymptomFragment) fragment;
                            if (lifeSymptomFragment.isKeyboardShowing()) {
                                lifeSymptomFragment.hideKeyboard();
                                return true;
                            }
                        } else if (fragment instanceof BatchLifeSymptomFragment) {
                            BatchLifeSymptomFragment batchLifeSymptomFragment = (BatchLifeSymptomFragment) fragment;
                            if (batchLifeSymptomFragment.isKeyboardShowing()) {
                                batchLifeSymptomFragment.hideKeyboard();
                                return true;
                            }
                        }
                        //======
                        //2222
                        showMenu();
                        return true;
                    }
                } else {
                    //3/4
                    if (mSlidingMenu.isMenuShowing()) {
                        mActivity.finish();
                        return true;
                    } else {
                        //======
                        if (fragment instanceof LifeSymptomFragment) {
                            LifeSymptomFragment lifeSymptomFragment = (LifeSymptomFragment) fragment;
                            if (lifeSymptomFragment.isKeyboardShowing()) {
                                lifeSymptomFragment.hideKeyboard();
                                return true;
                            }
                        } else if (fragment instanceof BatchLifeSymptomFragment) {
                            BatchLifeSymptomFragment batchLifeSymptomFragment = (BatchLifeSymptomFragment) fragment;
                            if (batchLifeSymptomFragment.isKeyboardShowing()) {
                                batchLifeSymptomFragment.hideKeyboard();
                                return true;
                            }
                        }
                        //======
                        //4444
                        showMenu();
                        return true;
                    }
                }
            }
        }
        return false;
    }


    protected void showExitDialog() {
        AlertDialog.Builder builder = new Builder(mActivity);
        builder.setMessage("确定要退出吗?");
        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mActivity,mActivity.getString(R.string.project_tips)));
        builder.setPositiveButton(R.string.project_operate_ok,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 线程释放
                        // ImageLoader.shutdown();
                        // mActivity.sendBroadcast(new Intent(
                        // BaseActivity.CLOSE_ACTION));
                        Intent service = new Intent(mActivity,
                                LogoutService.class);
                        mActivity.startService(service);
                        mActivity.stopService(new Intent(mActivity,
                                FloatingWindowService.class));
                        mActivity.finish();
                    }
                });
        builder.setNegativeButton(R.string.project_operate_cancel,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
