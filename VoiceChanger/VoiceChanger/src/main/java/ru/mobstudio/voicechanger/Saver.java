package ru.mobstudio.voicechanger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Evgenij on 13.11.13.
 *
 */
public class Saver
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private Context mContext;
    private SharedPreferences preferences;

    //-----------------------------
    //Ctors
    //-----------------------------

    public Saver(Context c)
    {
        this.mContext = c;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    //-----------------------------
    //Methods
    //-----------------------------

    public void saveMSISDN(String msisdn)
    {
        SharedPreferences.Editor edit= preferences.edit();

        edit.putString(Settings.TAG_SAVE_MSISDN, msisdn);
        edit.commit();
    }

    public void saveSecret(String secret)
    {
        SharedPreferences.Editor edit= preferences.edit();

        edit.putString(Settings.TAG_SAVE_SECRET, secret);
        edit.commit();
    }

    public void saveCountries(String country)
    {
        SharedPreferences.Editor edit= preferences.edit();
        edit.putString(Settings.TAG_SAVE_COUNTRIES, country);
        edit.commit();
    }

    public void saveTime(long time)
    {
        SharedPreferences.Editor edit= preferences.edit();

        edit.putLong(Settings.TAG_SAVE_TIME, time);
        edit.commit();
    }

    public void saveStateWork()
    {
        SharedPreferences.Editor edit= preferences.edit();
        edit.putBoolean(Settings.TAG_SAVE_WORKSMS, Settings.isSmsWork);
        edit.putBoolean(Settings.TAG_SAVE_WORKFULL, Settings.isFullWork);
        edit.commit();
    }

    public void saveBuyData(String signature, String json)
    {
        SharedPreferences.Editor edit= preferences.edit();
        edit.putString(Settings.TAG_SAVE_SIGNATURE, Settings.mSignature);
        edit.putString(Settings.TAG_SAVE_JSON, Settings.mJSON);
        edit.commit();
    }

    public void saveHelpState()
    {
        SharedPreferences.Editor edit= preferences.edit();
        edit.putInt(Settings.TAG_SAVE_HELP, Settings.mCurrMethod);
        edit.putBoolean(Settings.TAG_SAVE_HELP_SMS, Settings.isHelpCallSMS);
        edit.putBoolean(Settings.TAG_SAVE_HELP_CALL, Settings.isHelpCall);
        edit.commit();
    }

    public void loadAll()
    {
        Settings.mSecret = preferences.getString(Settings.TAG_SAVE_SECRET, "");
        Settings.mMSISDN = preferences.getString(Settings.TAG_SAVE_MSISDN, "");
        Settings.mTimeUpdate = preferences.getLong(Settings.TAG_SAVE_TIME, 0l);
        Settings.mCountryList = preferences.getString(Settings.TAG_SAVE_COUNTRIES, "");
        Settings.isSmsWork = preferences.getBoolean(Settings.TAG_SAVE_WORKSMS, false);
        Settings.isFullWork = preferences.getBoolean(Settings.TAG_SAVE_WORKFULL, false);
        Settings.mSignature = preferences.getString(Settings.TAG_SAVE_SIGNATURE, "");
        Settings.mJSON = preferences.getString(Settings.TAG_SAVE_JSON, "");
        Settings.mCurrMethod = preferences.getInt(Settings.TAG_SAVE_HELP, 0);
        Settings.isHelpCallSMS = preferences.getBoolean(Settings.TAG_SAVE_HELP_SMS, false);
        Settings.isHelpCall = preferences.getBoolean(Settings.TAG_SAVE_HELP_CALL, false);
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
