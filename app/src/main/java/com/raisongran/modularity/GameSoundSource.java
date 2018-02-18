package com.raisongran.modularity;

import android.util.Log;

import com.google.vr.sdk.audio.GvrAudioEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Contains:
 * - sound file
 * - repeat or one-off
 */
public class GameSoundSource {
    private String file;
    private ArrayList<Integer> soundIds;
    private boolean repeat;

    public GameSoundSource(JSONObject data) {
        try {
            file = data.getString("file");
            FeedbackManager.getInstance().getAudioEngine().preloadSoundFile(file);
            repeat = data.getBoolean("repeat");
            soundIds = new ArrayList<>();
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void playAt(float x, float y, float z) {
        GvrAudioEngine engine = FeedbackManager.getInstance().getAudioEngine();
        int soundId = engine.createSoundObject(file);
        if (soundId != GvrAudioEngine.INVALID_ID) {
            engine.setSoundObjectPosition(soundId, x, y, z);
            engine.playSound(soundId, repeat);
            soundIds.add(soundId);
        } else {
            Log.d("Audio", "Sound not loaded");
        }
    }

    public void stop() {
        if(soundIds.size() > 0) {
            GvrAudioEngine engine = FeedbackManager.getInstance().getAudioEngine();
            while(soundIds.size() > 0) {
                int soundId = soundIds.remove(0);
                if (engine.isSoundPlaying(soundId)) {
                    engine.stopSound(soundId);
                }
            }

        }
    }
}
