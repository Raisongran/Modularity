package com.raisongran.modularity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GameProp {
    private String name;
    private float x;
    private float y;
    private float z;
    private float direction;
    public float distance; // as radius
    private GameBehaviour[] behaviours;
    private GameSoundSource sound = null;

    public GameProp(JSONObject data) {
        try {
            name = data.getString("name");
            x = data.getInt("x");
            y = data.getInt("y");
            z = data.getInt("z");
            JSONArray behaviourData = data.getJSONArray("behaviours");
            behaviours = new GameBehaviour[behaviourData.length()];
            for (int i = 0; i < behaviourData.length(); i++) {
                behaviours[i] = new GameBehaviour(behaviourData.getJSONObject(i));
            }
            if (data.has("sound")) {
                sound = new GameSoundSource(data.getJSONObject("sound"));
            } else {
                sound = null;
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void setPosition(float nx, float ny, float nz) {
        x = nx; y = ny; z = nz;
        moveSound();
    }

    public void playSound() {
        if(sound != null) {
            sound.playAt(x, y, z);
        }
    }

    private void moveSound() {
        if(sound != null) {
             sound.moveTo(x, y, z);
        }
    }

    public void start() {
        for (int i = 0; i < behaviours.length; i++) {
            behaviours[i].startWith(this);
        }
    }

    public void tick(float dt) {
        for (int i = 0; i < behaviours.length; i++) {
            behaviours[i].tickWith(dt, this);
        }
    }

    public void stopSound() {
        if(sound != null) {
            sound.stop();
        }
    }

    public void drawTo(Canvas canvas) {
        canvas.translate(x, y);
        // draw self
        Paint paint = new Paint();
        paint.setStrokeWidth(3f);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(0, 0, 4, paint);
        // draw associated
        for (int i = 0; i < behaviours.length; i++) {
            behaviours[i].drawTo(canvas, x, y);
        }
        canvas.translate(-x, -y);
    }
}
