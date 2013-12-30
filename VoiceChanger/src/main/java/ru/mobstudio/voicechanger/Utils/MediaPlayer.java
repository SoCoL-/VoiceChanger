package ru.mobstudio.voicechanger.Utils;

import android.content.Context;

import ru.mobstudio.voicechanger.R;

/**
 * Created by Evgenij on 14.11.13.
 *
 */
public class MediaPlayer
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private android.media.MediaPlayer mMediaPlayer;
    private final Context mContext;
    private android.media.MediaPlayer.OnCompletionListener mListener;

    //-----------------------------
    //Ctors
    //-----------------------------

    public MediaPlayer(Context c)
    {
        mContext = c;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    public void resetPlayer()
    {
        if(mMediaPlayer == null)
            return;

        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void setCopleteInterface(android.media.MediaPlayer.OnCompletionListener ocl)
    {
        Debug.i("Setup Listener to player");
        //mMediaPlayer.setOnCompletionListener(ocl);
        mListener = ocl;
        Debug.i("Setup Listener to player done");
    }

    public void stopPlayer()
    {
        if(mMediaPlayer == null)
            return;

        mMediaPlayer.stop();
        if(mListener != null)
            mListener.onCompletion(mMediaPlayer);
    }

    public boolean isPlay()
    {
        if(mMediaPlayer == null || !mMediaPlayer.isPlaying())
            return false;

        return true;
    }

    public void PlayMusic(int index)
    {
        resetPlayer();
        mMediaPlayer = android.media.MediaPlayer.create(mContext, index);
        if(mListener != null)
            mMediaPlayer.setOnCompletionListener(mListener);
        mMediaPlayer.start();
    }

    public void destroyMedia()
    {
        if(mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = null;
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
