package com.bsoft.mob.ienr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;

import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.api.AdviceCheckApi;
import com.bsoft.mob.ienr.api.AnnounceApi;
import com.bsoft.mob.ienr.api.BloodGlucoseApi;
import com.bsoft.mob.ienr.api.BloodSugarApi;
import com.bsoft.mob.ienr.api.BloodTransfusionApi;
import com.bsoft.mob.ienr.api.CatheterApi;
import com.bsoft.mob.ienr.api.DailyCareApi;
import com.bsoft.mob.ienr.api.DailyWorkApi;
import com.bsoft.mob.ienr.api.EvaluateApi;
import com.bsoft.mob.ienr.api.ExpenseApi;
import com.bsoft.mob.ienr.api.HandOverApi;
import com.bsoft.mob.ienr.api.HealthGuidApi;
import com.bsoft.mob.ienr.api.InspectionApi;
import com.bsoft.mob.ienr.api.KernelApi;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.api.OutControlApi;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.api.SkinTestApi;
import com.bsoft.mob.ienr.api.TradApi;
import com.bsoft.mob.ienr.api.UpdateApi;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.api.VisitApi;
import com.bsoft.mob.ienr.barcode.Devices;
import com.bsoft.mob.ienr.components.setting.SettingConfig;
import com.bsoft.mob.ienr.components.tts.SpeechSynthesizerFactory;
import com.bsoft.mob.ienr.components.wifi.WifiReceiver;
import com.bsoft.mob.ienr.fragment.user.SickPersonInfoFragment;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.MemuVo;
import com.bsoft.mob.ienr.model.PDAInfo;
import com.bsoft.mob.ienr.model.evaluate.EvaluateTempDataBean;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.kernel.UserConfig;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSymptomTempDataBean;
import com.bsoft.mob.ienr.util.DeviceUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;
import com.pgyersdk.crash.PgyCrashManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及相关内存变量
 */
// @ReportsCrashes(formKey = "", // This is required for backward compatibility
// but
// // not used
// mode = ReportingInteractionMode.TOAST, resToastText =
// R.string.crash_report_msg)
public class AppApplication extends MultiDexApplication {
    private static final String TAG = "AppApplication";
    // 请求头字符串
    public String authorizationString = "Basic";
    // 请求头字符串 session id
    public String JSESSIONID = "";
    // 当前病人
    public SickPersonVo sickPersonVo;
    // 扫描是否成功标志，表示是否要做操作
    // public boolean isScanOK;
    // 当前选择的病人模块
    public int userModelItem;
    private static Context context;
    SettingConfig settingConfig;

    // 网络监听Receiver
    WifiReceiver wifiReceiver;

    //PDAINFO信息
    public PDAInfo pdaInfo = null;

    private HashMap<String, Object> serviceMap = new HashMap<String, Object>(10);

    private Vector<AreaVo> areaList = new Vector<AreaVo>();

    public LoginUser user;

    public boolean isChangeUser = true;

    private String areaId;

    // 机构ID
    public String jgId;

    public UserConfig userConfig = new UserConfig();
    public static Context getContext(){
        return context;
    }

    //员工管理床位列表
    public List<String> emplBedList = new ArrayList<>();

    public void updateSystemDateTime(String serverDateTime) {
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        if (manufacturer.toLowerCase().trim().equals(Devices.M_lachesis_sc) &&
                model.toLowerCase().trim().equals(Devices.lachesis_nr510)) {
            //联新SC7 NR510 更新系统时间
//            initLian_SC7_Time(serverDateTime);
        }

    }

    //public String mYSLXTemp;
    public String mHQFSTemp;
    public Map<String, LifeSymptomTempDataBean> mLifeSymptomTempDataBean;//BDXM-->LifeSymptomTempDataBean
    public Map<String, EvaluateTempDataBean> mEvaluateTempDataBean;// PGLX-->EvaluateTempDataBean

    final AppHttpClient client = new AppHttpClient(this);

    // 病人状态
    public SparseIntArray stateMape;

    public Vector<AreaVo> getAreaList() {
        return areaList;
    }


    public SettingConfig getSettingConfig() {
        if (null == settingConfig) {
            settingConfig = new SettingConfig(this);
        }
        return settingConfig;
    }

    public void updateMainModel() {
        getSettingConfig().initMainMenus(this);
    }

    public void updateUserModel() {
        getSettingConfig().initUserMenus(this);
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }


    /**
     * add by louis
     *
     * @return
     */
    public ArrayList<MemuVo> getMainModel() {
        ArrayList<MemuVo> mainModel = getSettingConfig().mainModel;
        if (getCrrentArea() != null && "[isSurgery]".equals(getCrrentArea().YGDM)) {
            ArrayList<MemuVo> mainModelSSKSTemp = new ArrayList<>(mainModel);
            ArrayList<MemuVo> mainModelSSKS = new ArrayList<>();
            for (int i = 0; i < mainModelSSKSTemp.size(); i++) {
                if ("com.bsoft.mob.ienr.fragment.SickPersonListFragment".equals(mainModelSSKSTemp.get(i).tclass)) {
                    mainModelSSKS.add(mainModelSSKSTemp.get(i));
                } else if ("com.bsoft.mob.ienr.fragment.BatchHandOverFragment".equals(mainModelSSKSTemp.get(i).tclass)) {
                    mainModelSSKS.add(mainModelSSKSTemp.get(i));
                }
            }
            //是手术科室
            return mainModelSSKS;
        } else {
            return mainModel;
        }
    }

    /**
     * add by louis
     *
     * @return
     */
    public ArrayList<MemuVo> getUserModel() {
        ArrayList<MemuVo> userModel = getSettingConfig().userModel;
        if (getCrrentArea() != null && "[isSurgery]".equals(getCrrentArea().YGDM)) {
            ArrayList<MemuVo> userModelSSKSTemp = new ArrayList<>(userModel);
            ArrayList<MemuVo> userModelSSKS = new ArrayList<>();
            for (int i = 0; i < userModelSSKSTemp.size(); i++) {
                if ("com.bsoft.mob.ienr.fragment.user.SickPersonInfoFragment".equals(userModelSSKSTemp.get(i).tclass)) {
                    userModelSSKS.add(userModelSSKSTemp.get(i));
                } else if ("com.bsoft.mob.ienr.fragment.user.HandOverFragment".equals(userModelSSKSTemp.get(i).tclass)) {
                    userModelSSKS.add(userModelSSKSTemp.get(i));
                }
            }
            //是手术科室
            return userModelSSKS;
        } else {
            return userModel;
        }
    }

    public Fragment getUserModelFragment() {
        Fragment fragment = null;
        try {
            fragment = (Fragment) Class.forName(
                    getUserModel().get(userModelItem).tclass)
                    .newInstance();
            return fragment;
        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
        }
        return new SickPersonInfoFragment();
    }

    public void setAreaList(Vector<AreaVo> areaList) {

        if (areaList == null) {
            return;
        }
        this.areaList = areaList;

        for (AreaVo vo : areaList) {
            if (vo.MRBZ == 1) {
                this.areaId = vo.KSDM;
                this.user.MRBZ = vo.MRBZ;
                break;
            }
        }
    }

    public AreaVo getCrrentArea() {

        if (areaList == null) {
            return null;
        }

        int position = areaList.indexOf(new AreaVo(getAreaId()));
        if (position != -1) {
            AreaVo areaVo = areaList.get(position);
            return areaVo;
        } else {
            if (areaList.size() > 0) {
                return areaList.get(0);
            }
        }
        return null;
    }

    public String getSerialNumber() {
        return DeviceUtil.getSerial4Logic();
    }

    /**
     * //当是手术科室时候  KSDM 存放 SSKS
     *
     * @return
     */
    public String getAreaId() {

        if (this.areaId != null) {
            return this.areaId;
        }

        for (AreaVo vo : areaList) {
            if (vo.MRBZ == 1) {
                this.areaId = vo.KSDM;
                return this.areaId;
            }
        }
        this.areaId = areaList.size() > 0 ? areaList.get(0).KSDM : null;
        return this.areaId;
    }

    // 保存图片的路径
    // private static String storeDir;

    // 分辨率-宽带
    // private int widthPixels;
    // private int heightPixels;

    // private static LocalBroadcastManager mLocalBroadcastManager;
    private static AppApplication instance;

    public static AppApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        if (Constant.DEBUG_LOCAL) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());
        }

        super.onCreate();
        context=getApplicationContext();
        instance = this;
        // ACRA.init(this);
        DeviceUtil.getDeviceInfo(this);
        //
        MyUncaughtExceptionHandler.getInstance().init(this);

        // 写错误日志于本地目录
       /* CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());*/

        initWifiReceiver();
        initRegisterActivityLifecycleCallbacks();
        initOverallField();

        // 初始化时区 2015-6-25 by lvzc
        initTimeZone();
        // 初始化病人状态
        initState();


        // TODO 数据库调试 正式发布时注释
        // Stetho.initialize(Stetho
        // .newInitializerBuilder(this)
        // .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
        // .enableWebKitInspector(
        // Stetho.defaultInspectorModulesProvider(this)).build());

        PgyCrashManager.register(this);

        SpeechSynthesizerFactory.getInstance().init(this);

    }
    private void finishWork() {

    }

    //判断前后台
    private int mStartedActivityCount;

    public boolean isAppBackground() {
        return mStartedActivityCount == 0;
    }

    //当前前台显示的 Activity
    private Activity mShowingActivity;

    private void setShowingActivity(Activity activity) {
        mShowingActivity = activity;
    }

    public Activity getShowingActivity() {
        return mShowingActivity;
    }

    public Activity getLastCreateActivity() {
        if (mCreatedActivities == null || mCreatedActivities.isEmpty()) {
            return null;
        }
        return mCreatedActivities.get(mCreatedActivities.size() - 1);
    }

    //管理
    private Vector<Activity> mCreatedActivities = new Vector<>();

    private void addCreatedActivity(Activity activity) {
        if (!mCreatedActivities.contains(activity)) {
            mCreatedActivities.add(activity);
        }
    }

    private void removeCreatedActivity(Activity activity) {
        if (mCreatedActivities.contains(activity)) {
            mCreatedActivities.remove(activity);
        }
    }


    public void finishAllActivity() {
        if (mCreatedActivities == null) {
            return;
        }
        for (Activity activity : mCreatedActivities) {
            ActivityCompat.finishAfterTransition(activity);
        }
        mCreatedActivities.clear();
    }


    public void exitApp() {
        try {
            finishAllActivity();
            //
            finishWork();
            // 彻底关闭程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    private void initRegisterActivityLifecycleCallbacks() {
//        mActivityCreatedLinkedList = Collections.synchronizedList(new LinkedList<>());
        //
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                addCreatedActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mStartedActivityCount++;
                setShowingActivity(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mStartedActivityCount--;
                if (getShowingActivity() == activity) {
                    setShowingActivity(null);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //
                removeCreatedActivity(activity);
            }
        });
    }


    private void initLian_SC7_Time(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return;
        }
        // Log.i(TAG, "initLian_SC7_Time: " + timeStr);
        Intent intent = new Intent("lachesis_synctime_value_notice_broadcast");
        intent.putExtra("lachesis_synctime_value_notice_broadcast_data", timeStr);
        sendBroadcast(intent);
    }

    /**
     * 初始化WIFI监听
     */
    public void initWifiReceiver() {

        if (wifiReceiver == null) {
            wifiReceiver = new WifiReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
        filter.addAction("android.net.wifi.SCAN_RESULTS");
        registerReceiver(wifiReceiver, filter);
    }

    /**
     * 取消WIFI监听
     */
    public void unRegistWifiReceiver() {

        if (wifiReceiver != null) {
            try {
                unregisterReceiver(wifiReceiver);
            } catch (Exception ex) {
                Log.e(Constant.TAG, ex.getMessage(), ex);
            }
        }
    }

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Shanghai");

    @Override
    public Object getSystemService(String name) {

        Object result = null;
        if (settingConfig != null) {
            result = getAppService(name);
        }
        if (result != null) {
            return result;
        }
        return super.getSystemService(name);

    }

    private Object getAppService(String key) {

        if (settingConfig == null || EmptyTool.isBlank(key)
                || !key.contains("com.bsoft.mob.ienr")) {
            return null;
        }
        if (serviceMap.containsKey(key)) {
            return serviceMap.get(key);
        }

        SettingConfig mConfig = settingConfig;
        if ("com.bsoft.mob.ienr.api.kernel".equals(key)) {

            KernelApi kernelApi = new KernelApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.KernelService));
            serviceMap.put("com.bsoft.mob.ienr.api.kernel", kernelApi);
            return kernelApi;
        } else if ("com.bsoft.mob.ienr.api.lifesymptom".equals(key)) {
            LifeSignApi lifeSignApi = new LifeSignApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.LifeSymptomService));
            serviceMap
                    .put("com.bsoft.mob.ienr.api.lifesymptom", lifeSignApi);
            return lifeSignApi;
        } else if ("com.bsoft.mob.ienr.api.advice".equals(key)) {

            AdviceApi adviceApi = new AdviceApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.AdviceService));
            serviceMap.put("com.bsoft.mob.ienr.api.advice", adviceApi);
            return adviceApi;
        } else if ("com.bsoft.mob.ienr.api.update".equals(key)) {

            UpdateApi updateApi = new UpdateApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.PDAUpdateServer));
            serviceMap.put("com.bsoft.mob.ienr.api.update", updateApi);
            return updateApi;
        } else if ("com.bsoft.mob.ienr.api.inspection".equals(key)) {

            InspectionApi inspectionApi = new InspectionApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.InspectionService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.inspection", inspectionApi);
            return inspectionApi;
        } else if ("com.bsoft.mob.ienr.api.expense".equals(key)) {

            ExpenseApi expenseApi = new ExpenseApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.ExpenseService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.expense", expenseApi);
            return expenseApi;
        } else if ("com.bsoft.mob.ienr.api.announce".equals(key)) {

            AnnounceApi announceApi = new AnnounceApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.AnnounceService));
            serviceMap.put("com.bsoft.mob.ienr.api.announce", announceApi);
            return announceApi;
        } else if ("com.bsoft.mob.ienr.api.nurserecord".equals(key)) {

            NurseRecordApi api = new NurseRecordApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.NurseRecordService));
            serviceMap.put("com.bsoft.mob.ienr.api.nurserecord", api);
            return api;

        } else if ("com.bsoft.mob.ienr.api.evalute".equals(key)) {

            EvaluateApi evaluateApi = new EvaluateApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.EvaluationServices_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.evalute", evaluateApi);
            return evaluateApi;
        } else if ("com.bsoft.mob.ienr.api.bloodTransfusion".equals(key)) {

            BloodTransfusionApi api = new BloodTransfusionApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.BloodTransfusionService));
            serviceMap.put("com.bsoft.mob.ienr.api.bloodTransfusion", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.DailyWorkApi".equals(key)) {

            DailyWorkApi api = new DailyWorkApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.DailyWorkService));
            serviceMap.put("com.bsoft.mob.ienr.api.DailyWorkApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.AdviceCheckApi".equals(key)) {

            AdviceCheckApi api = new AdviceCheckApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.AdviceCheckService));
            serviceMap.put("com.bsoft.mob.ienr.api.AdviceCheckApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.nurseform".equals(key)) {
            NurseFormApi api = new NurseFormApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.NurseFormService));
            serviceMap.put("com.bsoft.mob.ienr.api.nurseform", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.UserApi".equals(key)) {
            UserApi api = new UserApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.UserService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.UserApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.PatientApi".equals(key)) {
            PatientApi api = new PatientApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.PatientService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.PatientApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.LifeSignApi".equals(key)) {
            LifeSignApi api = new LifeSignApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.LifeSignService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.LifeSignApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.HealthGuidApi".equals(key)) {
            HealthGuidApi api = new HealthGuidApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.HealthGuidService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.HealthGuidApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.NursePlanApi".equals(key)) {
            NursePlanApi api = new NursePlanApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.NursePlanService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.NursePlanApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.BloodTransfusionApi".equals(key)) {
            BloodTransfusionApi api = new BloodTransfusionApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.BloodTransfusionService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.BloodTransfusionApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.VisitApi".equals(key)) {
            VisitApi api = new VisitApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.VisitService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.VisitApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.DailyCareApi".equals(key)) {
            DailyCareApi api = new DailyCareApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.DailyCareService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.DailyCareApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.OutControlApi".equals(key)) {
            OutControlApi api = new OutControlApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.OutControlService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.OutControlApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.AdviceApi".equals(key)) {

            AdviceApi adviceApi = new AdviceApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.AdviceService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.AdviceApi", adviceApi);
            return adviceApi;
        } else if ("com.bsoft.mob.ienr.api.NurseRecordApi".equals(key)) {
            NurseRecordApi api = new NurseRecordApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.NurseRecordService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.NurseRecordApi", api);
            return api;
        }
        //风险评估
        else if ("com.bsoft.mob.ienr.api.NurseFormApi".equals(key)) {
            NurseFormApi api = new NurseFormApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.EvaluationService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.NurseFormApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.AdviceApi".equals(key)) {
            AdviceApi api = new AdviceApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.AdviceService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.AdviceApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.catheter".equals(key)) {
            CatheterApi api = new CatheterApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.CatheterService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.catheter", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.BloodGlucoseApi".equals(key)) {
            BloodGlucoseApi api = new BloodGlucoseApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.BloodGlucoseService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.BloodGlucoseApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.HandOverApi".equals(key)) {
            HandOverApi api = new HandOverApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.HandOver_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.HandOverApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.UpdateApi".equals(key)) {
            UpdateApi updateApi = new UpdateApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.PDAUpdateServer_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.UpdateApi", updateApi);
            return updateApi;
        } else if ("com.bsoft.mob.ienr.api.OffLineApi".equals(key)) {
            OffLineApi api = new OffLineApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.OffLineService));
            serviceMap.put("com.bsoft.mob.ienr.api.OffLineApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.BloodSugarApi".equals(key)) {
            BloodSugarApi api = new BloodSugarApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.BloodSugarService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.BloodSugarApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.TradApi".equals(key)) {
            TradApi api = new TradApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.TradService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.TradApi", api);
            return api;
        } else if ("com.bsoft.mob.ienr.api.SkinTestApi".equals(key)) {
            SkinTestApi api = new SkinTestApi(client, this,
                    mConfig.urlMap.get(APIUrlConfig.SkinTestService_Java));
            serviceMap.put("com.bsoft.mob.ienr.api.SkinTestApi", api);
            return api;
        }
        return null;
    }

    /**
     * 重启APP
     */
    public void reboot() {
        PackageManager pm = getPackageManager();
        Intent oIntent = pm.getLaunchIntentForPackage(getPackageName());
        oIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(oIntent);
    }

    /**
     * 重启APP
     */
    public void reboot(final String msg) {
        Looper.prepare();
        BSToast.showToast(getApplicationContext(), msg,
                BSToast.LENGTH_LONG);
        Looper.loop();
        Timer timer = new Timer();// 实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                PackageManager pm = getPackageManager();
                Intent oIntent = pm.getLaunchIntentForPackage(getPackageName());
                oIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(oIntent);
                this.cancel();
            }
        }, 3000);
    }

    /**
     * 初始化全局变量
     */
    public void initOverallField() {
        settingConfig = new SettingConfig(this);
        serviceMap.clear();
    }

    // 初始化时区
    private void initTimeZone() {
        //
      /*
        1 存在2个问题，高版本该方式不安全，已经不可以使用这个方式了
        2 低版本存在部分机型被强制设置 东 8 区 后 时间倒是对了，通知栏不读取这个强制设置的东 8 区
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            AlarmManager timeZone = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (timeZone != null) {
                timeZone.setTimeZone(TIME_ZONE.getID());
            }
        }*/
        //
        TimeZone.setDefault(TIME_ZONE);
        System.setProperty("user.timezone", TIME_ZONE.getID());
    }

    private void initState() {
        stateMape = new SparseIntArray();
        stateMape.append(1, R.drawable.state_new);
        stateMape.append(2, R.drawable.state_boper);
        stateMape.append(3, R.drawable.state_aoper);
        stateMape.append(4, R.drawable.state_temperature);
        stateMape.append(5, R.drawable.state_pulse);
        stateMape.append(6, R.drawable.state_breathe);
        stateMape.append(7, R.drawable.state_heartrate);
        stateMape.append(8, R.drawable.state_bloodp);
        stateMape.append(9, R.drawable.state_critically);
        stateMape.append(10, R.drawable.state_owe);
        stateMape.append(11, R.drawable.state_irritable);
        stateMape.append(12, R.drawable.state_way);
    }

    public int getState(int id) {
        return stateMape.get(id);
    }

}
