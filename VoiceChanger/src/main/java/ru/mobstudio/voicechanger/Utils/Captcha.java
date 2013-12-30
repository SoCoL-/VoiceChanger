package ru.mobstudio.voicechanger.Utils;

/**
 * Created by Evgenij on 12.11.13.
 *
 */
public class Captcha
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private String mType = "";
    private String mTextCaptcha = "";
    private byte[] mData;

    //-----------------------------
    //Ctors
    //-----------------------------

    //-----------------------------
    //Methods
    //-----------------------------

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] mData) {
        this.mData = mData;
    }

    public String getTextCaptcha() { return mTextCaptcha; }

    public void setTextCaptcha(String mTextCaptcha) { this.mTextCaptcha = mTextCaptcha; }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
