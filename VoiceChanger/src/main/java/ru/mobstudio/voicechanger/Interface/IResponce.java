package ru.mobstudio.voicechanger.Interface;

/**
 * Created by Evgenij on 12.11.13.
 *
 */
public interface IResponce
{
    public void onAnswerOk(String TAG);
    public void onAnswerError(String TAG, String message);
    public void onAnswerOther(String TAG);
}
