package com.raisongran.modularity.environment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.vr.sdk.audio.GvrAudioEngine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains:
 * - 4 walls
 * - ceiling
 * - floor
 * - props anchored to the room (e.g. a chandelier)
 */
public class GameRoom {
    private float width;
    private float depth;
    private float height;
    private boolean enclosed;
    public GameProp[] props;

    GameRoom(JSONObject data) {
        try {
            width = (float) data.getInt("x");
            depth = (float) data.getInt("y");
            height = (float) data.getInt("z");
            enclosed = data.getBoolean("enclosed");
            JSONArray propsData = data.getJSONArray("props");
            props = new GameProp[propsData.length()];
            for (int i = 0; i < propsData.length(); i++) {
                props[i] = new GameProp(propsData.getJSONObject(i));
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    float getWidth() {
        return width;
    }

    float getDepth() {
        return height;
    }

    void buildAudioEnvironment() {
        GvrAudioEngine engine = FeedbackManager.getInstance().getAudioEngine();
        engine.setRoomProperties(
                width,
                depth,
                height,
                GvrAudioEngine.MaterialName.TRANSPARENT,
                GvrAudioEngine.MaterialName.TRANSPARENT,
                GvrAudioEngine.MaterialName.GRASS);
        engine.enableRoom(enclosed);
    }

    void start() {
        for (int i = 0; i < props.length; i++) {
            props[i].start();
        }
    }

    void tick(float dt) {
        for (int i = 0; i < props.length; i++) {
            props[i].tick(dt);
        }
    }

    void stop() {
        for (int i = 0; i < props.length; i++) {
            props[i].stopSound();
        }
    }

    void drawTo(Canvas canvas) {
        // centralise canvas on room
        canvas.translate(0.2f, 0.2f);
        float scale = canvas.getWidth() / width;
        canvas.scale(scale, scale);
        // draw room boundary
        Paint paint = new Paint();
        paint.setStrokeWidth(0.05f);
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, 1.6f, 1.6f, paint);
        // draw props
        for (int i = 0; i < props.length; i++) {
            props[i].drawTo(canvas);
        }
    }
}
