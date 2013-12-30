package ru.mobstudio.voicechanger.httpCore;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Saver;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Captcha;
import ru.mobstudio.voicechanger.Utils.CountryInfo;
import ru.mobstudio.voicechanger.Utils.Debug;
import ru.mobstudio.voicechanger.Utils.MediaPlayer;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class AppService extends Service implements OnFinishTask
{
    //-----------------------------
    //Constants
    //-----------------------------

    private final static String ANSW_CAPTCHA = "Captcha";
    private final static String ANSW_DATA = "Data";
    private final static String ANSW_TYPE = "Type";
    private final static String ANSW_STATUS = "Status";
    private final static String ANSW_AUTH = "Auth";
    private final static String ANSW_MESSAGE = "Message";
    private final static String ANSW_SECRET = "Secret";
    private final static String ANSW_BALANCE = "Balance";
    private final static String ANSW_CALL_ID = "CallId";
    private final static String ANSW_MSISDNS = "MSISDNs";
    private final static String ANSW_DATE = "Date";
    private final static String ANSW_LIST = "List";
    private final static String ANSW_NAME = "name";
    private final static String ANSW_PREFIX = "prefix";
    private final static String ANSW_EVENT = "Event";
    private final static String ANSW_LENGTH = "Length";
    private final static String ANSW_VERSIONS = "versions";

    public final static String REQ_MSISDN = "Msisdn";
    public final static String REQ_CAPTCHA_CODE = "CaptchaCode";
    public final static String REQ_STORE = "Store";
    public final static String REQ_SMS_CODE = "SmsCode";
    public final static String REQ_MSISDN_A = "msisdnA";
    public final static String REQ_MSISDN_B = "msisdnB";
    public final static String REQ_VOICE_ID = "voiceId";
    public final static String REQ_PURCHASE_DATA = "purchaseData";
    public final static String REQ_RESPONCE_DATA = "responseData";
    public final static String REQ_SIGNATURE = "signature";
    public final static String REQ_CALL_ID = "CallId";
    public final static String REQ_LOCALE  = "Locale";

    private final static String TYPE_OK = "0";
    private final static String TYPE_TRUE = "true";
    private final static String TYPE_FALSE = "false";
    //private final static String TYPE_FAIL = "fail";
    //private final static String TYPE_REQUIRED = "required";

    //-----------------------------
    //Variables
    //-----------------------------

    private final IBinder mBinder = new BindService();
    private IResponce mIResponce;

    public Captcha mCaptcha;
    private Saver mSaver;
    public MediaPlayer mPlayer;

    //-----------------------------
    //Ctors
    //-----------------------------


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Debug.i("Service: onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate()
    {
        mCaptcha = new Captcha();
        mSaver = new Saver(this);
        mSaver.loadAll();
        mPlayer = new MediaPlayer(this);

        parseCountries();

        if(Settings.mUserID.length() == 0)
            getDevID();
    }

    @Override
    public void onDestroy()
    {
        mPlayer.destroyMedia();
        super.onDestroy();
    }


    //-----------------------------
    //Methods
    //-----------------------------

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public class BindService extends Binder
    {
        public AppService getService()
        {
            return AppService.this;
        }
    }

    private void parseCountries()
    {
        try
        {
            JSONArray objCountries = new JSONArray(Settings.mCountryList);
            for(int i = 0; i < objCountries.length(); i++)
            {
                JSONObject objCountry = objCountries.getJSONObject(i);
                CountryInfo ci = new CountryInfo();
                ci.setNameCountry(getString(objCountry, ANSW_NAME));
                ci.setPrefix(getString(objCountry, ANSW_PREFIX));
                Settings.mCountryInfo.add(ci);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void getDevID()
    {
        AccountManager accountManager = AccountManager.get(getApplicationContext());

        try
        {
            Account[] accounts = accountManager.getAccountsByType("com.google");

            for (Account a: accounts)
            {
                if (a.name.contains("@gmail.com"))
                {
                    Settings.mUserID = a.name;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            Log.e("TAG", e.toString());
        }
    }

    public void addRequest(JSONRequest r)
    {
        HttpSocket task = new HttpSocket(r, this, this);
        task.execute(0);
    }

    private void parseError(String status, String TAG, String mess)
    {
        String message = getString(R.string.Error_server_service_not_support);
        if(status.equals("-1"))
        {
            message = getString(R.string.Error_server_service_not_support);
        }
        else if(status.equals("23"))
        {
            if(mess.contains("CaptchaCode"))
                message = getString(R.string.Error_server_23_captcha);
            else if(mess.contains("SmsCode"))
                message = getString(R.string.Error_server_23_sms);
        }
        else if(status.equals("40"))
        {
            if(mess.contains("Banned"))
            {
                TAG = "Banned";
                message = getString(R.string.Error_server_40);
                Settings.isBanned = true;
            }
        }
        else if(status.equals("41"))
        {
            message = getString(R.string.Error_server_41);
        }
        else if(status.equals("42"))
        {
            message = getString(R.string.Error_server_42);
        }
        else if(status.equals("43"))
        {
            message = getString(R.string.Error_server_43);
        }
        else if(status.equals("46"))
        {
            message = getString(R.string.Error_server_46);
        }
        else if(status.equals("47"))
        {
            message = getString(R.string.Error_server_47);
        }
        else if (status.equals("48"))
        {
            message = getString(R.string.Error_server_47);
        }
        else if(status.equals("60"))
        {
            message = getString(R.string.Error_server_60);
        }
        else if(status.equals("63"))
        {
            message = getString(R.string.Error_server_63);
        }
        else
        {
            message = getString(R.string.Error_server_service_not_support);
        }

        mIResponce.onAnswerError(TAG, message);
    }

    @Override
    public void finishTask(Responce r)
    {
        if(r == null || (r.getKey() == null) || r.getKey().length() == 0)
            return;

        if(mIResponce == null)
            return;

        String key = r.getKey();

        if(key.equals(Settings.CHECK_AUTH))
        {
            if(!r.isError())
            {
                JSONObject captchaObject = r.getData();
                String status = getString(captchaObject, ANSW_STATUS);
                String auth = getString(captchaObject, ANSW_AUTH);

                if(status.equals(TYPE_OK) && auth.equals(TYPE_TRUE))
                {
                    Settings.isBanned = false;
                    mIResponce.onAnswerOk(r.getKey());
                }
                else if(status.equals(TYPE_OK) && auth.equals(TYPE_FALSE))
                {
                    getCaptcha(captchaObject);
                    mIResponce.onAnswerError(r.getKey(), getString(captchaObject, ANSW_MESSAGE));
                }
                else if(!status.equals(TYPE_OK))
                {
                   // getCaptcha(captchaObject);
                    //mIResponce.onAnswerError(r.getKey(), getString(captchaObject, ANSW_MESSAGE));
                    parseError(status, r.getKey(), "");
                }
                /*else if(status.equals(TYPE_REQUIRED))
                {
                    //Тут надо заполнить капчу данными
                    getCaptcha(captchaObject);
                    mIResponce.onAnswerOther(r.getKey());
                }*/
            }
            else if(r.isError())
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.REQUEST_AUTH))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        //Settings.mSecret = getString(obj, ANSW_SECRET);
                        mSaver.saveSecret(Settings.mSecret);

                        getCaptcha(obj);
                        mIResponce.onAnswerOk(r.getKey());//Временно, надо убрать
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        getCaptcha(obj);
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.CONFIRM_AUTH))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mSecret = getString(obj, ANSW_SECRET);
                        mSaver.saveSecret(Settings.mSecret);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        /*else if(key.equals(Settings.CREATE_USER_ID))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mSecret = getString(obj, ANSW_SECRET);
                        mSaver.saveSecret(Settings.mSecret);

                        getCaptcha(obj);
                        mIResponce.onAnswerOk(r.getKey());//Временно, надо убрать
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        getCaptcha(obj);
                        mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                mIResponce.onAnswerError(r.getKey(), r.getMessage());
            }
        }
        else if(key.equals(Settings.RECOVER_USER_ID))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        getCaptcha(obj);
                        mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                mIResponce.onAnswerError(r.getKey(), r.getMessage());
            }
        }
        else if(key.equals(Settings.CONFIRM_RECOVER_USER_ID))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mSecret = getString(obj, ANSW_SECRET);
                        mSaver.saveSecret(Settings.mSecret);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                mIResponce.onAnswerError(r.getKey(), r.getMessage());
            }
        }*/
        else if(key.equals(Settings.REQ_ATTACH_MSISDN))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        getCaptcha(obj);
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.CONFIRM_ATTACH_MSISDN))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), getString(obj, ANSW_MESSAGE));
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.CHECK_BALANCE))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mBalance = getString(obj, ANSW_BALANCE);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.REFILL_BALANCE))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mBalance = getString(obj, ANSW_BALANCE);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.PERFORM_CALLBACK))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mCurrCallID = getString(obj, ANSW_CALL_ID);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        parseError(status, r.getKey(), getString(obj, ANSW_MESSAGE));
                        //parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.GET_ATTACHED_MSISDN))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        JSONArray jsonNumbers = obj.getJSONArray(ANSW_MSISDNS);
                        if(jsonNumbers != null)
                        {
                            Settings.mNumbers.clear();
                            for(int i = 0; i < jsonNumbers.length(); i++)
                                Settings.mNumbers.add(jsonNumbers.getString(i));
                        }
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.START_ATTACH_MSISDN))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        getCaptcha(obj);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.CHECK_COUNTRY_LIST))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        Settings.mCurrentTimeUpdate = obj.getLong(ANSW_DATE);
                        //mSaver.saveTime(Settings.mTimeUpdate);
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        //mIResponce.onAnswerError(r.getKey(), getString(obj, ANSW_MESSAGE));
                        parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                //mIResponce.onAnswerError(r.getKey(), r.getMessage());
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.GET_COUNTRY_LIST))
        {
            if(!r.isError())
            {
                try
                {
                    JSONObject obj = r.getData();
                    String status = getString(obj, ANSW_STATUS);

                    if(status.equals(TYPE_OK))
                    {
                        JSONArray mObjCountries = obj.getJSONArray(ANSW_LIST);
                        if(mObjCountries != null)
                        {
                            for(int i = 0; i < mObjCountries.length(); i++)
                            {
                                JSONObject objCountry = mObjCountries.getJSONObject(i);
                                CountryInfo ci = new CountryInfo();
                                ci.setNameCountry(getString(objCountry, ANSW_NAME));
                                ci.setPrefix(getString(objCountry, ANSW_PREFIX));
                                Settings.mCountryInfo.add(ci);
                            }
                            mSaver.saveCountries(mObjCountries.toString());
                        }
                        mIResponce.onAnswerOk(r.getKey());
                    }
                    else if(!status.equals(TYPE_OK))
                    {
                        parseError(status, r.getKey(), "");
                    }
                }
                catch (Exception e)
                {
                    Debug.e(e);
                }
            }
            else
            {
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.DETACH_MSISDN))
        {
            if(!r.isError())
            {
                JSONObject obj = r.getData();
                String status = getString(obj, ANSW_STATUS);
                if(status.equals(TYPE_OK))
                {
                    mIResponce.onAnswerOk(r.getKey());
                }
                else if(!status.equals(TYPE_OK))
                {
                    parseError(status, r.getKey(), "");
                }
            }
            else
            {
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.CHECK_CALL_STATUS))
        {
            if(!r.isError())
            {
                JSONObject obj = r.getData();
                String status = getString(obj, ANSW_STATUS);
                if(status.equals(TYPE_OK))
                {
                    String event = getString(obj, ANSW_EVENT);
                    if(event.equals(Settings.TAG_EVENT_CALL_A))
                        Settings.mCallMessage = getString(R.string.event_call_A);
                    else if(event.equals(Settings.TAG_EVENT_CALL_B))
                        Settings.mCallMessage = getString(R.string.event_call_B);
                    else if(event.equals(Settings.TAG_EVENT_FAIL_A))
                    {
                        Settings.mCallMessage = getString(R.string.event_fail_call_A);
                        Settings.isStopUpdate = true;
                    }
                    else if(event.equals(Settings.TAG_EVENT_FAIL_B))
                    {
                        Settings.mCallMessage = getString(R.string.event_fail_call_B);
                        Settings.isStopUpdate = true;
                    }
                    else if(event.equals(Settings.TAG_EVENT_PICKUP_A))
                        Settings.mCallMessage = getString(R.string.event_pickup_A);
                    else if(event.equals(Settings.TAG_EVENT_PICKUP_B))
                        Settings.mCallMessage = getString(R.string.event_pickup_B);
                    else if(event.equals(Settings.TAG_EVENT_DISCARD_A))
                    {
                        Settings.mCallMessage = getString(R.string.event_discard_A);
                        Settings.isStopUpdate = true;
                    }
                    else if(event.equals(Settings.TAG_EVENT_FINAL))
                    {
                        Settings.mCallMessage = getString(R.string.event_final);
                        Settings.isStopUpdate = true;
                    }
                    else if(event.equals(Settings.TAG_EVENT_WAIT))
                        Settings.mCallMessage = getString(R.string.event_wait);

                    String time = getString(obj, ANSW_LENGTH);
                    if(!time.equals("0"))
                        Settings.mCallMessage += " " + getString(R.string.hint_call_length) + " " + time + " " + getString(R.string.hint_call_sec);

                    mIResponce.onAnswerOk(r.getKey());
                }
                else if(!status.equals(TYPE_OK))
                {
                    parseError(status, r.getKey(), "");
                }
            }
            else
            {
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
        else if(key.equals(Settings.CHECK_API))
        {
            if(!r.isError())
            {
                JSONObject obj = r.getData();
                String status = getString(obj, ANSW_STATUS);
                if(status.equals(TYPE_OK))
                {
                    JSONObject versions = getJSONObject(obj, ANSW_VERSIONS);
                    Iterator i = versions.keys();
                    while (i.hasNext())
                    {
                        String json_key = (String)i.next();
                        if(json_key.equals(Settings.CURRENT_VERSION))
                        {
                            try
                            {
                                String state = versions.getString(json_key);
                                if(state.equals(Settings.API_STATE_ACTUAL))
                                    Settings.mAPIState = Settings.API_STATE_ACTUAL;
                                else if(state.equals(Settings.API_STATE_DEPRECATED))
                                    Settings.mAPIState = Settings.API_STATE_DEPRECATED;
                                else if(state.equals(Settings.API_STATE_CLOSE))
                                    Settings.mAPIState = Settings.API_STATE_CLOSE;
                                mIResponce.onAnswerOk(r.getKey());
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else if(!status.equals(TYPE_OK))
                {
                    parseError(status, r.getKey(), "");
                }
            }
            else
            {
                parseError(""+r.getTypeError(), r.getKey(), "");
            }
        }
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    private void getCaptcha(JSONObject o)
    {
        JSONObject objCaptchaData = getJSONObject(o, ANSW_CAPTCHA);
        if(objCaptchaData != null)  //Если капча передана, то поставим ее, иначе оставим старый вариант
        {
            mCaptcha.setType(getString(objCaptchaData, ANSW_TYPE));
            if(mCaptcha.getType().equals("code"))
            {
                mCaptcha.setTextCaptcha(getString(objCaptchaData, ANSW_DATA));
                mCaptcha.setData(null);
            }
            else
            {
                String data = getString(objCaptchaData, ANSW_DATA);
                mCaptcha.setData(Base64.decode(data.getBytes(), Base64.DEFAULT));
            }
        }
    }

    public void setInterface(IResponce ir)
    {
        this.mIResponce = ir;
    }

    private JSONObject getJSONObject(JSONObject json, String key)
    {
        JSONObject obj;
        try
        {
            obj = json.getJSONObject(key);
        }
        catch (JSONException e)
        {
            return null;
        }
        return obj;
    }

    public static  String getString(JSONObject json, String key)
    {
        String value;
        try
        {
            value = json.getString(key);
            if (value.equals("null")) { return ""; }
        }
        catch (JSONException e)
        {
            return "";
        }
        return value;
    }

    public Saver getSaver()
    {
        if(mSaver == null)
            mSaver = new Saver(this);
        return mSaver;
    }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
