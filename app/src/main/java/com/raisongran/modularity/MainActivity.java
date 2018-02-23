package com.raisongran.modularity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameManager gameManager;
    private SensorManager mSensorManager;
    private TextView tView1;
    private TextView tStatus;
    private TextView tCords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tView1 = findViewById(R.id.tView1);
        tStatus = findViewById(R.id.tStatus);
        tCords = findViewById(R.id.tCords);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); assert mSensorManager != null;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

        // init the singleton for feedback
        FeedbackManager.getInstance().init(getBaseContext());

        // Player control UI
        final GameTouchOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);
        gameManager = new GameManager(getBaseContext(), gestureOverlayView);
    }

    public void TestButtonClick(View view) {
        gameManager.getGameWorld().room.props[0].z -= 1;
        gameManager.getGameWorld().room.props[0].stopSound();
        gameManager.getGameWorld().room.props[0].playSound();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = (float) (Math.PI * event.values[0]/360);
        if (gameManager.getGameWorld() != null){
            gameManager.getGameWorld().player.direction = degree;
            tView1.setText("Degree: " + Math.sin(degree));
            tCords.setText("x = " +  Math.round(gameManager.getGameWorld().player.x) +
                    "; y = " + Math.round(gameManager.getGameWorld().player.y) +
                    "; x = " + Math.round(gameManager.getGameWorld().player.z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public void PlayButtonClick(View view) {
        tStatus.setText("Статус: загрузка...");
        gameManager.loadLevel();
        gameManager.play();
        tStatus.setText("Статус: запущено");
    }

    public void StopButtonClick(View view) {
        tStatus.setText("Статус: остановка...");
        gameManager.stop();
        tStatus.setText("Статус: не запущено");
    }
}
