package ru.mobstudio.voicechanger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class ApplicationChanger extends Application
{
    //-----------------------------
    //Constants
    //-----------------------------

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
    public void onCreate()
    {
        //Проверка оператора
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (networkOperator != null && networkOperator.length() >= 3)
        {
            int mcc = Integer.parseInt(networkOperator.substring(0, 3));
            //int mnc = Integer.parseInt(networkOperator.substring(3));

            Settings.isRussianMSISDN = mcc == 250;
        }
        else
            Settings.isRussianMSISDN = false;

        //Проверка наличия интернета до запуска Activity
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE );
        final android.net.NetworkInfo wifi =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi == null || mobile == null)
        {
            Settings.isConnectInternet = false;
            super.onCreate();
        }

        Settings.isConnectInternet = wifi.getState() == NetworkInfo.State.CONNECTED || mobile.getState() == NetworkInfo.State.CONNECTED;

        Settings.isRusLocale = Locale.getDefault().getLanguage().equals("ru");

        super.onCreate();
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
