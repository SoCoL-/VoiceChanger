package ru.mobstudio.voicechanger.httpCore;

import org.json.JSONObject;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class JSONRequest
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private JSONObject mData;
    private String mKey;

    //-----------------------------
    //Ctors
    //-----------------------------

    public JSONRequest(String key, JSONObject data)
    {
        this.mData = data;
        this.mKey = key;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public JSONObject getData() {
        return mData;
    }

    public void setData(JSONObject mData) {
        this.mData = mData;
    }

    public String getKey() { return mKey; }

    public void setKey(String mKey) { this.mKey = mKey; }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
