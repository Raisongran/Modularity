package com.raisongran.modularity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameManager gameManager;
    private SensorManager mSensorManager;
    private float currentDegree = 0f;
    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

        // init the singleton for feedback
        FeedbackManager.getInstance().init(getBaseContext());

        // Player control UI
        final GameTouchOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);

        // create the game object
        gameManager = new GameManager(getBaseContext(), gestureOverlayView);

        // High-level UI
        final Button playLevelButton = findViewById(R.id.playLevelButton);
        playLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.loadLevel();
                gameManager.play();
            }
        });

        final Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.stop();
            }
        });
    }

    public void TestButtonClick(View view) {
        gameManager.getGameWorld().props[0].y += 1;
        gameManager.getGameWorld().props[0].stopSound();
        gameManager.getGameWorld().props[0].playSound();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        if (gameManager.getGameWorld() != null){
            gameManager.getGameWorld().player.direction = degree/60;
            textView.setText("Degree: " + degree + "\t\t / " + gameManager.getGameWorld().player.direction);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
