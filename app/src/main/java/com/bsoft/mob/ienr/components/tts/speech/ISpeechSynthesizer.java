package com.bsoft.mob.ienr.components.tts.speech;

import android.content.Context;

import java.util.List;

/**
 * Created by Classichu on 2017/12/27.
 */

public interface ISpeechSynthesizer {
    void init(Context context);

    void speak(String text);

    void speak(int textResid);

    void batchSpeak(List<String> texts);

    void synthesize(String text);

    void pause();

    void resume();

    void stop();

    void cancel();

    void release();

    void destroy();
}
