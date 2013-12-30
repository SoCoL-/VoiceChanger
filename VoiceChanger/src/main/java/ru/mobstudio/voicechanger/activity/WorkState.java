package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.httpCore.RequestService;

/**
 * Created by Evgenij on 26.11.13.
 *
 */
public class WorkState extends Activity implements IResponce
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    /*private AppService mService;
    private boolean isServiceBind;*/
    private RequestService mService;

    CheckBox isSmsMode, isFullMode;
    private boolean isOldSmsMode;

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_state_activity);

        /*Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);*/
        mService = new RequestService(this, this);

        isSmsMode = (CheckBox)findViewById(R.id.Work_SMS);
        isFullMode = (CheckBox)findViewById(R.id.Work_Full);

        updateInterface();

        isFullMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    Settings.isFullWork = true;
                    Settings.isSmsWork = false;
                    isSmsMode.setChecked(false);
                }
                else
                {
                    Settings.isFullWork = false;
                    Settings.isSmsWork = true;
                    isSmsMode.setChecked(true);
                }
            }
        });

        isSmsMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    Settings.isFullWork = false;
                    Settings.isSmsWork = true;
                    isFullMode.setChecked(false);
                }
                else
                {
                    Settings.isFullWork = true;
                    Settings.isSmsWork = false;
                    isFullMode.setChecked(true);
                }
            }
        });
    }

    //-----------------------------
    //Methods
    //-----------------------------

    /*private ServiceConnection mConnect = new ServiceConnection()
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
            //mService.setInterface(W.this);
        }
    };*/

    private void updateInterface()
    {
        isOldSmsMode = Settings.isSmsWork;

        if(Settings.isFullWork)
            isFullMode.setChecked(true);

        if(Settings.isSmsWork)
            isSmsMode.setChecked(true);
    }

    @Override
    public void onBackPressed()
    {
        if(isOldSmsMode && Settings.isFullWork)  //Если мы захотели поработать через интернет и были в режиме смсок, то перейдем на регистрацию
        {
            Intent i = new Intent();
            i.setClass(WorkState.this, Hello.class);
            startActivity(i);
            finish();
        }

        super.onBackPressed();
    }

    @Override
    public void onDestroy()
    {
        /*if(isServiceBind)
        {
            mService.getSaver().saveStateWork();
            unbindService(mConnect);
        }*/
        mService.getSaver().saveStateWork();
        mService.destroy();
        mService = null;
        super.onDestroy();
    }

    @Override
    public void onAnswerOk(String TAG) {

    }

    @Override
    public void onAnswerError(String TAG, String message) {

    }

    @Override
    public void onAnswerOther(String TAG) {

    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
