package com.raisongran.modularity;

import org.json.JSONException;
import org.json.JSONObject;


public class GameBehaviour {

    public String trigger;
    public String action;
    public String parameters;

    GameBehaviour(JSONObject data) {
        try {
            trigger = data.getString("trigger");
            action = data.getString("action");
            parameters = data.getString("parameters");
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }
}