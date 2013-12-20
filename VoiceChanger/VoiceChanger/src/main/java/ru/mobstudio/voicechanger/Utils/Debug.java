package ru.mobstudio.voicechanger.Utils;

import android.util.Log;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class Debug
{
    //-----------------------------
    //Constants
    //-----------------------------

    private final static String TAG = "VoiceChanger";

    //-----------------------------
    //Variables
    //-----------------------------

    //-----------------------------
    //Ctors
    //-----------------------------

    //-----------------------------
    //Methods
    //-----------------------------

    public static void i(String TAG, String mess)
    {
        Log.i(TAG, mess);
    }

    public static void i(String mess)
    {
        Log.i(TAG, mess);
    }

    public static void e(String TAG, String mess)
    {
        Log.e(TAG, mess);
    }

    public static void e(String mess)
    {
        Log.e(TAG, mess);
    }

    public static void e(Exception e)
    {
        Log.e(TAG, e.toString());
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
