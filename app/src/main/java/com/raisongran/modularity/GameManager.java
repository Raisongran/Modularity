package com.raisongran.modularity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Contains:
 * - world
 * - start/stop/management
 */
public class GameManager implements SensorEventListener {
    private GameThread gameThread;
    private GameWorld gameWorld;
    private GameTouchOverlayView inputController;
    private Context context;
    public double angle;

    public GameManager(Context mContext, GameTouchOverlayView inInputController) {
        this.context = mContext;
        gameThread = new GameThread(this);
        inputController = inInputController;
        inputController.init(this);

        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE); assert mSensorManager != null;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    public void loadLevel() {
        stop();
        gameWorld = null;
        gameWorld = new GameWorld(loadJSONFromAsset("level1.json"));
    }

    public void play() {
        if (!gameThread.isRunning()) {
            gameThread.prepareToStart();
            new Thread(gameThread).start();
        }
    }

    public void stop() {
        gameThread.stop();
    }

    public void tick(float dt) {
        gameWorld.tick(dt);
        inputController.tick(dt);
    }

    public void slowTick() {
        // update minor UI, etc
        inputController.slowTick();
    }

    private JSONObject loadJSONFromAsset(String filename) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            return new JSONObject(json);

        } catch(JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.getGameWorld() != null) {
            double raw = event.values[0];
            angle = 2 * Math.PI * raw / 360;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
