package com.example.qydemo0.utils;

import android.content.Context;

import com.example.qydemo0.QYpack.KqwSpeechSynthesizer;

public class SoundTipUtil {
    private static KqwSpeechSynthesizer kqwSpeechSynthesizer;

    public static void soundTip(Context context,String text) {
        kqwSpeechSynthesizer = new KqwSpeechSynthesizer(context);
        kqwSpeechSynthesizer.start(text);
    }

}
