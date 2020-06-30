package com.bsoft.mob.ienr.components.tts;


import com.bsoft.mob.ienr.components.tts.speech.ISpeechSynthesizer;
import com.bsoft.mob.ienr.components.tts.speech.UniSoundSpeechSynthesizer;

/**
 * Created by Classichu on 2017/12/27.
 */

public class SpeechSynthesizerFactory {


    public static ISpeechSynthesizer getInstance() {
        return SpeechSynthesizerFactoryInner.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     */
    private static class SpeechSynthesizerFactoryInner {
        private static final ISpeechSynthesizer INSTANCE = create(UniSoundSpeechSynthesizer.class);
    }

    //=========================================
    private static <T extends ISpeechSynthesizer> T create(Class<T> tClass) {
        ISpeechSynthesizer base = null;
        try {
            base = (ISpeechSynthesizer) Class.forName(tClass.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) base;
    }
}
