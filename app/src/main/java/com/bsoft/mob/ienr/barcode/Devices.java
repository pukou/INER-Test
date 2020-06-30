package com.bsoft.mob.ienr.barcode;

import android.os.Build;

/**
 * Created by Classichu on 2018/2/7.
 */
     /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
public class Devices {
    // ==========================================================================================================
    // ======================================  【设备标识 会展现在关于页面 和 设置页面下拉列表】 ================
    public static final String D_01_NoBar = "NoBar";//不支扫描（摄像头模式）
    public static final String D_02_ZBKBluetooth = "ZBKBluetooth";//ZBK蓝牙扫描枪
    public static final String D_03_BayNexus_G03 = "BayNexus_G03(G02/G04)";//识凌 G03  （G02 G04也用G03接口）
    public static final String D_04_Unitech_PA700 = "Unitech_PA700";//unitech PA700
    public static final String D_05_Unitech_EA600 = "Unitech_EA600";//unitech EA600
    public static final String D_06_Cilico_ALPS_JW16_05 = "Cilico_ALPS_JW16_05";//富立叶 ALPS JW16-05
    public static final String D_07_Landi_P950_V2 = "Landi_P950_V2";//联迪 P950 V2
    public static final String D_08_Moto_MC40 = "Moto_MC40";//Moto MC40
    public static final String D_09_Urovo_6200s = "Urovo_6200s";//Urovo 6200s
    public static final String D_10_Joyree_Z7 = "Joyree_Z7";//巨历 Z7
    public static final String D_11_Lachesis_SC7_Nr510 = "Lachesis_SC7_Nr510";//联新 Nr510
    // public static final String D_12_SimBar = "SimBar";//希伯姆
    public static final String D_13_IData_50_Series = "IData_50_Series";//
    // public static final String D_14_H900Bar = "H900Bar";//深圳市攀凌科技 H900
    public static final String D_15_Mx_5020 = "Mx_5020";//赫盛 5020
    public static final String D_16_EChart_M4 = "EChart_M4";//中标软件 M4
    // public static final String D_17_DTResearch = "DTResearch";//DTResearch
    // public static final String D_18_SeuicBar = "SeuicBar";//江苏东大集成
    public static final String D_19_Copipod_C568 = "Copipod_C568 ";//北京首信锐普 C568
    // public static final String D_20_EiiBar = "EiiBar";//易迈海
    public static final String D_21_Seuic_Cruise_Xmg = "Seuic_Cruise_Xmg";//东大集成小码哥
    public static final String D_22_Honeywell_EDA51 = "Honeywell_EDA51";//霍尼韦尔EDA51
    public static final String D_23_Honeywell_ALPS_KT55 = "Honeywell_ALPS_KT55";//霍尼韦尔 ALPS KT55
    public static final String D_24_Honeywell_Glory_50 = "Honeywell_Glory_50";//霍尼韦尔 Glory 50
    public static final String D_25_Neusoft_S511 = "Neusoft_S511(Camus)";//东软
    //public static final String D_26_Zpd = "D_26_Zpd";//中普达
    public static final String D_27_Lachesis_Nr510_Nr510 = "Lachesis_Nr510_Nr510";//Nr510 老款
    public static final String D_28_ChainWay_Wtk_C70 = "ChainWay_Wtk_C70";//C70, wtk
    public static final String D_29_IData_80 = "IData_80";//
    public static final String D_30_Newland_NLS_MT90 = "Newland_NLS_MT90";//
    public static final String D_31_Cilico_ALPS_Android_Handheld_Terminal = "Cilico_ALPS_Android_Handheld_Terminal";//
    public static final String D_32_Hisense_N1s = "Hisense_N1s";//
    public static final String D_33_ZDJ_G07A = "ZDJ_G07A";  //荣创(ZDJ) G07A



    /**
     * 字符串内容是固定的  不可修改 ！！！
     */
    // ==========================================================================================================
    // ======================================  【设备厂商】 =====================================================
    //!!!
    public static final String M_unitech = "unitech";
    //!!!没有厂商的硬件
    public static final String M_unknown = Build.UNKNOWN;
    //!!!
    public static final String M_cilico_alps = "alps";
    //!!!
    public static final String M_ubx = "ubx";
    //!!!
    public static final String M_landi = "landi";
    public static final String M_joyree = "joyree";
    public static final String M_neusoft = "neusoft";
    //福建协和客户化：联心PDA 厂商
    public static final String M_lachesis_sc = "sc7";
    //福建协和客户化：联心PDA 厂商 旧版
    public static final String M_lachesis_nr510 = "nr510";

    public static final String M_lachesis_lachesis = "lachesis";

    //东大集成小马哥
    public static final String M_seuic = "seuic";

    public static final String M_qualcomm = "qualcomm";
    public static final String M_honeywell = "honeywell";
    public static final String M_wtk = "wtk";
    //idata_50_series
    public static final String M_IData_android= "android";
    //idata_80
    public static final String M_IData= "idata";
    public static final String M_Newland= "newland";
    public static final String M_hisense= "hisense";
    public static final String M_ZDJ= "zdj";

    // ==========================================================================================================
    // ======================================  【设备型号】 =====================================================
    //!!!
    public static final String unitech_pa700 = "pa700";
    @Deprecated
    public static final String unitech_ea600 = "ea600";
    //!!!
    public static final String ubx_ea600 = "ea600";//包装上称为 unitech ea600
    //!!!
    public static final String baynexus_g02 = "bn-hh-g02";
    //!!!
    public static final String cilico_alps_jw16_05 = "jw16-05";
    //!!!
    public static final String landi_p950_v2 = "p950v2";
    public static final String joyree_z7 = "joyree z7";//空格
    public static final String neusoft_camus = "camus";


    //东大集成小马哥
    public static final String seuic_cruise= "cruise";
    //福建协和客户化：联心PDA 型号 新旧版通用
    public static final String lachesis_nr510 = "nr510";
    public static final String honeywell_eda51 = "eda51";
    public static final String wtk_c70 = "c70";
    public static final String idata_50_series = "50 series";
    public static final String idata_80 = "android";
    public static final String newland_nls_mt90 = "nls-mt90";
    public static final String cilico_alps_android_handheld_terminal = "android handheld terminal";
    public static final String hisense_n1s = "n1s";
    public static final String ZDJ_G07A = "g07a";
}
/* =============================================================== end */
