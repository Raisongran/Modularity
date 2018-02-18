package com.raisongran.modularity;

import android.graphics.Canvas;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Contains:
 * - room
 * - props
 * - player
 */
public class GameWorld {
    public GameRoom room;
    public GameProp[] props;
    public GamePlayer player;

    public GameWorld(JSONObject data) {
        try {
            // - room
            room = new GameRoom(data.getJSONObject("room"));
            room.buildAudioEnvironment();

            // - props
            JSONArray jsonProps = data.getJSONArray("props");
            props = new GameProp[jsonProps.length()];
            for (int i = 0; i < jsonProps.length(); i++) {
                props[i] = new GameProp(jsonProps.getJSONObject(i));
            }
            System.out.println(Arrays.toString(props));

            // - player
            player = new GamePlayer(data.getJSONObject("player"));
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void start() {
        room.start();
        for(int i=0; i<props.length; i++ ) {
            props[i].start();
        }
        player.start();
    }

    public void stop() {
        room.stop();
        for(int i=0; i<props.length; i++ ) {
            props[i].stopSound();
        }
        player.stop();
    }

    public void tick(float dt) {
        room.tick(dt);
        for(int i=0; i<props.length; i++ ) {
            props[i].tick(dt);
        }
        player.tick(dt);
    }

    public void drawTo(Canvas canvas) {
        room.drawTo(canvas);
        for(int i=0; i<props.length; i++ ) {
            props[i].drawTo(canvas);
        }
        player.drawTo(canvas);
    }

    public void applyInput(GameTouchOverlayView.GAME_PLAYER_CONTROL_STATE controlState) {
        switch (controlState) {
            case TURN_LEFT:
                player.applyTurn(true);
                break;
            case TURN_RIGHT:
                player.applyTurn(false);
                break;
            case MOVE_FORWARD:
                player.applyMove(true, room, props);
                break;
            case MOVE_BACKWARD:
                player.applyMove(false, room, props);
                break;
        }
    }
}
