package com.raisongran.modularity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

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

    public void TestButtonClick(View view) {
        gameManager.getGameWorld().room.props[0].y += 1;
        gameManager.getGameWorld().room.props[0].stopSound();
        gameManager.getGameWorld().room.props[0].playSound();
    }
}
