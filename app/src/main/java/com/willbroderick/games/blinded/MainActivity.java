package com.willbroderick.games.blinded;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init the singleton for feedback
        FeedbackManager.getInstance().init(getBaseContext());

        // Player control UI
        final GameTouchOverlayView gestureOverlayView = (GameTouchOverlayView) findViewById(R.id.gestureOverlayView);

        // create the game object
        gameManager = new GameManager(getBaseContext(), gestureOverlayView);

        // High-level UI
        final Button playLevelButton = (Button) findViewById(R.id.playLevelButton);
        playLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.loadLevel();
                gameManager.play();
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.stop();
            }
        });
    }
}
