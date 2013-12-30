package ru.mobstudio.voicechanger.httpCore;

import org.json.JSONObject;

/**
 * Created by Evgenij on 12.11.13.
 *
 */
public class Responce
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private String mKey;
    private JSONObject mData;
    private boolean isError;
    private String mMessage;
    private int mTypeError;

    //-----------------------------
    //Ctors
    //-----------------------------

    public Responce(String key,boolean iserror, int typeError, String mess, JSONObject o)
    {
        this.mKey = key;
        this.mData = o;
        this.isError = iserror;
        this.mMessage = mess;
        this.mTypeError = typeError;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public JSONObject getData() { return mData; }

    public void setData(JSONObject mData) { this.mData = mData; }

    public boolean isError() { return isError; }

    public void setError(boolean isError) { this.isError = isError; }

    public String getMessage() { return mMessage; }

    public void setMessage(String mMessage) { this.mMessage = mMessage; }

    public int getTypeError() { return mTypeError; }

    public void setTypeError(int mTypeError) { this.mTypeError = mTypeError; }

    public String getKey() { return mKey; }

    public void setKey(String mKey) { this.mKey = mKey; }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
