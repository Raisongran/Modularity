package com.raisongran.modularity.environment;

import org.json.JSONException;
import org.json.JSONObject;


class GameBehaviour {
    String trigger;
    String action;
    String properties;

    GameBehaviour(JSONObject data) {
        try {
            trigger = data.getString("trigger");
            action = data.getString("action");
            properties = data.getString("properties");
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }
}