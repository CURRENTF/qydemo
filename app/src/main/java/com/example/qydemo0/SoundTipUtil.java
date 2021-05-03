package com.example.qydemo0;

import android.content.Context;

public class SoundTipUtil {
    private static KqwSpeechSynthesizer kqwSpeechSynthesizer;

    public static void soundTip(Context context,String text) {
        kqwSpeechSynthesizer = new KqwSpeechSynthesizer(context);
        kqwSpeechSynthesizer.start(text);
    }

}
