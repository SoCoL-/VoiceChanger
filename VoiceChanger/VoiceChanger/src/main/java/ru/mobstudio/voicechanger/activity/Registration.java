package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import ru.mobstudio.voicechanger.httpCore.AppService;
import ru.mobstudio.voicechanger.httpCore.JSONRequest;
import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Debug;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class Registration extends Activity implements IResponce

{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private AppService mService;
    private boolean isServiceBind;

    private Button mBtnReg;
    private ImageView mPicCaptcha;
    private EditText mMSISDN, mEditCaptcha, mEditCode;
    private TextView mTextSMS, mTextCaptcha, mTextCode;

    private boolean isNeedSMS = false;
    private String mNumber;
    private int mNumberWrog;
    private ProgressDialog mPD;

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);

        mBtnReg = (Button)findViewById(R.id.Reg_ButtonReg);
        mPicCaptcha = (ImageView)findViewById(R.id.Reg_PicCaptcha);
        mEditCaptcha = (EditText)findViewById(R.id.Reg_EditCaptcha);
        mTextCaptcha = (TextView)findViewById(R.id.Reg_TextCaptcha);
        mMSISDN = (EditText)findViewById(R.id.Reg_EditSms);
        mTextSMS = (TextView)findViewById(R.id.Reg_TextSMS);
        mEditCode = (EditText)findViewById(R.id.Reg_EditCode);
        mTextCode = (TextView)findViewById(R.id.Reg_TextCode);

        mPD = new ProgressDialog(this);

        mBtnReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isServiceBind)
                    Toast.makeText(Registration.this, R.string.Error_service_not_found, Toast.LENGTH_SHORT).show();

                if(Settings.isBanned)
                {
                    mService.addRequest(new JSONRequest(Settings.CHECK_AUTH, new JSONObject()));
                }

                if(!isNeedSMS)
                {
                    if(!isFillFields())
                    {
                        Toast.makeText(Registration.this, R.string.Error_fill_fields, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if((mNumber = correctMSISDN(mMSISDN.getText().toString())) == null)
                        return;

                    HashMap<String, String> jsonFields = new HashMap<String, String>();
                    //noinspection ConstantConditions
                    jsonFields.put(AppService.REQ_MSISDN, mNumber);
                    //noinspection ConstantConditions
                    jsonFields.put(AppService.REQ_CAPTCHA_CODE, mEditCaptcha.getText().toString());
                    String locale = "EN";
                    if(Settings.isRusLocale)
                        locale = "RU";
                    jsonFields.put(AppService.REQ_LOCALE, locale);
                    Settings.mMSISDN = mNumber;
                    mService.addRequest(new JSONRequest(Settings.REQUEST_AUTH, new JSONObject(jsonFields)));
                    mEditCaptcha.setEnabled(false);
                    mMSISDN.setEnabled(false);
                    mPD.show();
                }
                else// if(isNeedSMS && !Settings.isRecoverUserID)
                {
                    if(!isFillFields())
                    {
                        Toast.makeText(Registration.this, R.string.Error_fill_fields, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HashMap<String, String> jsonFields = new HashMap<String, String>();
                    //noinspection ConstantConditions
                    jsonFields.put(AppService.REQ_MSISDN, mNumber);
                    //noinspection ConstantConditions
                    jsonFields.put(AppService.REQ_SMS_CODE, mEditCode.getText().toString());
                    mService.mSaver.saveMSISDN(mNumber);
                    jsonFields.put(AppService.REQ_STORE, Settings.TYPE_STORE);
                    mService.addRequest(new JSONRequest(Settings.CONFIRM_AUTH, new JSONObject(jsonFields)));
                    mEditCode.setEnabled(false);
                    mPD.show();
                }
            }
        });

        InputFilter filter = new InputFilter()
        {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
            {
                for(int i = start; i < end; i++)
                {
                    if(!Character.isDigit(source.charAt(i)))
                    {
                        return "";
                    }
                }
                return null;
            }
        };

        mMSISDN.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});
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
            mService.setInterface(Registration.this);
            setupInterfaceCaptcha();

            /*if(Settings.mCurrMethod == 0)
            {
                Settings.mCurrMethod = 1;
                mService.mSaver.saveHelpState();

                Intent i = new Intent();
                i.setClass(Registration.this, Hint.class);
                startActivity(i);
            }*/
        }
    };

    @Override
    public void onAnswerOk(String TAG)
    {
        if(TAG.equals(Settings.REQUEST_AUTH))
        {
            //Продолжим привязывать номер телефона к аккаунту на серваке
            //Уберем лишние формы с активити
            Registration.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    isNeedSMS = true;
                    setupInterface();
                    mPD.dismiss();

                    /*if(Settings.mCurrMethod == 1)
                    {
                        Settings.mCurrMethod = 2;
                        mService.mSaver.saveHelpState();

                        Intent i = new Intent();
                        i.setClass(Registration.this, Hint.class);
                        startActivity(i);
                    }*/
                }
            });
        }
        if(TAG.equals(Settings.CONFIRM_AUTH))
        {
            mPD.dismiss();
            Intent i = new Intent();
            i.setClass(Registration.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onAnswerError(String TAG, final String message)
    {
        if(message != null && message.length() > 0)
        {
            if(TAG.equals(Settings.CONFIRM_AUTH))
            {
                mPD.dismiss();
                if(mNumberWrog != 2)
                {
                    mNumberWrog++;
                    mEditCode.setEnabled(true);
                }
                else
                {
                    //Делаем тогда все сначала
                    isNeedSMS = false;
                    setupInterface();
                    mService.addRequest(new JSONRequest(Settings.CHECK_AUTH, new JSONObject()));
                    mPD.show();
                }
            }
            if(TAG.equals(Settings.REQUEST_AUTH))
            {
                mPD.dismiss();
                isNeedSMS = false;
                mEditCaptcha.setEnabled(true);
                mMSISDN.setEnabled(true);
                setupInterfaceCaptcha();
            }
            if(TAG.equals(Settings.CHECK_AUTH))
            {
                mPD.dismiss();
                Registration.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mNumberWrog = 0;
                        setupInterfaceCaptcha();
                    }
                });
            }
            Registration.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAnswerOther(String TAG)
    {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(mNumber != null && mNumber.length() > 0)
            outState.putString("Reg_number", mNumber);
        if(isNeedSMS)
        {
            if(mEditCaptcha.getText() != null && mEditCaptcha.getText().length() > 0)
                outState.putString("Reg_captcha", mEditCaptcha.getText().toString());
        }
        else
        {
            if(mEditCode.getText() != null && mEditCode.getText().length() > 0)
                outState.putString("Reg_code", mEditCode.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        mNumber = savedInstanceState.getString("Reg_number");
        if(mNumber == null)
            mNumber = "";
        if(isNeedSMS)
        {
            mMSISDN.setText(mNumber);
            String captcha = savedInstanceState.getString("Reg_captcha");
            if(captcha == null)
                captcha = "";
            mEditCaptcha.setText(captcha);
        }
        else
        {
            String code = savedInstanceState.getString("Reg_code");
            if(code == null)
                code = "";
            mEditCode.setText(code);
        }
    }

    private String correctMSISDN(String number)
    {
        String rez = null;

        if(number == null || number.length() == 0)
            return rez;

        //Уберем "+", если в записной книге он был
        if(number.charAt(0) == '+')
            number = number.substring(1);

        //Если телефон начинается с 0, то уберем
        if(number.charAt(0) == '0')
            number = number.substring(1);

        //Если телефон начинается с 00, то уберем второй 0
        if(number.charAt(0) == '0')
            number = number.substring(1);

        //Если это какой-то короткий номер, то его тоже не будем обрабатывать
        if(number.length() < 10)
        {
            mMSISDN.setText("");
            Toast.makeText(Registration.this, R.string.Error_wrong_number, Toast.LENGTH_SHORT).show();
            return rez;
        }

        //Вырежем тело номера 9233355502
        String numberBody = number.substring(number.length() - 10, number.length());
        //Вырежем код страны номера
        String numberCode = number.substring(0, number.length() - 10);

        if(numberCode.length() == 0)
        {
            //Самый простой вариант: Еси не Россия, то не звоним, иначе подставми +7
            if(Settings.isRussianMSISDN)
                numberCode = "7";
            else
            {
                mMSISDN.setText("");
                Toast.makeText(Registration.this, R.string.Error_wrong_number, Toast.LENGTH_SHORT).show();
                return rez;
            }
        }
        else
        {
            //Теперь разберемся, что введено в коде страны
            //Если мы в России и код начинается с 8, то заменим ее на 7
            if(Settings.isRussianMSISDN && numberCode.charAt(0) == '8')
            {
                numberCode = "7" + numberCode.substring(1);
            }
        }

        return "+" + numberCode + numberBody;
    }

    private void setupInterfaceCaptcha()
    {
        if(mService.mCaptcha.getType().equals("code"))
        {
            mEditCaptcha.setText(mService.mCaptcha.getTextCaptcha());
        }
        else if(mService.mCaptcha.getType().contains("image"))
        {
            mPicCaptcha.setVisibility(View.VISIBLE);
            mEditCaptcha.setVisibility(View.VISIBLE);
            mTextCaptcha.setVisibility(View.VISIBLE);
            ByteArrayInputStream in = new ByteArrayInputStream(mService.mCaptcha.getData());
            mPicCaptcha.setBackgroundDrawable(Drawable.createFromStream(in, "captcha"));
        }
    }

    private void setupInterface()
    {
        if(isNeedSMS)//Ждем прихода смски
        {
            mPicCaptcha.setVisibility(View.GONE);
            mEditCaptcha.setVisibility(View.GONE);
            mTextCaptcha.setVisibility(View.GONE);
            RelativeLayout mContainer = (RelativeLayout)Registration.this.findViewById(R.id.Reg_SMSContainer);
            mContainer.setVisibility(View.GONE);
            mTextSMS.setVisibility(View.GONE);
            mEditCode.setVisibility(View.VISIBLE);
            mTextCode.setVisibility(View.VISIBLE);
            mBtnReg.setText(R.string.btn_send_code);
        }
        else//Если неправильно ввели код из смс
        {
            mPicCaptcha.setVisibility(View.VISIBLE);
            mEditCaptcha.setVisibility(View.VISIBLE);
            mEditCaptcha.setText("");
            mTextCaptcha.setVisibility(View.VISIBLE);
            RelativeLayout mContainer = (RelativeLayout)Registration.this.findViewById(R.id.Reg_SMSContainer);
            mContainer.setVisibility(View.VISIBLE);
            mTextSMS.setVisibility(View.VISIBLE);
            mEditCode.setText("");
            mEditCode.setVisibility(View.GONE);
            mTextCode.setVisibility(View.GONE);
            mBtnReg.setText(R.string.btn_registration);
        }
    }

    private boolean isFillFields()
    {
        boolean rez;
        if(isNeedSMS)
        {
            rez = (mEditCode.getText() != null && mEditCode.getText().length() > 0);
        }
        else
        {
            boolean rezz;

            if(mEditCaptcha.isShown())
                rezz = mEditCaptcha.getText() != null && mEditCaptcha.getText().length() > 0;
            else
                rezz = true;

            rez = (mMSISDN.getText() != null && mMSISDN.getText().length() > 0 && rezz);
        }
        return rez;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, final Intent intent)
        {
            String action = intent.getAction();
            if(action != null && action.equals(Settings.RECEIVE_SMS))
            {
                Debug.i("Оппа, прилетел пароль из смски!");
                Registration.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mEditCode.setText(intent.getStringExtra(Settings.TAG_STORE_SMS));
                    }
                });
            }
        }
    };

    @Override
    public void onResume()
    {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Settings.RECEIVE_SMS);

        registerReceiver(mReceiver, mFilter);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        unregisterReceiver(mReceiver);
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
