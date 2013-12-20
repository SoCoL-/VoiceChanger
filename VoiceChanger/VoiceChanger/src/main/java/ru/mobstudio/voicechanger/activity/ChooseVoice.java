package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.mobstudio.voicechanger.httpCore.AppService;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.AdapterVoice;

/**
 * Created by Evgenij on 15.11.13.
 *
 * 1 Дед Мороз
 2 Злодей
 3 Гелиевый
 4 Мужской
 5 Женский
 6 Мультяшка
 7 Бурундук
 8 Эхо
 9 Пришелец
 10 Дракон
 *
 */
public class ChooseVoice extends Activity
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private AppService mService;
    private boolean isServiceBind;

    private ListView mList;
    private AdapterVoice mAdapter;

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_voice);

        Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);

        mList = (ListView)findViewById(R.id.Choose_List);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Settings.mVoiceID = position;
                mAdapter.stopPlay();
                finish();
            }
        });
    }

    //-----------------------------
    //Methods
    //-----------------------------

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
            mAdapter = new AdapterVoice(ChooseVoice.this, mService.mPlayer);
            mList.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onPause()
    {
        if(mAdapter != null)
            mAdapter.stopPlay();

        super.onPause();
    }

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
