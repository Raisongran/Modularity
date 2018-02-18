package com.raisongran.modularity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GameBehaviour {
    private enum GAME_BEHAVIOUR_TRIGGERS {
        ONSTART,
        ONTICK
    }
    private enum GAME_BEHAVIOUR_ACTIONS {
        PLAY_SOUND,
        FOLLOW_PATH
    }

    private GAME_BEHAVIOUR_TRIGGERS trigger;
    private GAME_BEHAVIOUR_ACTIONS action;
    private float progress;
    private float[] pathX;
    private float[] pathY;
    private float[] pathZ;
    private float[] pathS;
    private float[] pathLength;
    private float totalPathLength;

    public GameBehaviour(JSONObject data) {
        try {
            trigger = GAME_BEHAVIOUR_TRIGGERS.valueOf(data.getString("when").toUpperCase());
            action = GAME_BEHAVIOUR_ACTIONS.valueOf(data.getString("do").toUpperCase());
            progress = 0;
            if(action == GAME_BEHAVIOUR_ACTIONS.FOLLOW_PATH) {
                JSONArray path = data.getJSONArray("path");
                pathX = new float[path.length()];
                pathY = new float[path.length()];
                pathZ = new float[path.length()];
                pathS = new float[path.length()];
                pathLength = new float[path.length()];
                for(int i=0; i<path.length(); i++) {
                    JSONObject vertex = path.getJSONObject(i);
                    pathX[i] = (float) vertex.getDouble("x");
                    pathY[i] = (float) vertex.getDouble("y");
                    pathZ[i] = (float) vertex.getDouble("z");
                    pathS[i] = (float) vertex.getDouble("s");
                    if(i > 0) {
                        pathLength[i-1] = (float) Math.sqrt(
                                Math.pow(pathX[i] - pathX[i-1], 2) + Math.pow(pathY[i] - pathY[i-1], 2) + Math.pow(pathZ[i] - pathZ[i-1], 2)
                        );
                    }
                }
                pathLength[path.length()-1] = (float) Math.sqrt(
                        Math.pow(pathX[0] - pathX[path.length()-1], 2) + Math.pow(pathY[0] - pathY[path.length()-1], 2) + Math.pow(pathZ[0] - pathZ[path.length()-1], 2)
                );
                totalPathLength = 0;
                for(int i=0; i<pathLength.length; i++) {
                    totalPathLength += pathLength[i];
                }
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void startWith(GameProp prop) {
        if(trigger == GAME_BEHAVIOUR_TRIGGERS.ONSTART) {
            performActionOn(prop);
        }
    }

    public void tickWith(float dt, GameProp prop) {
        if(trigger == GAME_BEHAVIOUR_TRIGGERS.ONTICK) {
            switch (action) {
                case FOLLOW_PATH:
                    float x = 0, y = 0, z = 0, distance = 0;
                    for(int i=0; i<pathX.length; i++) {
                        // which path section are we in?
                        distance += pathLength[i];
                        if(progress < distance) {
                            float dist = (progress - (distance - pathLength[i])) / pathLength[i];
                            int nextVertex = (i+1) % pathX.length;
                            x = pathX[i] + dist * (pathX[nextVertex] - pathX[i]);
                            y = pathY[i] + dist * (pathY[nextVertex] - pathY[i]);
                            z = pathZ[i] + dist * (pathZ[nextVertex] - pathZ[i]);
                            progress += dt * pathS[i];
                            if(progress > totalPathLength) {
                                progress -= totalPathLength;
                            }
                            break;
                        }
                    }
                    prop.setPosition(x, y, z);
                    break;
                default:
                    break;
            }
        }
    }

    public void drawTo(Canvas canvas, float propX, float propY) {
        //TODO: move all paint objects into feedback manager, create once
        switch (action) {
            case FOLLOW_PATH:
                Paint paint = new Paint();
                paint.setStrokeWidth(1f);
                paint.setColor(Color.rgb(20,170,70));
                paint.setTextSize(8);
                canvas.translate(-propX, -propY);
                for(int i=0; i<pathX.length; i++) {
                    int nextVertex = (i+1) % pathX.length;
                    canvas.drawLine(pathX[i], pathY[i], pathX[nextVertex], pathY[nextVertex], paint);
                    canvas.drawText(""+(i+1), pathX[i], pathY[i], paint);
                }
                canvas.translate(propX, propY);
                break;
            default:
                break;
        }
    }

    private void performActionOn(GameProp prop) {
        switch (action) {
            case PLAY_SOUND:
                prop.playSound();
                break;
            default:
                break;
        }
    }
}