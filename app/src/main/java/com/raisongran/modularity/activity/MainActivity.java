package com.raisongran.modularity.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.raisongran.modularity.R;
import com.raisongran.modularity.environment.FeedbackManager;
import com.raisongran.modularity.environment.GameManager;
import com.raisongran.modularity.environment.GameTouchOverlayView;

public class MainActivity extends AppCompatActivity {

    public static GameManager gameManager;
    public static TextView tView1;
    public static TextView tCords;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tView1 = findViewById(R.id.tView1);
        tCords = findViewById(R.id.tCords);

        // init the singleton for feedback
        FeedbackManager.getInstance().init(getBaseContext());

        // Player control UI
        final GameTouchOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);
        gameManager = new GameManager(getBaseContext(), gestureOverlayView);
        handler.postDelayed(runnable, 100);
    }

    public void TestButtonClick(View view) {

    }

    public void PlayButtonClick(View view) {
        gameManager.loadLevel();
        gameManager.play();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tick();
            handler.postDelayed(runnable, 100);
        }
    };

    public void tick() {
        if (gameManager.getGameWorld() != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    float x = gameManager.getGameWorld().room.props[0].x;
                    float y = gameManager.getGameWorld().room.props[0].y;
                    float z = gameManager.getGameWorld().room.props[0].z;
                    tCords.setText(" X: " + x + "\n\r" + "Y: " + y + "\n\r" + "Z: " + z);
                    tView1.setText("angle: " + gameManager.angle);
                }
            });

        }
    }

    public void StopButtonClick(View view) {
        gameManager.stop();
    }
}
