package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ru.mobstudio.voicechanger.httpCore.AppService;
import ru.mobstudio.voicechanger.httpCore.JSONRequest;
import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.AdapterCountry;

/**
 * Created by Evgenij on 22.11.13.
 *
 */
public class Info extends Activity implements IResponce
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private AppService mService;
    private boolean isServiceBind;

    private TextView mEmpty;
    private ListView mList;
    private AdapterCountry mAdapter;

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);

        mEmpty = (TextView)findViewById(R.id.Info_Empty);
        mList = (ListView)findViewById(R.id.Info_List);
        mAdapter = new AdapterCountry(this);
        mList.setAdapter(mAdapter);

        UpdateInterface();
    }

    //-----------------------------
    //Methods
    //-----------------------------

    private void UpdateInterface()
    {
        if(Settings.mCountryInfo == null || Settings.mCountryInfo.size() == 0)
        {
            mList.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            mList.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            mAdapter.clear();
            mAdapter.addInfos(Settings.mCountryInfo);
        }
    }

    private ServiceConnection mConnect = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mService = null;
            isServiceBind = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mService = ((AppService.BindService)service).getService();
            isServiceBind = true;
            mService.setInterface(Info.this);

            if(Settings.isConnectInternet)
                mService.addRequest(new JSONRequest(Settings.CHECK_COUNTRY_LIST, new JSONObject()));
        }
    };

    @Override
    public void onAnswerOk(String TAG)
    {
        if(TAG.equals(Settings.CHECK_COUNTRY_LIST))
        {
            if(Settings.mTimeUpdate < Settings.mCurrentTimeUpdate)
            {
                //обновим список городов
                Settings.mTimeUpdate = Settings.mCurrentTimeUpdate;
                mService.mSaver.saveTime(Settings.mTimeUpdate);

                mService.addRequest(new JSONRequest(Settings.GET_COUNTRY_LIST, new JSONObject()));
            }
        }
        else if(TAG.equals(Settings.GET_COUNTRY_LIST))
        {
            //обновить интерфейс
            Info.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    UpdateInterface();
                }
            });
        }
    }

    @Override
    public void onAnswerError(String TAG, final String message)
    {
        Info.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(Info.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAnswerOther(String TAG) {}

    @Override
    public void onDestroy()
    {
        if(isServiceBind)
        {
            unbindService(mConnect);
        }
        mService = null;
        super.onDestroy();
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
