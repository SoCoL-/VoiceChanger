package ru.mobstudio.voicechanger.httpCore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Debug;

/**
 * Created by Evgenij on 15.11.13.
 *
 */
public class SMSReceiver extends BroadcastReceiver
{
    //-----------------------------
    //Constants
    //-----------------------------

    private final String SMS_INTENT = "android.provider.Telephony.SMS_RECEIVED";

    //-----------------------------
    //Variables
    //-----------------------------

    //-----------------------------
    //Ctors
    //-----------------------------

    //-----------------------------
    //Methods
    //-----------------------------

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent == null)
            return;

        Bundle bundle = intent.getExtras();

        if(bundle == null || intent.getAction() == null)
            return;

        if(intent.getAction().equals(SMS_INTENT))
        {
            Debug.i("Получили смску");
            SmsMessage[] sms;
            String TextSMS = "";

            Object[] pdus = (Object[]) bundle.get("pdus");
            if(pdus == null)
                return;

            Debug.i("Длинна пдусов этих = " + pdus.length);
            sms = new SmsMessage[pdus.length];

            for(int i = 0; i < pdus.length; i++)
            {
                sms[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            }
            Debug.i("Адрес смски = " + sms[0].getOriginatingAddress());

            for(SmsMessage sm : sms)
            {
                if(sm.getMessageBody() != null)
                    TextSMS += sm.getMessageBody();
            }
            String pass = parseSMS(TextSMS);

            if(pass.length() > 0)
            {
                Intent i = new Intent();
                i.setAction(Settings.RECEIVE_SMS);
                i.putExtra(Settings.TAG_STORE_SMS, pass);
                context.sendBroadcast(i);
            }
        }
    }

    private String parseSMS(String text)
    {
        Debug.i("Начинаем парсить смску....");
        text = text.toLowerCase();
        Debug.i("Текст смски = " + text);
        String pass = "";

        if(text.contains("voicechanger"))
        {
            int from;

            from = text.indexOf(":") + 2;

            if(from != -1)
            {
                pass = text.substring(from, text.length());
                Debug.i("Вырезаем пароль полностью: " + pass);
            }
        }
        Debug.i("Возвращаем пароль");

        return pass;
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
