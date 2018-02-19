package com.raisongran.modularity;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import static com.google.vr.cardboard.ThreadUtils.runOnUiThread;

/**
 * Contains:
 * - world
 * - start/stop/management
 */
public class GameManager {
    private GameThread gameThread;
    private GameWorld gameWorld;
    private GameTouchOverlayView inputController;
    private Context context;

    public GameManager(Context inContext, GameTouchOverlayView inInputController) {
        context = inContext;
        gameThread = new GameThread(this);
        inputController = inInputController;
        inputController.init(this);
    }

    public void loadLevel() {
        // halt current game
        stop();
        // unload current game world
        gameWorld = null;
        // fetch data for new level
        gameWorld = new GameWorld(loadJSONFromAsset("level1.json"));
    }

    public void play() {
        // if not already playing...
        if(!gameThread.isRunning()) {
            // get ready...
            gameThread.prepareToStart();
            // go!
            new Thread(gameThread).start();
        }
    }

    public void stop() {
        gameThread.stop();
    }

    public void tick(float dt) {
        // live game update
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
}
