package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 播放声音辅助类
 */
@Deprecated
public class MediaUtil {

    MediaPlayer mCurrentMediaPlayer;
    private static final ExecutorService es = Executors.newSingleThreadExecutor();

    //Context mContext;

    private static MediaUtil _instance;

    public static final byte[] obj = new byte[0];

    private MediaUtil() {

    }

    public static MediaUtil getInstance(Context mContext) {

        if (_instance == null) {
            synchronized (obj) {
                if (_instance == null) {
                    _instance = new MediaUtil();
                }
            }
        }
        return _instance;
    }

    public synchronized void playSound(int resId, Context mContext) {

        // Stop current player, if there's one playing
        if (null != mCurrentMediaPlayer) {
            mCurrentMediaPlayer.stop();
            mCurrentMediaPlayer.release();
        }
        mCurrentMediaPlayer = MediaPlayer.create(mContext, resId);
        if (null != mCurrentMediaPlayer) {
            es.submit(new Runnable() {

                @Override
                public void run() {
                    mCurrentMediaPlayer.start();
                }
            });
        }
    }

}
