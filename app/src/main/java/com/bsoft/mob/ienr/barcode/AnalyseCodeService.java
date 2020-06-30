package com.bsoft.mob.ienr.barcode;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.LoginActivity;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.VibratorUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析条码扫描结果，并广播扫描结果 Created by hy on 14-3-7.
 */
public class AnalyseCodeService extends IntentService {

    /**
     * 扫描条码字符串 EXTRA KEY,VALUE 是String类型
     */
    public static final String SCAN_RESULT_EXTRA = "com.bsoft.mob.ienr.AnalyseCodeService.SCAN_RESULT_EXTRA";

    /**
     * 扫描类型EXTRA KEY。VALUE值：0 条码二维码 ；1 rfid
     */
    public static final String SCAN_TYPE = "com.bsoft.mob.ienr.AnalyseCodeService.SCAN_TYPE";

    // private SoundPool sp = null;
    // private SparseIntArray soundMap = new SparseIntArray();

    private Handler handler_ = new Handler();

    private MediaPlayer mCurrentMediaPlayer;

    public AnalyseCodeService() {
        super(AnalyseCodeService.class.getName());
    }

    public AnalyseCodeService(String name) {
        super(AnalyseCodeService.class.getName());
    }

    AppApplication mAppApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mAppApplication = (AppApplication) getApplication();
        // sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
        // soundMap.put(1, sp.load(this, R.raw.sound_key, 1));
        // soundMap.put(2, sp.load(this, R.raw.sound_ok, 1));
        // soundMap.put(3, sp.load(this, R.raw.wrong, 1));
    }

    /**
     * 异步线程执行
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        String scanResult = intent.getStringExtra(SCAN_RESULT_EXTRA);
        int scanType = intent.getIntExtra(SCAN_TYPE, 0);
        playSound(R.raw.sound_key);
        ReturnMo result = getBarcodeResult(scanResult, scanType);
        //add 2019-1-8 17:17:04
        if (scanResult.contains("bedCode")&&scanResult.contains("Qrcode")){
            //数字病房认证条码
            authDwSystem(scanResult);
            return;
        }
        //add
        parserBarcode(result, scanType);

    }

    private void authDwSystem(String qrcode) {


    }

    /**
     * 根据扫描结果，连接后台，获取条码对应的业务信息
     *
     * @param scanResult 条码扫描结果
     * @param scanType
     * @return
     */

    private ReturnMo getBarcodeResult(String scanResult, int scanType) {

        if (EmptyTool.isBlank(scanResult)) {
            return null;
        }
        // 去除有些机型，在条码的空格、回车、换行符、制表符
        scanResult = scanResult.trim();
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(scanResult);
        scanResult = m.replaceAll("");
        try {

            ReturnMo mo = new ReturnMo();

            // RFID做特殊处理，先获取RFID前缀
            if (scanType == 1) {
                scanResult = getRfidPre(scanResult);
            }

            BarcodeEntity bcEntity = getCodeType(scanResult);
            if (bcEntity == null) {// 请求失败，例如网络请求失败等
                return null;
            }
            // 病人条码
            if (bcEntity.TMFL == 1 || bcEntity.TMFL == 4 || bcEntity.TMFL == 5) {
                mo.result = getPersonInfo(bcEntity.TMNR, bcEntity.TMQZ);
            } else if (bcEntity.TMFL == 3) {
                if (((AppApplication) getApplication()).user != null) {
                    if (((AppApplication) getApplication()).isChangeUser) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setAction("com.bsoft.mob.ienr.SwitchLogin");
                        intent.putExtra("extra_barcode", bcEntity.source);
                        startActivity(intent);
                    } else {
                        mo.result = getPersonInfo(bcEntity.TMNR, bcEntity.TMQZ);
                    }
                }
            }

            mo.bcEntity = bcEntity;
            return mo;
        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取RFID条码前缀
     *
     * @param scanResult 条码原码
     * @return 失败返回原码，成功返回前缀+原码
     */
    private String getRfidPre(String scanResult) {


        String jgid = mAppApplication.jgId;
        Response<String> response = UserApi.getInstance(getApplicationContext())
                .GetBarcodeSettings(jgid);
        if (response != null && response.ReType == 0) {
            String tmqz = response.Data;
            if (tmqz != null) {
                return tmqz + scanResult;
            }
        }
        return scanResult;
    }

    /**
     * 获取条码类型
     *
     * @param source
     * @return 失败返回NULL，如果解析结果未返回，返回默认生成的实例
     */
    public BarcodeEntity getCodeType(String source) {


        String jgid = mAppApplication.jgId;
        if (EmptyTool.isBlank(jgid)) {
            jgid = "-1";
        }
        Response<Object> response = UserApi.getInstance(getApplicationContext())
                .GetBarcodeInfo(source, jgid);
        if (response == null || response.ReType != 0) {
            return null;
        }

        BarcodeEntity entity = (BarcodeEntity) response.Data;
        if (entity == null) {
            entity = new BarcodeEntity();
        }
        entity.source = source;
        return entity;
    }

    /**
     * 根据条码值和前缀获取病人信息
     *
     * @param value
     * @param preStr
     * @return
     */
    public Response<Object> getPersonInfo(String value, String preStr) {

        // 访问网络获取类型

        String jgid = mAppApplication.jgId;
        Response<Object> result = PatientApi.getInstance(getApplicationContext())
                .GetPatientForScan(value, preStr, jgid);
        return result;
    }

    /**
     * 解析条码结果
     *
     * @param mo
     * @param scanType
     */
    private void parserBarcode(final ReturnMo mo, int scanType) {


        boolean vib = mAppApplication.getSettingConfig().vib;
        if (null == mo || mo.bcEntity == null) {
            VibratorUtil.vibrator(mAppApplication,vib);
            showToast("扫描失败");
            playSound(R.raw.wrong);
            return;
        }

        BarcodeEntity bcEntity = mo.bcEntity;

        // RFID支持 start01
        if (scanType == 1 && bcEntity.TMFL == 5 && (mo.result == null || mo.result.ReType != 0)) {

            Intent intent = new Intent(BarcodeActions.RFID_Get);
            intent.putExtra("barinfo", mo.bcEntity);
            sendBroadcast(intent);
            // playAudio(2);
            playSound(R.raw.sound_ok);
            return;
        }
        // 病人信息特殊处理
        if (bcEntity.TMFL == 1 || bcEntity.TMFL == 4 || bcEntity.TMFL == 5) {

            if (mo.result == null) {
                VibratorUtil.vibrator(mAppApplication,vib);
                showToast("请求失败：查找病人信息失败");
                playSound(R.raw.wrong);
                return;
            }

            if (mo.result.ReType != 0) {
                handler_.post(new Runnable() {

                    @Override
                    public void run() {
                        showToast(mo.result.Msg);
                    }
                });
                playSound(R.raw.wrong);
                return;
            }
        }
        // RFID支持 end01

        if (bcEntity.TMFL == 1 || bcEntity.TMFL == 4 || bcEntity.TMFL == 5) {
            SickPersonVo sickPersonVo = (SickPersonVo) mo.result.Data;
            if (null != sickPersonVo) {
                mAppApplication.sickPersonVo = sickPersonVo;
                Intent intent = new Intent(BarcodeActions.Refresh);
                sendBroadcast(intent);
                playSound(R.raw.sound_ok);
            } else {
                VibratorUtil.vibrator(mAppApplication,vib);
                showToast("无法查找到病人信息");
                playSound(R.raw.wrong);
            }

        } else {
            Intent intent = new Intent(BarcodeActions.Bar_Get);
            intent.putExtra("barinfo", bcEntity);
            sendBroadcast(intent);
            playSound(R.raw.sound_ok);
        }
    }

    public static class ReturnMo {

        public BarcodeEntity bcEntity;
        public Response<Object> result;
    }

    /**
     * 播放soundpool缓冲的音频
     *
     * @param key
     *            soundMap key value
     */
    // public void playAudio(int key) {
    //
    // if (soundMap.get(key) != 0) {
    // sp.play(soundMap.get(key), 1, 1, 0, 0, 1);
    // }
    //
    // }

    /**
     * 在UI 线程 中调用Toast show函数
     *
     * @param msg show message
     */
    public void showToast(final String msg) {

        handler_.post(new Runnable() {

            @Override
            public void run() {
                try {
                    BSToast.showToast(getApplicationContext(), msg,
                            BSToast.LENGTH_SHORT);
                } catch (RuntimeException e) {
                    Log.e(Constant.TAG,
                            "A runtime exception was thrown while executing code in a runnable",
                            e);
                }
            }

        });
    }

    private void playSound(int resId) {
        // Stop current player, if there's one playing
        if (null != mCurrentMediaPlayer) {
            mCurrentMediaPlayer.stop();
            mCurrentMediaPlayer.release();
        }

        mCurrentMediaPlayer = MediaPlayer.create(this, resId);
        if (null != mCurrentMediaPlayer) {
            mCurrentMediaPlayer.start();
        }
    }

}
