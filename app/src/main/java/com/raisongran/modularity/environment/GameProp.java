package com.raisongran.modularity.environment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;

import com.raisongran.modularity.activity.MainActivity;
import com.raisongran.modularity.processing.LocalMath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.TimeoutException;


public class GameProp {
    private String name;
    public float x, y, z; // current cords
    private GameBehaviour[] behaviours;
    private GameSoundSource sound = null;
    private JSONObject propJson;
    private Handler handler = new Handler();

    private double _x, _y, _z; // last iteration cords
    private int _angle = 0;

    public GameProp(JSONObject mPropJson) {
        propJson = mPropJson;
        try {
            name = propJson.getString("name");
            x = propJson.getInt("x");
            y = propJson.getInt("y");
            z = propJson.getInt("z");
            JSONArray behaviourData = propJson.getJSONArray("behaviours");
            behaviours = new GameBehaviour[behaviourData.length()];
            for (int i = 0; i < behaviourData.length(); i++) {
                behaviours[i] = new GameBehaviour(behaviourData.getJSONObject(i));
            }
            if (propJson.has("sound")) {
                sound = new GameSoundSource(propJson.getJSONObject("sound"));
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
        for (int i=0; i<behaviours.length; i++) {
            if (Objects.equals(behaviours[i].trigger, "onstart")) {
                switch (behaviours[i].action) {
                    case "play_sound":
                        playSound();
                        break;
                    case "move_around":
                        moveAround(behaviours[i].parameters);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void tick(float dt) {
        for (int i=0; i<behaviours.length; i++) {
            if (MainActivity.gameManager.getGameWorld().player != null) {
                if (Objects.equals(behaviours[i].trigger, "ontick")) {
                    switch (behaviours[i].action) {
                        case "follow_compass":
                            followCompass(behaviours[i].parameters);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void followCompass(String parametersJson) {
        float r = 20;
        double angleBias = 0f;
        try {
            JSONObject param = new JSONObject(parametersJson);
            r = (float) param.getInt("distance");
            angleBias = (float) param.getInt("angleBias");
        } catch (JSONException e) {
            e.printStackTrace();
            MainActivity.gameManager.stop();
        }
        float biasX = MainActivity.gameManager.getGameWorld().player.x;
        float biasY = MainActivity.gameManager.getGameWorld().player.y;
        float biasZ = MainActivity.gameManager.getGameWorld().player.z;
        double alpha = 0.07f;
        double angle = MainActivity.gameManager.angle + angleBias;
        _x = LocalMath.SmoothFilter.Get(Math.sin(angle), alpha, _x);
        _y = LocalMath.SmoothFilter.Get(Math.cos(angle), alpha, _y);
        _z = LocalMath.SmoothFilter.Get(Math.cos(angle)/6, alpha, _z);
        float x = (float) (r * _x + biasX);
        float y = (float) (r * _y + biasY);
        float z = (float) (r * _z + biasZ);
        this.setPosition(x, y, z);
    }

    private void moveAround(String parametersJson) {
        float speed = 3f;
        float angleBias = 0f;
        float r = 20;
        try {
            JSONObject param = new JSONObject(parametersJson);
            speed = (float) param.getInt("speed");
            angleBias = (float) param.getInt("angleBias");
            r = (float) param.getInt("distance");
        } catch (JSONException e) {
            e.printStackTrace();
            MainActivity.gameManager.stop();
        }

        handler.postDelayed(runnable, 100);

        float progressMax = 360 + angleBias;
        float angle = _angle + angleBias;
        float biasX = MainActivity.gameManager.getGameWorld().player.x;
        float biasY = MainActivity.gameManager.getGameWorld().player.y;
        float biasZ = MainActivity.gameManager.getGameWorld().player.z;
        float x = (float) (r * Math.sin(angle) + biasX);
        float y = (float) (r * Math.cos(angle) + biasY);

        this.setPosition(x, y, biasZ);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(runnable, 100);
        }
    };

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
        canvas.translate(-x, -y);
    }
}
