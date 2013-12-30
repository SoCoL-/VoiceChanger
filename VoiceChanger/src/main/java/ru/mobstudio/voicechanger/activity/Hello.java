package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import ru.mobstudio.voicechanger.httpCore.JSONRequest;
import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.httpCore.RequestService;

/**
 * Created by Evgenij on 12.11.13.
 *
 */
public class Hello extends Activity implements IResponce
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

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);

        /*Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);*/
        mService = new RequestService(this, this);

        if(Settings.isConnectInternet)
            mService.addRequest(new JSONRequest(Settings.CHECK_API, new JSONObject()));
        else
            workContinue();
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
            mService.setInterface(Hello.this);


        }
    };*/

    private void workContinue()
    {
        if(Settings.isConnectInternet/* && Settings.isRussianMSISDN*/)
        {
            workFullMode();
        }
        else if(!Settings.isConnectInternet)
        {
            if(Settings.isRussianMSISDN)
                workSMSMode();
            else
            {
                AlertDialog.Builder d = new AlertDialog.Builder(Hello.this);
                d.setCancelable(false);
                d.setTitle(R.string.dialog_title_error);
                d.setMessage(R.string.dialog_no_internet_message);
                d.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Hello.this.finish();
                    }
                });
                d.show();
            }
        }
    }

    private void workFullMode()
    {
        if(!Settings.isConnectInternet)
            workSMSMode();

        Settings.isFullWork = true;
        Settings.isSmsWork = false;
        if(mService == null)
            mService = new RequestService(this, this);

        mService.getSaver().saveStateWork();
        mService.addRequest(new JSONRequest(Settings.CHECK_AUTH, new JSONObject()));
    }

    private void workSMSMode()
    {
        Settings.isSmsWork = true;
        Settings.isFullWork = false;
        mService.getSaver().saveStateWork();
        Intent i = new Intent();
        i.setClass(Hello.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    @Override
    public void onAnswerOk(String TAG)
    {
        if(TAG.equals(Settings.CHECK_AUTH))     //Мы зареганы, авторизация пройдена успешно
        {
            Intent i = new Intent();
            i.setClass(Hello.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            finish();
        }
        else if(TAG.equals(Settings.CHECK_API))
        {
            //Покажем диалог, если версия не actual
            if(Settings.mAPIState.equals(Settings.API_STATE_ACTUAL))
                workContinue();
            else if(Settings.mAPIState.equals(Settings.API_STATE_DEPRECATED))
            {
                AlertDialog.Builder d = new AlertDialog.Builder(Hello.this);
                d.setCancelable(false);
                d.setTitle(R.string.dialog_title_warning);
                d.setMessage(R.string.Hello_api_state_deprecated);
                d.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        workContinue();
                    }
                });
                d.show();
            }
            else if(Settings.mAPIState.equals(Settings.API_STATE_CLOSE))
            {
                AlertDialog.Builder d = new AlertDialog.Builder(Hello.this);
                d.setCancelable(false);
                d.setTitle(R.string.dialog_title_error);
                d.setMessage(R.string.Hello_api_state_close);
                d.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Hello.this.finish();
                    }
                });
                d.show();
            }
        }
    }

    @Override
    public void onAnswerError(String TAG, final String message)
    {
        if(message != null && message.length() > 0)
        {
            if(TAG.equals(Settings.CHECK_AUTH))
            {
                {
                    Intent i = new Intent();
                    i.setClass(Hello.this, Registration.class);
                    startActivity(i);
                    finish();
                }
            }
            Hello.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(!message.equals("OK"))
                        Toast.makeText(Hello.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAnswerOther(String TAG) { }

    @Override
    public void onDestroy()
    {
        /*if(isServiceBind)
        {
            unbindService(mConnect);
        }*/
        mService.destroy();
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
