package com.raisongran.modularity.environment;

import android.graphics.Canvas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains:
 * - room
 * - props
 * - player
 */
public class GameWorld {
    public GameRoom room;
    private GameProp[] props;
    GamePlayer player;

    GameWorld(JSONObject data) {
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

            // - player
            player = new GamePlayer(data.getJSONObject("player"));
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    void start() {
        room.start();
        for(int i=0; i<props.length; i++ ) {
            props[i].start();
        }
        player.start();
    }

    void stop() {
        room.stop();
        for(int i=0; i<props.length; i++ ) {
            props[i].stopSound();
        }
        player.stop();
    }

    void tick(float dt) {
        room.tick(dt);
        for(int i=0; i<props.length; i++ ) {
            props[i].tick(dt);
        }
        player.tick(dt);
    }

    void drawTo(Canvas canvas) {
        room.drawTo(canvas);
        for(int i=0; i<props.length; i++ ) {
            props[i].drawTo(canvas);
        }
        player.drawTo(canvas);
    }

    void applyInput(GameTouchOverlayView.GAME_PLAYER_CONTROL_STATE controlState) {
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
