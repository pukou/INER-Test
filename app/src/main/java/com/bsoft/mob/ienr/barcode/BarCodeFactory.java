package com.bsoft.mob.ienr.barcode;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Pair;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.barcode.impl.BayNexus_G03_Impl;
import com.bsoft.mob.ienr.barcode.impl.ChainWay_Wtk_C70_Impl;
import com.bsoft.mob.ienr.barcode.impl.Cilico_ALPS_Android_Handheld_Terminal_Impl;
import com.bsoft.mob.ienr.barcode.impl.Cilico_ALPS_JW16_05_Impl;
import com.bsoft.mob.ienr.barcode.impl.Copipod_C568_Impl;
import com.bsoft.mob.ienr.barcode.impl.CustomBarcodeImpl;
import com.bsoft.mob.ienr.barcode.impl.EChart_M4_Impl;
import com.bsoft.mob.ienr.barcode.impl.Hisense_N1s_Impl;
import com.bsoft.mob.ienr.barcode.impl.Honeywell_ALPS_KT55_Impl;
import com.bsoft.mob.ienr.barcode.impl.Honeywell_EDA51_Impl;
import com.bsoft.mob.ienr.barcode.impl.Honeywell_Glory_50_Impl;
import com.bsoft.mob.ienr.barcode.impl.IData_50_Series_Impl;
import com.bsoft.mob.ienr.barcode.impl.IData_80_Impl;
import com.bsoft.mob.ienr.barcode.impl.Joyree_Z7_Impl;
import com.bsoft.mob.ienr.barcode.impl.Lachesis_Nr510_Nr510_Impl;
import com.bsoft.mob.ienr.barcode.impl.Lachesis_SC7_Nr510_Impl;
import com.bsoft.mob.ienr.barcode.impl.Landi_P950_V2_Impl;
import com.bsoft.mob.ienr.barcode.impl.Moto_MC40_Impl;
import com.bsoft.mob.ienr.barcode.impl.Mx_5020_Impl;
import com.bsoft.mob.ienr.barcode.impl.Neusoft_S511_Impl;
import com.bsoft.mob.ienr.barcode.impl.Newland_NLS_MT90_Impl;
import com.bsoft.mob.ienr.barcode.impl.NoBarCodeImpl;
import com.bsoft.mob.ienr.barcode.impl.Seuic_Cruise_Xmg_Impl;
import com.bsoft.mob.ienr.barcode.impl.Unitech_EA600_Impl;
import com.bsoft.mob.ienr.barcode.impl.Unitech_PA700_Impl;
import com.bsoft.mob.ienr.barcode.impl.Urovo_6200s_Impl;
import com.bsoft.mob.ienr.barcode.impl.ZDJ_G07A_Impl;
import com.bsoft.mob.ienr.model.PDAInfo;
import com.bsoft.mob.ienr.util.prefs.SettingUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/*升级编号【56010059】============================================= start
                 PDA 自选扫码的简单实现
            ================= classichu 2018/3/22 11:21
            */
public class BarCodeFactory {
    private static List<Pair<String, IBarCode>> mPdaPairList = new ArrayList<>();
    // ==========================================================================================================
    // ======================================  【TO_DO 修改1 start】 ============================================
    // 用户不设置 代码定义的 默认的扫码枪
    // TODO:修改1 设置 无法自动识别且 当用户自定义未自定义的情况下  使用的扫码枪
    private static final String DEFAULT_PDA_TYPE_STR = Devices.D_01_NoBar;

    // ======================================  【TO_DO 修改1 end】 ==============================================
    // ==========================================================================================================

    static {
        // ==========================================================================================================
        // ======================================  【TO_DO 修改2 start】 ============================================
        // 加 D_数字 是为了 核对的时候清晰明了
        // TODO:修改2 添加对应的扫码实现类
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_01_NoBar, new NoBarCodeImpl()));

        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_03_BayNexus_G03, new BayNexus_G03_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_04_Unitech_PA700, new Unitech_PA700_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_05_Unitech_EA600, new Unitech_EA600_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_06_Cilico_ALPS_JW16_05, new Cilico_ALPS_JW16_05_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_07_Landi_P950_V2, new Landi_P950_V2_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_08_Moto_MC40, new Moto_MC40_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_09_Urovo_6200s, new Urovo_6200s_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_10_Joyree_Z7, new Joyree_Z7_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_11_Lachesis_SC7_Nr510, new Lachesis_SC7_Nr510_Impl()));

        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_13_IData_50_Series, new IData_50_Series_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_15_Mx_5020, new Mx_5020_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_16_EChart_M4, new EChart_M4_Impl()));

        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_19_Copipod_C568, new Copipod_C568_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_21_Seuic_Cruise_Xmg, new Seuic_Cruise_Xmg_Impl()));

        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_22_Honeywell_EDA51, new Honeywell_EDA51_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_23_Honeywell_ALPS_KT55, new Honeywell_ALPS_KT55_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_24_Honeywell_Glory_50, new Honeywell_Glory_50_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_25_Neusoft_S511, new Neusoft_S511_Impl()));

        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_27_Lachesis_Nr510_Nr510, new Lachesis_Nr510_Nr510_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_28_ChainWay_Wtk_C70, new ChainWay_Wtk_C70_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_29_IData_80, new IData_80_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_30_Newland_NLS_MT90, new Newland_NLS_MT90_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_31_Cilico_ALPS_Android_Handheld_Terminal, new Cilico_ALPS_Android_Handheld_Terminal_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_32_Hisense_N1s, new Hisense_N1s_Impl()));
        //mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_33_ZDJ_G07A, new ZDJ_G07A_Impl()));

        /* mPdaPairList.add(Pair.<String, IBarCode>create(Devices.D_03_BayNexus_G03, new Moto_MC40_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create("08_UrovoBar", new Urovo_6200s_Impl()));//urovo s6200
        mPdaPairList.add(Pair.<String, IBarCode>create("09_SimBar", new SimBarcodeImpl()));//希伯姆
        mPdaPairList.add(Pair.<String, IBarCode>create("10_IDataBar", new NoBarCodeImpl()));//fixme//iData
        mPdaPairList.add(Pair.<String, IBarCode>create("11_H900Bar", new NoBarCodeImpl()));//fixme//深圳市攀凌科技 H900
        mPdaPairList.add(Pair.<String, IBarCode>create("12_Mx5020Bar", new Mx_5020_Impl()));//赫盛5020
        mPdaPairList.add(Pair.<String, IBarCode>create("13_EChartBar", new EChart_M4_Impl()));//中标软件M4
        mPdaPairList.add(Pair.<String, IBarCode>create("14_DTResearch", new DTResearchCodeImpl()));//DTResearch
        mPdaPairList.add(Pair.<String, IBarCode>create("15_SeuicBar", new NoBarCodeImpl()));//fixme//江苏东大集成
        mPdaPairList.add(Pair.<String, IBarCode>create("16_CopipodBar", new Copipod_C568_Impl()));//北京首信锐普
        mPdaPairList.add(Pair.<String, IBarCode>create("17_EiiBar", new NoBarCodeImpl()));//fixme//易迈海
        mPdaPairList.add(Pair.<String, IBarCode>create("18_ZBKBluetooth", new NoBarCodeImpl()));//fixme//ZBK蓝牙扫描枪
        mPdaPairList.add(Pair.<String, IBarCode>create("19_SeuicXmgBar", new NoBarCodeImpl()));//fixme//东大集成小码哥
        mPdaPairList.add(Pair.<String, IBarCode>create("20_Cilico_ALPS_JW1605Bar", new Cilico_ALPS_JW16_05_Impl()));
        mPdaPairList.add(Pair.<String, IBarCode>create("21_Landi_P950", new NoBarCodeImpl()));//fixme 联迪
        mPdaPairList.add(Pair.<String, IBarCode>create("22_Unitech_EA600", new Unitech_EA600_Impl()));//unitech EA600
        mPdaPairList.add(Pair.<String, IBarCode>create("23_Joyree_Z7", new JoyreeBarcodeImpl()));//深圳巨历  JOYREE Z7
        mPdaPairList.add(Pair.<String, IBarCode>create("24_Neu_S511", new Neusoft_S511_Impl()));//东软S511*/

        // ======================================  【TO_DO 修改2 end】 ==============================================
        // ==========================================================================================================
    }


    /**
     * 读取代码识别
     */
    // ==========================================================================================================
    // ======================================  【TO_DO 修改3 start】 ============================================
    // TODO:修改3添加对应的扫码识别逻辑
    private static String readPdaTypeStrAuto() {
        String pdaType = null;
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        switch (manufacturer.toLowerCase().trim()) {
            case Devices.M_unitech:
                if (Devices.unitech_pa700.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_04_Unitech_PA700;
                }
                break;
            case Devices.M_ubx:
                if (Devices.ubx_ea600.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_05_Unitech_EA600;
                }
                break;
            case Devices.M_unknown:
                if (Devices.baynexus_g02.equals(model.toLowerCase().trim())) {
                    //###！！！ G02 使用G03接口  pdaType = Devices.D_03_BayNexus_G03;
                    pdaType = Devices.D_03_BayNexus_G03;
                }
                break;
            case Devices.M_qualcomm:
                if (Devices.baynexus_g02.equals(model.toLowerCase().trim())) {
                    //###！！！ G04 使用G03接口  pdaType = Devices.D_03_BayNexus_G03;
                    pdaType = Devices.D_03_BayNexus_G03;
                }
                break;
            case "baybar":
                //fixme baybar G03待确认
                if ("G03".equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_03_BayNexus_G03;
                }
                break;
            case Devices.M_cilico_alps:
                if (Devices.cilico_alps_jw16_05.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_06_Cilico_ALPS_JW16_05;
                } else if (Devices.cilico_alps_android_handheld_terminal.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_31_Cilico_ALPS_Android_Handheld_Terminal;
                }
                break;
            case Devices.M_landi:
                if (Devices.landi_p950_v2.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_07_Landi_P950_V2;
                }
                break;
            case Devices.M_joyree:
                if (Devices.joyree_z7.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_10_Joyree_Z7;
                }
                break;
            case Devices.M_lachesis_sc:
            case "lachesis":
                if (Devices.lachesis_nr510.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_11_Lachesis_SC7_Nr510;
                }
                break;
            //联新 旧版
            case Devices.M_lachesis_nr510:
                if (Devices.lachesis_nr510.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_27_Lachesis_Nr510_Nr510;
                }
                break;
            case Devices.M_seuic:
                if (Devices.seuic_cruise.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_21_Seuic_Cruise_Xmg;
                }
                break;
            case Devices.M_neusoft:
                if ("issac".equals(model.toLowerCase().trim()) || Devices.neusoft_camus.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_25_Neusoft_S511;
                }
                break;
            case Devices.M_honeywell:
                if (Devices.honeywell_eda51.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_22_Honeywell_EDA51;
                }
                break;
            case Devices.M_wtk://ChainWay C70
                if (Devices.wtk_c70.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_28_ChainWay_Wtk_C70;
                }
                break;
            case Devices.M_IData_android://
                if (Devices.idata_50_series.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_13_IData_50_Series;
                }
                break;
            case Devices.M_IData://
                if (Devices.idata_80.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_29_IData_80;
                }
                break;
            case Devices.M_Newland://
                if (Devices.newland_nls_mt90.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_30_Newland_NLS_MT90;
                }
                break;
            case Devices.M_hisense://
                if (Devices.hisense_n1s.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_32_Hisense_N1s;
                }
                break;
            /*case Devices.M_ZDJ://
                if (Devices.ZDJ_G07A.equals(model.toLowerCase().trim())) {
                    pdaType = Devices.D_33_ZDJ_G07A;
                }
                break;*/
            default:
        }

        return pdaType;
    }
    // ======================================  【TO_DO 修改3 end】 ==============================================
    // ==========================================================================================================


    private static String pdaTypePosToStr(int pdaTypePos) {
        if (pdaTypePos < 0 || mPdaPairList.isEmpty()) {
            return null;
        }
        return mPdaPairList.get(pdaTypePos).first;
    }

    private static int pdaTypeStrToPos(String pdaTypeStr) {
        for (int i = 0; i < mPdaPairList.size(); i++) {
            if (pdaTypeStr.equals(mPdaPairList.get(i).first)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * @param pda_type_pos
     */
    public static void savePDATypePos_SharedPrefe(int pda_type_pos) {
        String pda_type = null;
        if (pda_type_pos >= 0) {
            //下拉选择的情况下
            pda_type = pdaTypePosToStr(pda_type_pos);
        }
        //用户已选择
        if (pda_type != null) {
            //保存信息
            savePDATypeStr_SharedPrefe(pda_type);
        }
    }

    public static void savePDATypeStr_SharedPrefe(String pda_type) {
        //save
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        SharedPreferences preferences = AppApplication.getInstance().getSharedPreferences(SettingUtils.SETTING_PREF, Context.MODE_PRIVATE);
        preferences.edit().putString(SP_PDA_TYPE_ + model.trim() + manufacturer.trim(), pda_type).apply();
    }

    private static String SP_PDA_TYPE_ = "SP_PDA_TYPE_";

    //加载入口
    private static String loadPDATypeStr_SharedPrefe() {
        //加载用户设置数据
        String model = Build.MODEL;//型号
        String manufacturer = Build.MANUFACTURER;//硬件厂商
        SharedPreferences preferences = AppApplication.getInstance().getSharedPreferences(SettingUtils.SETTING_PREF, Context.MODE_PRIVATE);
        String pda_type = preferences.getString(SP_PDA_TYPE_ + model.trim() + manufacturer.trim(), null);
        //未设置
        if (pda_type == null) {
            //自动获取
            pda_type = readPdaTypeStrAuto();
        }
        //为空 返回默认
        return pda_type != null ? pda_type : DEFAULT_PDA_TYPE_STR;
    }

    public static int loadPDATypePos_SharedPrefe() {
        return pdaTypeStrToPos(loadPDATypeStr_SharedPrefe());
    }

    /**
     * ====================================================================================
     */
    public static List<Pair<String, IBarCode>> getPdaPairList() {
        return mPdaPairList;
    }

    /**
     * 获取扫码 标识
     *
     * @return
     */
    public static String getBarCodeStr() {
        return loadPDATypeStr_SharedPrefe();
    }


    /**
     * 获取扫码实现类
     *
     * @return
     */
    public static IBarCode getBarCode() {
        //如果自定义扫描头参数开启,并且后台有Action存在,侧调用自定义扫描头接口
        AppApplication app = AppApplication.getInstance();
        if(SettingUtils.isCustomBarcode(app.getApplicationContext())) {
            if (app.pdaInfo != null) {
                if (!StringUtils.isEmpty(app.pdaInfo.ACTION)) {
                    return new CustomBarcodeImpl(app.pdaInfo);
                }
            }
        }

        int pdaTypePos = loadPDATypePos_SharedPrefe();
        if (pdaTypePos < 0) {
            throw new RuntimeException("Please check PDA IBarCode implements class again");
        }
        return mPdaPairList.get(pdaTypePos).second;
    }


}
/* =============================================================== end */

