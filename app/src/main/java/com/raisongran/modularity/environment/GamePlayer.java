package com.raisongran.modularity.environment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.vr.sdk.audio.GvrAudioEngine;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains:
 * - position
 * - orientation
 * - velocity
 * - time since last footstep
 */
public class GamePlayer {
    private static final float TURN_SPEED = 0.02f;
    private static final float MOVE_SPEED = 0.15f;
    float x;
    float y;
    float z;
    private float direction; // as a bearing

    GamePlayer(JSONObject data) {
        try {
            JSONObject positionData = data.getJSONObject("position");
            x = positionData.getInt("x");
            y = positionData.getInt("y");
            z = positionData.getInt("z");
            direction = data.getInt("direction");
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    void applyTurn(boolean isLeft) {
        if(isLeft) {
            direction -= TURN_SPEED;
        } else {
            direction += TURN_SPEED;
        }
        // direction control is executing by a compass
        //sanitiseDirection();
    }

    void applyMove(boolean isForward, GameRoom room, GameProp[] props) {
        float newX, newY;
        if (isForward) {
            newX = x + (float)Math.cos(direction) * MOVE_SPEED;
            newY = y + (float)Math.sin(direction) * MOVE_SPEED;
        } else {
            newX = x - (float)Math.cos(direction) * MOVE_SPEED;
            newY = y - (float)Math.sin(direction) * MOVE_SPEED;
        }
        boolean doMove = true;
        // collision with room boundary
        if (newX < 0 || newX > room.getWidth() || newY < 0 || newY > room.getDepth()) {
            doMove = false;
            FeedbackManager.getInstance().vibrate(10);
        }
        //TODO: interaction with props
        if (doMove) {
            x = newX;
            y = newY;
        }
    }

    private void sanitiseDirection() {
        float twoPI = 2f * (float) Math.PI;
        if(direction > twoPI) {
            direction -= twoPI;
        } else if(direction < 0) {
            direction += twoPI;
        }
    }

    void start() {}

    void tick(float dt) {
        // set position in audio renderer
        GvrAudioEngine engine = FeedbackManager.getInstance().getAudioEngine();
        engine.setHeadPosition(x, y, z);
        // set orientation (as quaternion about Z)
        engine.setHeadRotation(0f, 0f, (float) Math.sin(direction), 0.5f);
        engine.update();
    }

    void stop() {}

    void drawTo(Canvas canvas) {
        canvas.translate(0.8f, 0.8f);
        Paint paint = new Paint();
        paint.setStrokeWidth(0.08f);
        paint.setColor(Color.RED);
        canvas.drawCircle(0, 0, 0.15f, paint);
        //float degrees = direction * 180f / (float)Math.PI + playerRotationShift;
        float playerRotationShift = 271;
        float degrees = (float) ((direction * 360) / Math.PI + playerRotationShift);
        canvas.rotate(degrees);
        canvas.drawLine(0f, 0f, 0.5f, 0f, paint);
        canvas.rotate(-degrees);
        canvas.drawCircle(x-0.8f, y-0.8f, 0.8f, paint);
        canvas.drawLine(x-0.8f, y-0.8f, x-0.8f, y-0.25f, paint);
    }
}
