package com.raisongran.modularity;

import android.content.Context;
import android.os.Vibrator;
import com.google.vr.sdk.audio.GvrAudioEngine;


public class FeedbackManager {
    // singleton
    private static FeedbackManager instance = null;
    public static FeedbackManager getInstance() {
        if(instance == null) {
            instance = new FeedbackManager();
        }
        return instance;
    }
    protected FeedbackManager() {
    }

    private Vibrator vibrator;
    private GvrAudioEngine cardboardAudioEngine;

    public void init(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        cardboardAudioEngine =
                new GvrAudioEngine(context, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
    }

    public void vibrate(long duration) {
        vibrator.vibrate(duration); // in ms, e.g. 50 for a little bump
    }

    public GvrAudioEngine getAudioEngine() {
        return cardboardAudioEngine;
    }
}
