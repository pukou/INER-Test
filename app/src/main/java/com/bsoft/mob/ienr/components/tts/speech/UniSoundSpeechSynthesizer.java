package com.bsoft.mob.ienr.components.tts.speech;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bsoft.mob.ienr.components.tts.tool.AssetManagerTool;
import com.bsoft.mob.ienr.components.tts.tool.Constants;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by Classichu on 2017/12/27.
 */

public class UniSoundSpeechSynthesizer implements ISpeechSynthesizer {
    private static final String TAG = "UniSoundSpeech";
    private SpeechSynthesizer mTTSPlayer;
    private Context mContext;
    private String mFrontendModelPath;
    private String mBackendModelPath;
    private int code;
    private boolean isInited;
    private static final boolean DEBUG = false;

    @Override
    public void init(Context context) {
        mContext = context.getApplicationContext();
        initModels();
    }


    private void initModels() {
        mFrontendModelPath = Constants.FILE_PATH_TTS_UNISOUND_UniSoundTTSModels + Constants.FILE_UNISOUND_FRONTEND_MODEL;
        mBackendModelPath = Constants.FILE_PATH_TTS_UNISOUND_UniSoundTTSModels + Constants.FILE_UNISOUND_BACKEND_MODEL;
        File frontendModelFile = new File(mFrontendModelPath);
        File backendModelFile = new File(mBackendModelPath);
        if (frontendModelFile.exists() && backendModelFile.exists()) {
            Log.i(TAG, "离线模型文件已存在");
            initTts();
        } else {
            Executors.newFixedThreadPool(5).execute(new Runnable() {
                @Override
                public void run() {
                    String file_frontend = AssetManagerTool.copyAssetsFile(mContext, Constants.FILE_PATH_TTS_UNISOUND_UniSoundTTSModels, Constants.FILE_UNISOUND_FRONTEND_MODEL
                            , Constants.ASSETS_FILE_PATH_UNISOUND_FRONTEND_MODEL);
                    String file_backend = AssetManagerTool.copyAssetsFile(mContext, Constants.FILE_PATH_TTS_UNISOUND_UniSoundTTSModels, Constants.FILE_UNISOUND_BACKEND_MODEL
                            , Constants.ASSETS_FILE_PATH_UNISOUND_BACKEND_MODEL);
                    if (file_frontend != null && file_backend != null) {
                        initTts();
                    }
                }
            });
          /*  new Thread(new Runnable() {
                @Override
                public void run() {
                    String file_frontend = AssetManagerTool.copyAssetsFile(mContext, Constants.FILE_PATH_TTS_UNISOUND_UniSoundTTSModels, Constants.FILE_UNISOUND_FRONTEND_MODEL
                            , Constants.ASSETS_FILE_PATH_UNISOUND_FRONTEND_MODEL);
                    String file_backend = AssetManagerTool.copyAssetsFile(mContext, Constants.FILE_PATH_TTS_UNISOUND_UniSoundTTSModels, Constants.FILE_UNISOUND_BACKEND_MODEL
                            , Constants.ASSETS_FILE_PATH_UNISOUND_BACKEND_MODEL);
                    if (file_frontend != null && file_backend != null) {
                        initTts();
                    }
                }
            }).start();*/
        }
    }

    private void initTts() {
        // 初始化语音合成对象
        mTTSPlayer = new SpeechSynthesizer(mContext, Constants.UNISOUND_YUYIN_APP_KEY, Constants.UNISOUND_YUYIN_APP_SECRET);
        // 设置本地合成
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);//TTS_SERVICE_MODE_NET
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModelPath);// 设置前端模型
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModelPath);// 设置后端模型
        //
        //mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, 0);//语速 范围 0 ~ 100
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_PITCH, 40);//音高 范围 0 ~ 100 大而尖锐 普通女生推荐 40  林志玲推荐 todo
         mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, 80);//音量 范围 0 ~ 100

        // mTTSPlayer.setOption(SpeechConstants.TTS_KEY_SAMPLE_RATE, 16 * 1000);//合成码率 例如：16 * 1000
        // mTTSPlayer.setOption(SpeechConstants.TTS_KEY_STREAM_TYPE, AudioManager.STREAM_MUSIC);//合成采样率 例如：AudioManager.STREAM_MUSIC
        //  mTTSPlayer.setOption(SpeechConstants.TTS_KEY_PLAY_START_BUFFER_TIME, 10);//设置播放开始缓冲时间 0 ~ 500 单位
        //mTTSPlayer.setOption(SpeechConstants.TTS_KEY_IS_READ_ENLISH_IN_PINYIN, true);//设置是否将英文按拼音读 如：wang->王
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONT_SILENCE, 10);//语音开始段的静音时长 0 ~ 1000 单位 ms
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACK_SILENCE, 100);//语音结尾段的静音时长 0 ~ 1000 单位 ms
        // 设置回调监听
        mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        log_i("onInitFinish");
                        isInited = true;
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        // 开始合成回调
                        log_i("beginSynthesizer");
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        log_i("endSynthesizer");
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        log_i("beginBuffer");
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        log_i("bufferReady");
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
                        log_i("onPlayBegin");
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        // 播放完成回调
                        log_i("onPlayEnd");
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        log_i("pause");
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        log_i("resume");
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        log_i("stop");
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调
                        log_i("release");
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onError(int type, String errorMSG) {
                // 语音合成错误回调
                log_e("onError" + errorMSG);
                toastMessage(errorMSG);
            }
        });
        // 初始化合成引擎
        try {
            code = mTTSPlayer.init(null);
            log_i("code:" + code);
        } catch (Exception e) {
            log_i("Exception:" + e.getMessage());
        }

    }


    @Override
    public void speak(String text) {
        if (!isInited) {
            toastMessage("尚未初始化完成，请稍后重试！");
            return;
        }
        code = mTTSPlayer.playText(text);
        log_i("code:" + code);
    }

    @Override
    public void speak(int textResid) {
        if (!isInited) {
            toastMessage("尚未初始化完成，请稍后重试！");
            return;
        }
        code = mTTSPlayer.playText(mContext.getString(textResid));
        log_i("code:" + code);
    }

    @Override
    public void batchSpeak(List<String> texts) {
        if (!isInited) {
            toastMessage("尚未初始化完成，请稍后重试！");
            return;
        }
        for (String text : texts) {
            code = mTTSPlayer.playText(text);
            log_i("code:" + code);
        }
    }

    @Override
    public void synthesize(String text) {
        mTTSPlayer.synthesizeText(text);
    }

    @Override
    public void pause() {
        if (mTTSPlayer != null) {
            mTTSPlayer.pause();
        }
    }

    @Override
    public void resume() {
        if (mTTSPlayer != null) {
            mTTSPlayer.resume();
        }
    }

    @Override
    public void stop() {
        if (mTTSPlayer != null) {
            mTTSPlayer.stop();
        }
    }

    @Override
    public void cancel() {
        if (mTTSPlayer != null) {
            code = mTTSPlayer.cancel();
            log_i("code:" + code);
        }
    }

    @Override
    public void release() {
        // 主动释放离线引擎
        if (mTTSPlayer != null) {
            code = mTTSPlayer.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
            log_i("code:" + code);
        }
    }

    @Override
    public void destroy() {
        if (mTTSPlayer != null) {
            code = mTTSPlayer.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
            log_i("code:" + code);
            mTTSPlayer = null;
        }
    }


    //==============================================================================================
    private void toastMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private void log_e(String msg) {
        Log.e(TAG, msg);
    }

    private void log_i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }
}
