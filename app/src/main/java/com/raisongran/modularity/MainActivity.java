package com.raisongran.modularity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.raisongran.modularity.processing.LocalMath;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameManager gameManager;
    private SensorManager mSensorManager;
    private TextView tView1;
    private TextView tStatus;
    private TextView tCords;
    private static double alpha = 0.06f;

    LocalMath.SmoothFilter smoothFilter1 = new LocalMath.SmoothFilter();
    LocalMath.SmoothFilter smoothFilter2 = new LocalMath.SmoothFilter();

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

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (gameManager.getGameWorld() != null) {
            double raw = event.values[0];

            double angle = 2 * Math.PI * raw / 360;
            float r = gameManager.getGameWorld().room.props[0].distance;
            float shiftX = gameManager.getGameWorld().player.x;
            float shiftY = gameManager.getGameWorld().player.y;
            float x = (float) (r * smoothFilter1.Get(Math.sin(angle), alpha) + shiftX);
            float y = (float) (r * smoothFilter2.Get(Math.cos(angle), alpha) + shiftY);
            float z = gameManager.getGameWorld().player.z;
            gameManager.getGameWorld().room.props[0].setPosition(x, y, z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public void PlayButtonClick(View view) {
        tStatus.setText("Статус: загрузка...");
        gameManager.loadLevel();
        gameManager.play();
        gameManager.getGameWorld().room.props[0].distance = 30;
        tStatus.setText("Статус: запущено");
    }

    public void StopButtonClick(View view) {
        tStatus.setText("Статус: остановка...");
        gameManager.stop();
        tStatus.setText("Статус: не запущено");
    }
}
