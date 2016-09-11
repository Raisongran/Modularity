package com.willbroderick.games.blinded;

import android.util.Log;

/**
 * Created by willbo on 09/07/16.
 */
public class GameThread implements Runnable {
    private boolean doRun;
    private boolean isRunning;
    private GameManager gameManager;

    private final static int tickInterval = 30;
    private final static int slowTickInterval = 100;

    public void run() {
        isRunning = true;
        gameManager.getGameWorld().start();
        long currTime;
        long prevTime = System.currentTimeMillis() - tickInterval;
        long nextSlowTickTime = 0;
        while(doRun) {
            currTime = System.currentTimeMillis();
            gameManager.tick( 1.f / (1f * tickInterval / (currTime - prevTime) ) );
            if(currTime > nextSlowTickTime) {
                gameManager.slowTick();
                nextSlowTickTime = currTime + slowTickInterval;
            }
            prevTime = currTime;
            try {
                Thread.sleep(tickInterval);
            } catch (InterruptedException ex) {
                doRun = false;
            }
        }
        gameManager.getGameWorld().stop();
        isRunning = false;
    }

    public GameThread(GameManager inGameManager) {
        doRun = false;
        isRunning = false;
        gameManager = inGameManager;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void prepareToStart() {
        doRun = true;
    }

    public void stop() {
        doRun = false;
    }
}
