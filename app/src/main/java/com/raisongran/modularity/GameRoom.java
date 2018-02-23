package com.raisongran.modularity;

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

    public GameRoom(JSONObject data) {
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

    public float getWidth() {
        return width;
    }

    public float getDepth() {
        return height;
    }

    public void buildAudioEnvironment() {
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

    public void start() {
        for (int i = 0; i < props.length; i++) {
            props[i].start();
        }
    }

    public void tick(float dt) {
        for (int i = 0; i < props.length; i++) {
            props[i].tick(dt);
        }
    }

    public void stop() {
        for (int i = 0; i < props.length; i++) {
            props[i].stopSound();
        }
    }

    public void drawTo(Canvas canvas) {
        // centralise canvas on room
        canvas.translate(2, 2);
        float scale = canvas.getWidth() / width;
        canvas.scale(scale, scale);
        // draw room boundary
        Paint paint = new Paint();
        paint.setStrokeWidth(0.5f);
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, 16, 16, paint);
        // draw props
        for (int i = 0; i < props.length; i++) {
            props[i].drawTo(canvas);
        }
    }
}
