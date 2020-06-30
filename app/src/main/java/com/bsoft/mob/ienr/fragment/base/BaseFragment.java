package com.bsoft.mob.ienr.fragment.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.barcode.Devices;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.components.tts.SpeechSynthesizerFactory;
import com.bsoft.mob.ienr.event.BaseEvent;
import com.bsoft.mob.ienr.fragment.dialog.MyDialogFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.util.AsyncTaskUtil;
import com.bsoft.mob.ienr.util.DensityUtil;
import com.bsoft.mob.ienr.util.VibratorUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;
import com.bsoft.mob.ienr.view.SimpleBackAction;
import com.bsoft.mob.ienr.view.floatmenu.FloatMenuManager;
import com.bsoft.mob.ienr.view.floatmenu.floatbutton.FloatButtonCfg;
import com.bsoft.mob.ienr.view.floatmenu.menu.FloatMenuCfg;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.classichu.dialogview.manager.DialogManager;
import com.classichu.popupwindow.util.ScreenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

/**
 * @author zzz
 */
public abstract class BaseFragment extends Fragment {
    private DialogFragment newFragment;
    protected View mRootLayout;
    protected BsoftActionBar actionBar;
    protected LinkedList<AsyncTask<?, ?, ?>> tasks = new LinkedList<AsyncTask<?, ?, ?>>();
    protected Context mContext;
    protected FragmentActivity mFragmentActivity;
    protected AppApplication mAppApplication;
    protected AppApplication application;
    protected SwipeRefreshLayout id_swipe_refresh_layout;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppApplication = (AppApplication) getActivity().getApplication();
        application = (AppApplication) getActivity().getApplication();
        EventBus.getDefault().register(this);
    }

    private void fixNavigationBar(View rootLayout) {
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_sc) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)
                || manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_lachesis) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)
                ) {
            //联新SC7 NR510
            //联新 NR510
            rootLayout.setPadding(rootLayout.getPaddingLeft(),
                    rootLayout.getPaddingTop(),
                    rootLayout.getPaddingRight(),
                    rootLayout.getPaddingBottom() + ScreenUtil.getNavigationBarHeight());

        }
      /*  if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_nr510) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新NR510 NR510
            rootLayout.setPadding(rootLayout.getPaddingLeft(),
                    rootLayout.getPaddingTop(),
                    rootLayout.getPaddingRight(),
                    rootLayout.getPaddingBottom() + ScreenUtil.getNavigationBarHeight());

        }*/
    }

    private void initActionBar() {
        //
        actionBar = (BsoftActionBar) mRootLayout.findViewById(R.id.actionbar);
        if (actionBar != null) {
            actionBar.setBackAction(new SimpleBackAction());
        }
    }

    protected int configSwipeRefreshLayoutResId() {
        return 0;
    }

    protected void toRefreshData() {
    }

    private void initSwipeRefreshLayout() {
        if (configSwipeRefreshLayoutResId() == 0) {
            return;
        }
        id_swipe_refresh_layout = mRootLayout.findViewById(configSwipeRefreshLayoutResId());
        if (id_swipe_refresh_layout == null) {
            Log.d(Constant.TAG, "initSwipeRefreshLayout:id_swipe_refresh_layout == null");
            return;
        }
        // id_swipe_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        id_swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        //mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        id_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                Log.d(Constant.TAG_COMM, "onRefresh刷新数据");
                toRefreshData();
            }
        });
    }

    protected void showSwipeRefreshLayout() {
        if (id_swipe_refresh_layout != null) {
            // id_swipe_refresh_layout.setRefreshing(true);
            id_swipe_refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    id_swipe_refresh_layout.setRefreshing(true);
                }
            });
        }
    }

    protected void hideSwipeRefreshLayout() {
        if (id_swipe_refresh_layout != null) {
            id_swipe_refresh_layout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    id_swipe_refresh_layout.setRefreshing(false);
                }
            }, 500);
        }
        //兼容hideLoadingDialog
        DialogManager.hideLoadingDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        mRootLayout = inflater.inflate(setupLayoutResId(), parent, false);
        initActionBar();
        fixNavigationBar(mRootLayout);
        //
        initFloatMenu(mContext);
        initSwipeRefreshLayout();
        /**
         * last
         */
        initView(mRootLayout, savedInstanceState);

        return mRootLayout;
    }

    protected abstract int setupLayoutResId();

    protected abstract void initView(View rootLayout, Bundle savedInstanceState);

    @Deprecated
    protected void hideDialog() {

        if (newFragment != null) {
            try {
                getChildFragmentManager().beginTransaction().remove(newFragment)
                        .commitAllowingStateLoss();
            } catch (Exception ex) {
                Log.e(Constant.TAG, ex.getMessage(), ex);
            }
            newFragment = null;
        }
    }

    protected void hideLoadingDialog() {
        DialogManager.hideLoadingDialog();
        //兼容 id_swipe_refresh_layout
        if (id_swipe_refresh_layout != null) {
            id_swipe_refresh_layout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    id_swipe_refresh_layout.setRefreshing(false);
                }
            }, 500);
        }
    }

    protected void showLoadingDialog(String msg) {
        if (!this.isAdded()) {
            return;
        }
        DialogManager.showLoadingDialog(mFragmentActivity, msg, false);
    }

    protected void showLoadingDialog(int msgResid) {
        if (!this.isAdded()) {
            return;
        }
        DialogManager.showLoadingDialog(mFragmentActivity,
                getString(msgResid), false);
    }

    protected void showTipDialog(String msg) {
        if (!this.isAdded()) {
            return;
        }
        TextView titleTextView = ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips));
        DialogManager.showTipDialog(mFragmentActivity, titleTextView, msg);
    }

    protected void showTipDialog(int msgResID) {
        if (!this.isAdded()) {
            return;
        }
        showTipDialog(getString(msgResID));
    }


    protected void showMsgAndVoice(int resid) {
        showMsgAndVoice(getString(resid));
    }

    protected void showMsgAndVoice(String str) {
        showSnack(str);
        playVoice(str);
    }

    protected void showMsgAndVoiceAndVibrator(int resid) {
        showMsgAndVoiceAndVibrator(getString(resid));
    }

    protected void showMsgAndVoiceAndVibrator(String msg) {
        showSnack(msg);
        playVoice(msg);
        VibratorUtil.vibrator(mContext, mAppApplication.getSettingConfig().vib);
    }


    private void playVoice(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            SpeechSynthesizerFactory.getInstance().speak(msg);
        }
    }

    protected void showDatePickerCompat(String nowDate, int viewId, long minDate, long maxDate) {
        if (EmptyTool.isBlank(nowDate)) {
            return;
        }
        YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(nowDate);
        newFragment = MyDialogFragment.newDateInstance(ymdHMs.year, ymdHMs.month, ymdHMs.day,
                viewId, minDate, maxDate);
        //
        showDialog();
    }

    protected void showDatePickerCompat(String nowDate, int viewId) {
        showDatePickerCompat(nowDate, viewId, -1, -1);
    }

    protected void showDateTimePickerCompat(String nowDateTime, int viewId) {
        if (EmptyTool.isBlank(nowDateTime)) {
            return;
        }
        YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(nowDateTime);
        newFragment = MyDialogFragment.newDateTimeInstance(ymdHMs.year,
                ymdHMs.month,
                ymdHMs.day,
                ymdHMs.hour,
                ymdHMs.minute,
                viewId, -1,-1);
        //
        showDialog();
    }

    protected void showTimePickerCompat(String nowTime, int viewId) {
        if (EmptyTool.isBlank(nowTime)) {
            return;
        }
        //√√√
        String dateTime = DateTimeFactory.getInstance().custom2DateTime(nowTime, DateTimeFormat.HHmm);
        YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateTime);
        newFragment = MyDialogFragment.newTimeInstance(
                ymdHMs.hour,
                ymdHMs.minute,
                viewId);
        //
        showDialog();
    }


    private void showDialog() {

        try {
            if (!newFragment.isAdded()) {
                getChildFragmentManager().beginTransaction().add(newFragment, "dialog")
                        .commitAllowingStateLoss();
            }
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }
    }

    /**
     * 响应date picker dialog时间选择事件，默认不作处理
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param viewId
     */
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {

    }

    /**
     * 需要配合activity的onBackPressed实现
     * public void onBackPressed() {
     * BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
     * .findFragmentById(R.id.content_frame);
     * if (!fragment.onKeyBackPressed()){
     * super.onBackPressed();
     * }
     * }
     *
     * @return
     */
    public boolean onKeyBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        for (AsyncTask<?, ?, ?> task : tasks) {
            AsyncTaskUtil.cancelTask(task);
        }
        if (mFloatMenuManager != null) {
            mFloatMenuManager.hide();
            mFloatMenuManager = null;
        }
        //取消所有
//        new OkHttpTool().cancelAll();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 响应日期时间选择
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hourOfDay
     * @param minute
     * @param viewId
     */
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        // 空实现

    }

    public void onTimeSet(int hourOfDay, int minute, int viewId) {
        // 空实现
    }
    /*private void setFloatPermission(final Context context) {
        FloatMenuManager.setPermission(new FloatMenuManager.IFloatMenuPermission() {
			@Override
			public boolean onRequestFloatButtonPermission() {
				FloatMenuManager.getInstance().applyPermission(context);
				return true;
			}

			@Override
			public boolean hasFloatButtonPermission(Context context) {
				return FloatMenuManager.getInstance().checkPermission(context);
			}

		});
	}*/

    protected List<IFloatMenuItem> configFloatMenuItems() {
        return null;
    }

    protected FloatMenuManager mFloatMenuManager;

    private void initFloatMenu(Context context) {
        List<IFloatMenuItem> items = configFloatMenuItems();
        if (items == null || items.size() <= 0) {
            return;
        }
        //1 初始化悬浮按钮配置，定义好悬浮按钮大小和icon的drawable
        int iconSize = DensityUtil.dp2px(56);
        Drawable icon = ContextCompatHelper.getDrawable(context, R.drawable.selector_menu_icon_colored, 0);
        if (mAppApplication.userConfig.floatMenuShowByIcon) {
            icon = ContextCompatHelper.getDrawable(context, R.drawable.selector_menu_icon, 0);
        }
        FloatButtonCfg buttonCfg = new FloatButtonCfg(iconSize, icon);

        //2 需要显示悬浮菜单
        //2.1 初始化悬浮菜单配置，有菜单item的大小和菜单item的个数
        int menuSize = DensityUtil.dp2px(250);
        int menuItemSize = DensityUtil.dp2px(48);
        FloatMenuCfg menuCfg = new FloatMenuCfg(menuSize, menuItemSize);
        //3 生成mFloatMenuManager
        mFloatMenuManager = new FloatMenuManager(context, buttonCfg, menuCfg);
        mFloatMenuManager.setMenu(items).buildMenu();
        mFloatMenuManager.setPermission(new FloatMenuManager.IFloatMenuPermission() {
            @Override
            public boolean onRequestFloatButtonPermission() {
                return true;
            }

            @Override
            public boolean hasFloatButtonPermission(Context context) {
                return true;
            }
        });
        //5 如果没有添加菜单，可以设置悬浮按钮点击事件
    /*	if (mFloatMenuManager.getMenuItemSize() == 0) {
            mFloatMenuManager.setOnFloatButtonClickListener(new FloatMenuManager.OnFloatButtonClickListener() {
				@Override
				public void onFloatButtonClick() {
					// toast("点击了悬浮按钮");
				}
			});
		}*/
        mFloatMenuManager.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFloatMenuManager != null) {
            mFloatMenuManager.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFloatMenuManager != null) {
            mFloatMenuManager.hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /**
         * 推荐在 在 onPause / onStop 里停止动画之类的
         */
        if (this.isRemoving()) {
            hideLoadingDialog();
            hideSwipeRefreshLayout();
        }
        //取消所有
//        new OkHttpTool().cancelByTag(this);
    }

    protected void showSnack(int resId) {
        if (mFragmentActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mFragmentActivity;
            baseActivity.showSnack(resId);
        }
    }

    protected void showSnack(int resId, int duration) {
        if (mFragmentActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mFragmentActivity;
            baseActivity.showSnack(resId, duration);
        }
    }

    protected void showSnack(CharSequence text) {
        if (mFragmentActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mFragmentActivity;
            baseActivity.showSnack(text);
        }
    }

    protected void showSnack(CharSequence text, int duration) {
        if (mFragmentActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mFragmentActivity;
            baseActivity.showSnack(text, duration);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseEvent baseEvent) {

    }


}
