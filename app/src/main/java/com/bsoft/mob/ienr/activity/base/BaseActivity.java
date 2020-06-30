package com.bsoft.mob.ienr.activity.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.barcode.Devices;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.components.tts.SpeechSynthesizerFactory;
import com.bsoft.mob.ienr.event.BaseEvent;
import com.bsoft.mob.ienr.fragment.dialog.MyDialogFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.listener.ListSelected;
import com.bsoft.mob.ienr.util.AsyncTaskUtil;
import com.bsoft.mob.ienr.util.DensityUtil;
import com.bsoft.mob.ienr.util.VibratorUtil;
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
import java.util.Map;


/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 上午11:43:05 类说明 应用程序Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * 关闭
     */
    public static final String CLOSE_ACTION = "com.bsoft.mob.ienr.close.action";

    protected LinkedList<AsyncTask<?, ?, ?>> tasks = new LinkedList<AsyncTask<?, ?, ?>>();

    protected Context mContext;
    protected FragmentActivity mFragmentActivity;
    protected AppApplication mAppApplication;
    protected AppApplication application;
    protected BsoftActionBar actionBar;
    protected SwipeRefreshLayout id_swipe_refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setupLayoutResId());
        //after setContentView
        // fixNavigationBar(getContentViewRootLayout());
        /*  IntentFilter filter = new IntentFilter();
        filter.addAction(CLOSE_ACTION);
        initFloatMenu(this);*/
        mAppApplication = (AppApplication) getApplication();
        application = (AppApplication) getApplication();
        mContext = this;
        mFragmentActivity = this;
        EventBus.getDefault().register(this);
        initActionBar();
        initFloatMenu(mContext);
        // 设置为多媒体音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initSwipeRefreshLayout();
        /**
         * last
         */
        initView(savedInstanceState);
    }

    protected void fixNavigationBar(View rootLayout) {
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_sc) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)|| manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_lachesis) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新SC7 NR510
            rootLayout.setPadding(rootLayout.getPaddingLeft(),
                    rootLayout.getPaddingTop(),
                    rootLayout.getPaddingRight(),
                    rootLayout.getPaddingBottom() + ScreenUtil.getNavigationBarHeight());

        }
     /*   if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_nr510) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新NR510 NR510 旧版
            rootLayout.setPadding(rootLayout.getPaddingLeft(),
                    rootLayout.getPaddingTop(),
                    rootLayout.getPaddingRight(),
                    rootLayout.getPaddingBottom() + ScreenUtil.getNavigationBarHeight());

        }*/
    }

    private void initActionBar() {
        //
        actionBar = (BsoftActionBar) findViewById(R.id.actionbar);
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
        id_swipe_refresh_layout = (SwipeRefreshLayout) findViewById(configSwipeRefreshLayoutResId());
        if (id_swipe_refresh_layout == null) {
            Log.e(Constant.TAG, "initSwipeRefreshLayout:id_swipe_refresh_layout == null");
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

    protected abstract int setupLayoutResId();

    protected abstract void initView(Bundle savedInstanceState);

    @Override
    protected void onStop() {
        super.onStop();
        /**
         * 推荐在 在 onPause / onStop 里停止动画之类的
         */
        if (this.isFinishing()) {
            hideLoadingDialog();
            hideSwipeRefreshLayout();
        }
        //取消当前
//        new OkHttpTool().cancelByTag(this);
    }

    @Override
    protected void onDestroy() {

        for (AsyncTask<?, ?, ?> task : tasks) {
            AsyncTaskUtil.cancelTask(task);
        }
        //切记
        mFloatMenuManager = null;
        //Crouton.cancelAllCroutons();
        //取消所有
//        new OkHttpTool().cancelAll();
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    private android.support.v4.app.DialogFragment newFragment;

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
        DialogManager.showLoadingDialog(mFragmentActivity, msg, false);
    }

    protected void showLoadingDialog(int msgResid) {
        DialogManager.showLoadingDialog(mFragmentActivity,
                getString(msgResid), false);
    }

    protected void showTipDialog(String msg) {
        TextView titleTextView = ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips));
        DialogManager.showTipDialog(mFragmentActivity, titleTextView, msg);
    }

    protected void showTipDialog(int msgResID) {
        showTipDialog(getString(msgResID));
    }


    protected void showInfoDialog(String info) {
        newFragment = MyDialogFragment.newInfoInstance(info);
        showDialog(newFragment);
    }

    @Deprecated
    protected void hideDialog() {

        if (newFragment != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .remove(newFragment).commitAllowingStateLoss();
            } catch (Exception ex) {
                Log.e(Constant.TAG, ex.getMessage(), ex);
            }
            newFragment = null;
        }
    }


    protected void showMsgAndVoice(int resid) {
        showMsgAndVoice(getString(resid));
    }

    protected void showMsgAndVoice(String msg) {
        showSnack(msg);
        playVoice(msg);
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

    protected void showPickerDateCompat(YmdHMs ymdHMs, int viewId) {
        newFragment = MyDialogFragment.newDateInstance(ymdHMs.year, ymdHMs.month, ymdHMs.day, viewId,-1,-1);
        showDialog(newFragment);
    }

    protected void showPickerDateTimeCompat(YmdHMs ymdHMs, int viewId) {
        newFragment = MyDialogFragment.newDateTimeInstance(ymdHMs.year, ymdHMs.month, ymdHMs.day,
                ymdHMs.hour, ymdHMs.minute,
                viewId,-1,-1);
        showDialog(newFragment);
    }

    protected void showPickerTimeDialog(YmdHMs ymdHMs, int viewId) {
        newFragment = MyDialogFragment.newTimeInstance(ymdHMs.hour, ymdHMs.minute, viewId);
        showDialog(newFragment);
    }

    protected void showInputDiaolog(String title, int viewId) {
        newFragment = MyDialogFragment.newInputInstance(title, viewId);
        showDialog(newFragment);
    }

    protected void showConfirmDialog(String msg, String action) {
        newFragment = MyDialogFragment.newConfirmInstance(msg, action);
        showDialog(newFragment);
    }

    protected void showListDialog(String msg, int viewId, Map<String, String> stringMap) {
        showListDialog(msg, viewId, stringMap, null);
    }

    public void showListDialog(String msg, int viewId, Map<String, String> stringMap, ListSelected listSelected) {
        MyDialogFragment myDialogFragment = MyDialogFragment.newListInstance(msg, viewId, stringMap, false);
        myDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void showDialog(DialogFragment newFragment) {
        try {
            if (!newFragment.isAdded()) {
                getSupportFragmentManager().beginTransaction()
                        .add(newFragment, "dialog").commitAllowingStateLoss();

            }
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }
    }

    /**
     * 响应日期选择
     *
     * @param year
     * @param month 1~12
     * @param dayOfMonth
     * @param viewId
     */
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        // 空实现

    }

    /**
     * 响应日期时间选择
     *
     * @param year
     * @param month  1~12
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

    public void onInputCompleteed(String content, int viewId) {

    }

    public void onConfirmSet(String action) {

    }

    public void onListSelected(String key, String value) {

    }


    protected List<IFloatMenuItem> configFloatMenuItems() {
        return null;
    }

    protected void updateFloatMenuItems(List<IFloatMenuItem> floatMenuItems) {
        mFloatMenuManager.closeMenu();
        mFloatMenuManager.setMenu(floatMenuItems).buildMenu();
    }

    private FloatMenuManager mFloatMenuManager;

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
/*

        //单独处理  app病人列表 后台回前台  Menu打开时不显示 FloatMenu
        if (mContext != null && mContext instanceof MainActivity) {
            SlidingMenu slidingMenu = ((MainActivity) mContext).sm;
            if (slidingMenu != null && !slidingMenu.isMenuShowing()) {
                if (mFloatMenuManager != null) {
                    mFloatMenuManager.show();
                }
                return;
            }
        }*/
        //显示 FloatMenu
        if (mFloatMenuManager != null) {
            mFloatMenuManager.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //隐藏 FloatMenu
        if (mFloatMenuManager != null) {
            mFloatMenuManager.hide();
        }
    }


    /**
     * 获取根视图的容器 包含标题栏 content等
     */
    protected View getContentView() {
        //DecorView是一个FrameLayout子类  里面有一个id为content的FrameLayout用来存放我们的布局
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }

    /**
     * 获取根视图  xml根节点 Layout
     */
    protected View getContentViewRootLayout() {
        //DecorView是一个FrameLayout子类  里面有一个id为content的FrameLayout用来存放我们的布局
        ViewGroup viewGroup = (ViewGroup) getContentView();
        return viewGroup.getChildAt(0);
    }

    public void showSnack(int resId) {
        showSnack(getString(resId), Snackbar.LENGTH_SHORT);
    }

    public void showSnack(int resId, int duration) {
        showSnack(getString(resId), duration);
    }

    public void showSnack(CharSequence text) {
        showSnack(text, Snackbar.LENGTH_SHORT);
    }

    public void showSnack(CharSequence text, int duration) {
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_sc) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新SC7 NR510
            Toast.makeText(mContext, text, duration).show();
        } else if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_nr510) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新NR510 NR510
            Toast.makeText(mContext, text, duration).show();
        } else {
            //联新SC7 NR510部分页面不显示
//            Snackbar.make(getContentViewRootLayout(), "TEST", duration).show();
            showSnackColored(text, duration);
        }
    }

    public void showSnackColored(CharSequence text, int duration) {
        View root = getContentViewRootLayout();
        Snackbar snackbar = Snackbar.make(root, text, duration);
        //修改Snackbar的背景色
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        TextView snackbarText = view.findViewById(R.id.snackbar_text);
        if (snackbarText != null) {
            snackbarText.setTextColor(Color.WHITE);
        }
      /*  Button snackbarAction = view.findViewById(R.id.snackbar_action);
        if (snackbarAction != null) {
            snackbarAction.setTextColor(Color.parseColor("#00ff00"));
        }*/
        snackbar.show();

    }

    public void showToast(int resId) {
        Toast.makeText(mContext, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showToast(CharSequence text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    public void showToast(CharSequence text, int duration) {
        Toast.makeText(mContext, text, duration).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseEvent baseEvent) {

    }

}
