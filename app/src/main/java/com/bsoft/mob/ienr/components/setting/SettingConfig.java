package com.bsoft.mob.ienr.components.setting;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.api.BaseApi;
import com.bsoft.mob.ienr.model.MemuVo;
import com.bsoft.mob.ienr.util.prefs.MainGuidePrefUtils;
import com.bsoft.mob.ienr.util.prefs.UserGuidePrefUtils;
import com.bsoft.mob.ienr.util.prefs.WifiPrefUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-24 下午4:40:15
 * @类说明 可配置的 url ， 扫描枪 ， 主模块 ， 用户模块
 */

public class SettingConfig {

    /**
     * 主导航
     */
    public ArrayList<MemuVo> mainModel = new ArrayList<MemuVo>();
    /**
     * 用户导航
     */
    public ArrayList<MemuVo> userModel = new ArrayList<MemuVo>();
    /**
     * WEB SERVICE url key-values
     */
    public HashMap<String, String> urlMap = new HashMap<String, String>();
    /**
     * 条码前缀
     */
    // public ArrayList<BarCode> barList = new ArrayList<BarCode>();

    /**
     * 消息震动
     */
    public boolean vib = true;

    public boolean custom_barcode = true;

    Context mContext;

    public SettingConfig(final Context mContext) {
        this.mContext = mContext;

        initUrls(mContext);
        initUrlsForJava(mContext);
        initMainMenus(mContext);
        initUserMenus(mContext);
        // init2(mContext);

    }

    /**
     * 初始化API URL
     *
     * @param mContext
     */
    public void initUrls(Context mContext) {

        String ip = WifiPrefUtils.getIPForJava(mContext);
        String port = WifiPrefUtils.getPortForJava(mContext);
        if (EmptyTool.isBlank(ip) || EmptyTool.isBlank(port)) {
            ip = APIUrlConfig.DEFAULT_IP;
            port = APIUrlConfig.DEFAULT_PORT;
        }
        StringBuilder baseUrlSf = new StringBuilder("http://");
        baseUrlSf.append(ip).append(":").append(port);
        String baseUrl = baseUrlSf.toString();
        BaseApi.baseUrl = baseUrl;
        urlMap.put(APIUrlConfig.AdviceService, baseUrl
                + APIUrlConfig.AdviceService);

        urlMap.put(APIUrlConfig.PDAUpdateServer, baseUrl
                + APIUrlConfig.PDAUpdateServer);

        urlMap.put(APIUrlConfig.KernelService, baseUrl
                + APIUrlConfig.KernelService);

        urlMap.put(APIUrlConfig.LifeSymptomService, baseUrl
                + APIUrlConfig.LifeSymptomService);

        urlMap.put(APIUrlConfig.InspectionService, baseUrl
                + APIUrlConfig.InspectionService);

        urlMap.put(APIUrlConfig.ExpenseService, baseUrl
                + APIUrlConfig.ExpenseService);

        urlMap.put(APIUrlConfig.AnnounceService, baseUrl
                + APIUrlConfig.AnnounceService);

        urlMap.put(APIUrlConfig.NurseService, baseUrl
                + APIUrlConfig.NurseService);

        urlMap.put(APIUrlConfig.NurseRecordService, baseUrl
                + APIUrlConfig.NurseRecordService);

        urlMap.put(APIUrlConfig.EvaluationService, baseUrl
                + APIUrlConfig.EvaluationService);

        urlMap.put(APIUrlConfig.BloodTransfusionService, baseUrl
                + APIUrlConfig.BloodTransfusionService);

        urlMap.put(APIUrlConfig.AdviceCheckService, baseUrl
                + APIUrlConfig.AdviceCheckService);

        urlMap.put(APIUrlConfig.DailyWorkService, baseUrl
                + APIUrlConfig.DailyWorkService);
        urlMap.put(APIUrlConfig.NurseFormService, baseUrl
                + APIUrlConfig.NurseFormService);

        urlMap.put(APIUrlConfig.CatheterService_Java, baseUrl
                + APIUrlConfig.CatheterService_Java);

    }

    /**
     * 初始化API URL
     *
     * @param mContext
     */
    public void initUrlsForJava(Context mContext) {

        String ip = WifiPrefUtils.getIPForJava(mContext);
        String port = WifiPrefUtils.getPortForJava(mContext);
        if (EmptyTool.isBlank(ip) || EmptyTool.isBlank(port)) {
            ip = APIUrlConfig.DEFAULT_IP;
            port = APIUrlConfig.DEFAULT_PORT;
        }
        StringBuilder baseUrlSf = new StringBuilder("http://");
        baseUrlSf.append(ip).append(":").append(port);
        String baseUrl = baseUrlSf.toString();
        BaseApi.baseUrl = baseUrl;

        urlMap.put(APIUrlConfig.UserService_Java, baseUrl
                + APIUrlConfig.UserService_Java);

        urlMap.put(APIUrlConfig.PatientService_Java, baseUrl
                + APIUrlConfig.PatientService_Java);

        urlMap.put(APIUrlConfig.LifeSignService_Java, baseUrl
                + APIUrlConfig.LifeSignService_Java);

        urlMap.put(APIUrlConfig.HealthGuidService_Java, baseUrl
                + APIUrlConfig.HealthGuidService_Java);

        urlMap.put(APIUrlConfig.NursePlanService_Java, baseUrl
                + APIUrlConfig.NursePlanService_Java);

        urlMap.put(APIUrlConfig.NurseRecordService_Java, baseUrl
                + APIUrlConfig.NurseRecordService_Java);

        urlMap.put(APIUrlConfig.BloodTransfusionService_Java, baseUrl
                + APIUrlConfig.BloodTransfusionService_Java);

        urlMap.put(APIUrlConfig.VisitService_Java, baseUrl
                + APIUrlConfig.VisitService_Java);

        urlMap.put(APIUrlConfig.DailyCareService_Java, baseUrl
                + APIUrlConfig.DailyCareService_Java);

        urlMap.put(APIUrlConfig.OutControlService_Java, baseUrl
                + APIUrlConfig.OutControlService_Java);

        urlMap.put(APIUrlConfig.EvaluationService_Java, baseUrl
                + APIUrlConfig.EvaluationService_Java);

        urlMap.put(APIUrlConfig.AdviceService_Java, baseUrl
                + APIUrlConfig.AdviceService_Java);

        urlMap.put(APIUrlConfig.BloodGlucoseService_Java, baseUrl
                + APIUrlConfig.BloodGlucoseService_Java);

        urlMap.put(APIUrlConfig.HandOver_Java, baseUrl
                + APIUrlConfig.HandOver_Java);

        urlMap.put(APIUrlConfig.PDAUpdateServer_Java, baseUrl
                + APIUrlConfig.PDAUpdateServer_Java);

        urlMap.put(APIUrlConfig.EvaluationServices_Java, baseUrl
                + APIUrlConfig.EvaluationServices_Java);

        urlMap.put(APIUrlConfig.InspectionService_Java, baseUrl
                + APIUrlConfig.InspectionService_Java);

        urlMap.put(APIUrlConfig.ExpenseService_Java, baseUrl
                + APIUrlConfig.ExpenseService_Java);

        urlMap.put(APIUrlConfig.CatheterService_Java, baseUrl
                + APIUrlConfig.CatheterService_Java);

        urlMap.put(APIUrlConfig.OffLineService, baseUrl
                + APIUrlConfig.OffLineService);

        urlMap.put(APIUrlConfig.BloodSugarService_Java, baseUrl
                + APIUrlConfig.BloodSugarService_Java);

        urlMap.put(APIUrlConfig.TradService_Java, baseUrl
                + APIUrlConfig.TradService_Java);

        urlMap.put(APIUrlConfig.SkinTestService_Java, baseUrl
                + APIUrlConfig.SkinTestService_Java);
    }

    /**
     * 初始化menu
     */
    public void initMainMenus(Context mContext) {

        boolean[] states = MainGuidePrefUtils.getsltMenus(mContext);
        if (states == null) {
            Log.e(Constant.TAG, "states is null");
            return;
        }
        String[] clazz = mContext.getResources().getStringArray(
                R.array.main_menu_class_array);
        String[] names = mContext.getResources().getStringArray(
                R.array.setting_main_menu_array);
        // TODO 加入长度是否相等判断
        mainModel.clear();
        MemuVo menu = null;
        for (int i = 0; i < states.length; i++) {
            if (states[i]) {
                menu = new MemuVo(names[i], 0, clazz[i]);
                mainModel.add(menu);
                menu = null;
            }
        }

    }

    /**
     * 初始化menu
     */
    public void initUserMenus(Context mContext) {

        boolean[] states = UserGuidePrefUtils.getsltMenus(mContext);

        if (states == null) {
            Log.e(Constant.TAG, "states is null");
            return;
        }

        String[] clazz = mContext.getResources().getStringArray(
                R.array.user_menu_class_array);
        String[] names = mContext.getResources().getStringArray(
                R.array.setting_user_menu_array);
        // TODO 加入长度是否相等判断
        userModel.clear();
        MemuVo menu = null;
        for (int i = 0; i < states.length; i++) {
            if (states[i]) {
                menu = new MemuVo(names[i], 0, clazz[i]);
                userModel.add(menu);
                menu = null;
            }
        }

    }

    /**
     * 只读取条码与扫描,临时方案
     *
     * @param mContext
     */
    // TODO
    // public void init2(Context mContext) {
    //
    // InputStream input = null;
    // try {
    // input = mContext.getAssets().open("setting.xml");
    //
    // // // 方式一：使用Android提供的实用工具类android.util.Xml
    // // XmlPullParser xpp = Xml.newPullParser();
    // // 方式二：使用工厂类XmlPullParserFactory的方式
    // XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    // XmlPullParser xpp = factory.newPullParser();
    //
    // xpp.setInput(input, "utf-8");
    //
    // int eventType = xpp.getEventType();
    // String ele = null; // Element flag
    // ArrayList<BarCode> barList = null;
    //
    // String name = null;
    // String value = null;
    //
    // while (XmlPullParser.END_DOCUMENT != eventType) {
    //
    // switch (eventType) {
    // case XmlPullParser.START_DOCUMENT:
    // barList = new ArrayList<BarCode>();
    // break;
    //
    // case XmlPullParser.START_TAG:
    // ele = xpp.getName();
    // break;
    //
    // case XmlPullParser.TEXT:
    // if (null != ele) {
    // if ("name".equals(ele)) {
    // name = xpp.getText();
    // } else if ("value".equals(ele)) {
    // value = xpp.getText();
    // }
    // }
    // break;
    //
    // case XmlPullParser.END_TAG:
    //
    // if ("barprefix".equals(xpp.getName())
    // && !EmptyTool.isBlank(name)
    // && !EmptyTool.isBlank(value)) {
    //
    // String[] vss = value.split(",");
    //
    // for (int i = 0; i < vss.length; i++) {
    // barList.add(new BarCode(Integer.parseInt(name),
    // vss[i]));
    // }
    // name = null;
    // value = null;
    // } else if ("scanmodel".equals(xpp.getName())
    // && !EmptyTool.isBlank(value)) {
    // BarCodeFactory.bar = Integer.valueOf(value);
    // name = null;
    // value = null;
    // }
    // ele = null;
    // break;
    // }
    // eventType = xpp.next();
    // }
    // } catch (Exception e) {
    // Log.e(Constant.TAG, e.getMessage(), e);
    // } finally {
    // if (input != null) {
    // try {
    // input.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // }

}
