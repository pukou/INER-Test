package com.bsoft.mob.ienr.components.tts.tool;

import android.os.Environment;

import java.io.File;

public class Constants {

    ////////////////////////////////KEY////////////////////////////////
    public static final String BAIDU_YUYIN_APP_ID = "10584306";
    public static final String BAIDU_YUYIN_API_KEY = "nwofdKQhmMDqMdQv5ihYKEuo";
    public static final String BAIDU_YUYIN_SECRET_KEY = "520129c1303fd33494f9061de601b9fd";

    public static final String UNISOUND_YUYIN_APP_KEY = "kwfxyq6nkxddqxu3w3ouxvzjys43d2ihyibj5yiv";
    public static final String UNISOUND_YUYIN_APP_SECRET = "2f935712a71fce53e49e7d371541d2eb";


    ////////////////////////////////COMM////////////////////////////////
    private static final String FILE_PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final String FILE_PATH_BSOFT = FILE_PATH_SDCARD + "bsoft" + File.separator;
    private static final String FILE_PATH_IENR = FILE_PATH_BSOFT + "ienr" + File.separator;
    private static final String FILE_PATH_TTS = FILE_PATH_IENR + "tts" + File.separator;


    ///////////////////////////////UNISOUND/////////////////////////////////
    private static final String UNISOUND_BACKEND_MODEL_FEMALE = "backend_female";
    private static final String UNISOUND_BACKEND_MODEL_LZL = "backend_lzl";//林志玲
    //
    private static final String FILE_PATH_TTS_UNISOUND = FILE_PATH_TTS + "unisound" + File.separator;
    public static final String FILE_PATH_TTS_UNISOUND_UniSoundTTSModels = FILE_PATH_TTS_UNISOUND + "UniSoundTTSModels" + File.separator;
    //////文件名
    public static final String FILE_UNISOUND_FRONTEND_MODEL = "frontend_model";
    public static final String FILE_UNISOUND_BACKEND_MODEL = UNISOUND_BACKEND_MODEL_FEMALE;//UNISOUND_BACKEND_MODEL_LZL//林志玲

    //////Assets
    public static final String ASSETS_FILE_PATH_UNISOUND_FRONTEND_MODEL = "UniSoundTTSModels" + File.separator + FILE_UNISOUND_FRONTEND_MODEL;
    public static final String ASSETS_FILE_PATH_UNISOUND_BACKEND_MODEL = "UniSoundTTSModels" + File.separator + FILE_UNISOUND_BACKEND_MODEL;
    /////////////////////////////////BAIDU//////////////////////////////
    private static final String BAIDU_SPEECH_MODEL_FEMALE = "bd_etts_speech_female.dat";
    ////
    private static final String FILE_PATH_TTS_BAIDU = FILE_PATH_TTS + "baidu" + File.separator;
    public static final String FILE_PATH_TTS_BAIDU_BaiduTTSModels = FILE_PATH_TTS_BAIDU + "BaiduTTSModels" + File.separator;
    //////文件名
    public static final String FILE_BAIDU_TEXT_MODEL = "bd_etts_text.dat";
    public static final String FILE_BAIDU_SPEECH_MODEL= BAIDU_SPEECH_MODEL_FEMALE;
    ///////Assets
    public static final String ASSETS_FILE_PATH_BAIDU_TEXT_MODEL = "BaiduTTSModels" + File.separator + FILE_BAIDU_TEXT_MODEL;
    public static final String ASSETS_FILE_PATH_BAIDU_SPEECH_MODEL = "BaiduTTSModels" + File.separator + FILE_BAIDU_SPEECH_MODEL;
}
