package com.willbroderick.games.blinded;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * For recognising gestures:
 * - down + move + hold = walk or turn, depending on direction
 * - tap to interact
 */
public class GameTouchOverlayView extends View {
    private enum GAME_INPUT_STATE {
        WAITING,
        DETECTING,
        IN_ACTION
    }

    public enum GAME_PLAYER_CONTROL_STATE {
        IDLE,
        TURN_LEFT,
        TURN_RIGHT,
        MOVE_FORWARD,
        MOVE_BACKWARD
    }

    private static final double MOVE_RECOGNITION_THRESHOLD = 100f;

    private GameManager gameManager;
    private GAME_INPUT_STATE inputState;
    private GAME_PLAYER_CONTROL_STATE controlState;
    private float dragStartX;
    private float dragStartY;
    private GameWorld gameWorldToDraw;

    public GameTouchOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameWorldToDraw = null;
    }

    // must be called before any ticks occur
    public void init(GameManager inGameManager) {
        gameManager = inGameManager;
        inputState = GAME_INPUT_STATE.WAITING;
        controlState = GAME_PLAYER_CONTROL_STATE.IDLE;
    }

    public void tick(float dt) {
        if(inputState == GAME_INPUT_STATE.IN_ACTION) {
            gameManager.getGameWorld().applyInput(controlState);
        }
    }

    public void slowTick() {
        this.postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // do stuff
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            inputState = GAME_INPUT_STATE.DETECTING;
            dragStartX = event.getX();
            dragStartY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // detect direction, if distant enough
            if(inputState == GAME_INPUT_STATE.DETECTING) {
                float deltaX = event.getX() - dragStartX;
                float deltaY = event.getY() - dragStartY;
                double deltaHyp = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
                if(deltaHyp > MOVE_RECOGNITION_THRESHOLD) {
                    inputState = GAME_INPUT_STATE.IN_ACTION;
                    if(deltaX*deltaX > deltaY*deltaY) {
                        if(deltaX > 0) {
                            controlState = GAME_PLAYER_CONTROL_STATE.TURN_RIGHT;
                        } else {
                            controlState = GAME_PLAYER_CONTROL_STATE.TURN_LEFT;
                        }
                    } else {
                        if(deltaY > 0) {
                            controlState = GAME_PLAYER_CONTROL_STATE.MOVE_BACKWARD;
                        } else {
                            controlState = GAME_PLAYER_CONTROL_STATE.MOVE_FORWARD;
                        }
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            inputState = GAME_INPUT_STATE.WAITING;
            controlState = GAME_PLAYER_CONTROL_STATE.IDLE;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(gameManager != null && gameManager.getGameWorld() != null) {
            // ui debug
            if(inputState == GAME_INPUT_STATE.DETECTING) {
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                canvas.drawCircle(dragStartX, dragStartY, (float) MOVE_RECOGNITION_THRESHOLD, paint);
            }

            gameManager.getGameWorld().drawTo(canvas);
        }
    }
}
