package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import ru.mobstudio.voicechanger.httpCore.JSONRequest;
import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.Interface.OnClearData;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.AdapterPhones;
import ru.mobstudio.voicechanger.Utils.Debug;
import ru.mobstudio.voicechanger.Utils.EditWithDrawable;
import ru.mobstudio.voicechanger.httpCore.RequestService;

public class MainActivity extends Activity implements IResponce, OnClearData
{
    /*private AppService mService;
    private boolean isServiceBind;*/
    private RequestService mService;

    private String mMsisdnB;
    private boolean isNeedBalance = false;
    private boolean isFromContacts = false;

    private TextView mBalance, mStatusCall, mNameMelody;
    private EditWithDrawable mEditContacts;
    private FrameLayout mVoiceContainer;
    private Button mBtnCallSMS, mBtnCall;
    private ImageView mPicVoice, mBtnPlay, mBtnUpdate;
    private MediaPlayer.OnCompletionListener ocl;

    private Handler mHandler;
    private Runnable mUpdateStatus;
    private Runnable mClearStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        /*Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);*/
        mService = new RequestService(this, this);

        mBalance = (TextView)findViewById(R.id.Main_Balance);
        mStatusCall = (TextView)findViewById(R.id.Main_StatusCall);
        mNameMelody = (TextView)findViewById(R.id.Main_Text);
        mEditContacts = (EditWithDrawable)findViewById(R.id.Main_ContactEdit);
        mVoiceContainer = (FrameLayout)findViewById(R.id.Main_second);
        mBtnPlay = (ImageView)findViewById(R.id.Main_Play);
        ImageView mBtnContacts = (ImageView)findViewById(R.id.Main_Contacts);
        ImageView mBtnFill = (ImageView)findViewById(R.id.Main_FillBalance);
        mBtnUpdate = (ImageView)findViewById(R.id.Main_Update);
        mBtnCallSMS = (Button)findViewById(R.id.Main_CallSMS);
        mBtnCall = (Button)findViewById(R.id.Main_Call);
        mPicVoice = (ImageView)findViewById(R.id.Main_Pic);
        ImageView mSettings = (ImageView)findViewById(R.id.Main_Settings);

        mEditContacts.setListener(this);

        mHandler = new Handler();
        mUpdateStatus = new Runnable()
        {
            @Override
            public void run()
            {
                HashMap<String, String> jsonFields = new HashMap<String, String>();
                jsonFields.put(RequestService.REQ_CALL_ID, Settings.mCurrCallID);

                mService.addRequest(new JSONRequest(Settings.CHECK_CALL_STATUS, new JSONObject(jsonFields)));
            }
        };

        mSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.this.openOptionsMenu();
            }
        });

        mClearStatus = new Runnable()
        {
            @Override
            public void run()
            {
                mStatusCall.setText("");
                Settings.isStopUpdate = false;
            }
        };

        mEditContacts.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                if(mMsisdnB != null && mMsisdnB.length() > 0 && !isFromContacts)   //Очистим данные временные
                {
                    mMsisdnB = "";
                }
                if(mStatusCall.getText() != null && mStatusCall.getText().toString().length() > 0)
                    mStatusCall.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length() == 0)
                    mMsisdnB = "";
            }

            @Override
            public void afterTextChanged(Editable s){}
        });

        mBtnUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Settings.isConnectInternet && !Settings.isSmsWork)
                {
                    mService.addRequest(new JSONRequest(Settings.CHECK_BALANCE, null));
                }
            }
        });

        mBalance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Settings.isConnectInternet && !Settings.isSmsWork)
                {
                    mService.addRequest(new JSONRequest(Settings.CHECK_BALANCE, null));
                }
            }
        });

        mBtnFill.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mHandler.removeCallbacks(mClearStatus);

                Intent i = new Intent();
                i.setClass(MainActivity.this, FillBalance.class);
                startActivity(i);
            }
        });

        mVoiceContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mService.mPlayer.isPlay())
                {
                    mService.mPlayer.stopPlayer();
                    mBtnPlay.setImageResource(R.drawable.ic_play);
                }
                mHandler.removeCallbacks(mClearStatus);

                Intent i = new Intent();
                i.setClass(MainActivity.this, ChooseVoice.class);
                startActivity(i);
            }
        });

        mBtnCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Settings.isConnectInternet)
                {
                    if((mMsisdnB == null || mMsisdnB.length() == 0) && (mEditContacts.getText() == null || mEditContacts.getText().toString().length() == 0))
                    {
                        Toast.makeText(MainActivity.this, R.string.Error_fill_fields, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!Settings.isConnectInternet)
                    {
                        Toast.makeText(MainActivity.this, R.string.Error_internet_connect, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(mMsisdnB == null || mMsisdnB.length() == 0)
                    {
                        mMsisdnB = mEditContacts.getText().toString();
                        correctMSISDN(mMsisdnB);
                    }

                    if(mMsisdnB.equals(Settings.mMSISDN))
                    {
                        Toast.makeText(MainActivity.this, R.string.Error_same_number, Toast.LENGTH_SHORT).show();
                        mMsisdnB = "";
                        mEditContacts.setText("");
                        return;
                    }

                    mHandler.removeCallbacks(mClearStatus);

                    HashMap<String, String> jsonFields = new HashMap<String, String>();
                    //noinspection ConstantConditions
                    jsonFields.put(RequestService.REQ_MSISDN_A, Settings.mMSISDN);
                    //noinspection ConstantConditions
                    Debug.i("Номер из записной книжки = " + mMsisdnB);
                    jsonFields.put(RequestService.REQ_MSISDN_B, mMsisdnB);
                    jsonFields.put(RequestService.REQ_VOICE_ID, "" + (Settings.mVoiceID+1));
                    String locale = "EN";
                    if(Settings.isRusLocale)
                        locale = "RU";

                    jsonFields.put(RequestService.REQ_LOCALE, locale);
                    mService.addRequest(new JSONRequest(Settings.PERFORM_CALLBACK, new JSONObject(jsonFields)));
                }

                if(!Settings.isConnectInternet)
                {
                    Toast.makeText(MainActivity.this, R.string.Error_internet_connect, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnCallSMS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mStatusCall.getText() != null && mStatusCall.getText().toString().length() > 0)
                    mStatusCall.setText("");

                if((mMsisdnB == null || mMsisdnB.length() == 0) && (mEditContacts.getText() == null || mEditContacts.getText().toString().length() == 0))
                {
                    Toast.makeText(MainActivity.this, R.string.Error_fill_fields, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mMsisdnB == null || mMsisdnB.length() == 0)
                {
                    mMsisdnB = mEditContacts.getText().toString();
                    correctMSISDN(mMsisdnB);
                }

                //Сделать выбор идентификатора, номера и проверку на заполненность
                Uri uri = Uri.parse(Settings.TAG_SMS_ADDRESS);
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra(Settings.TAG_SMS_BODY, getString(R.string.sms_create_call) + " " + mMsisdnB + " " + getString(R.string.sms_create_voice) + " " + Settings.getNameByID(MainActivity.this, Settings.mVoiceID));
                startActivity(it);
            }
        });

        mBtnContacts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mHandler.removeCallbacks(mClearStatus);

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Settings.mVoiceID == -1)
                    return;

                if(!mService.mPlayer.isPlay())
                {
                    mService.mPlayer.PlayMusic(Settings.getVoiceByID(Settings.mVoiceID));
                    mBtnPlay.setImageResource(R.drawable.ic_pause_np);
                }
                else
                {
                    mBtnPlay.setImageResource(R.drawable.ic_play);
                    mService.mPlayer.stopPlayer();
                }
            }
        });

        ocl = new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                Debug.i("Проигрыватель завершил проигрывать =))");
                mBtnPlay.setImageResource(R.drawable.ic_play);
            }
        };

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

        mEditContacts.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});

        mService.mPlayer.setCopleteInterface(ocl);

        if(!isNeedBalance && (Settings.isConnectInternet && !Settings.isSmsWork))
        {
            mService.addRequest(new JSONRequest(Settings.CHECK_BALANCE, null));
        }

        if(!Settings.isHelpCall)
        {
            Settings.mCurrMethod = 2;
            Settings.isHelpCall = true;
            mService.getSaver().saveHelpState();

            Intent i = new Intent();
            i.setClass(MainActivity.this, Hint.class);
            startActivity(i);
        }
    }

    private void setMelodyUI()
    {
        if(Settings.mVoiceID == -1)
        {
            //Set default первый из списка
            mVoiceContainer.setBackgroundResource(Settings.getBackgroundColorAlpha(0));
            mPicVoice.setImageResource(Settings.getBackgroundByID(0));
            mNameMelody.setText(Settings.getNameByID(this, 0));
            Settings.mVoiceID = 0;
        }
        else
        {
            mVoiceContainer.setBackgroundResource(Settings.getBackgroundColorAlpha(Settings.mVoiceID));
            mPicVoice.setImageResource(Settings.getBackgroundByID(Settings.mVoiceID));
            mNameMelody.setText(Settings.getNameByID(this, Settings.mVoiceID));
        }
    }

    private void setBalanceUI()
    {
        //mBalance.setText(MainActivity.this.getString(R.string.main_balance) + Settings.mBalance);
        if(Settings.isFullWork)
        {
            mBalance.setText(getString(R.string.main_balance, Settings.mBalance));
            mBtnUpdate.setVisibility(View.VISIBLE);
        }
        else if(Settings.isSmsWork)
        {
            mBalance.setText(getString(R.string.main_balance, "---"));
            mBtnUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == Activity.RESULT_OK)
        {
            try
            {
                Uri contactData = data.getData();
                if(contactData == null)
                    return;
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(contactData, null, null, null, null);
                if(c == null)
                    return;
                if(c.moveToFirst())
                {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if(hasPhone == null)
                        return;
                    if(hasPhone.equals("1"))
                    {
                        isFromContacts = true;
                        if(ContactsContract.CommonDataKinds.Phone.CONTENT_URI == null)
                            return;
                        final Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + id, null, null);
                        if(phones == null)
                            return;
                        phones.moveToFirst();
                        final String nameContact = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        if(phones.getCount() > 1)
                            MainActivity.this.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    openPhoneDialogList(phones, nameContact);
                                }
                            });
                        else
                        {
                            mMsisdnB = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //mEditContacts.setText(nameContact);
                            correctMSISDN(mMsisdnB);
                            mEditContacts.setText(mMsisdnB.substring(1));
                        }
                    }
                }
            }
            catch(Exception e)
            {
                Debug.e(e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openPhoneDialogList(Cursor phones, final String name)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);

        ListView mListPhones = new ListView(MainActivity.this);
        final AdapterPhones adapt = new AdapterPhones(MainActivity.this);
        mListPhones.setAdapter(adapt);
        adapt.clearAdapter();
        adapt.addPhones(phones);


        adb.setTitle(name);
        adb.setView(mListPhones);
        adb.setNegativeButton(R.string.btn_cancel, null);
        final AlertDialog ad = adb.create();
        ad.show();

        mListPhones.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //mEditContacts.setText(name);
                mMsisdnB = (String)adapt.getItem(position);
                isFromContacts = true;
                correctMSISDN(mMsisdnB);
                mEditContacts.setText(mMsisdnB.substring(1));
                ad.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean("Main_state", isFromContacts);

        if(isFromContacts)
        {
            outState.putString("Main_name", mEditContacts.getText().toString());
            outState.putString("Main_number", mMsisdnB);
        }
        else
        {
            if(mEditContacts.getText() != null && mEditContacts.getText().length() > 0)
                outState.putString("Main_number", mEditContacts.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        isFromContacts = savedInstanceState.getBoolean("Main_state");
        if(isFromContacts)
        {
            String name = savedInstanceState.getString("Main_name");
            if(name != null)
            {
                mEditContacts.setText(name);
                mMsisdnB = savedInstanceState.getString("Main_number");
            }
        }
        else
        {
            String number = savedInstanceState.getString("Main_number");
            if(number == null)
                number = "";
            mEditContacts.setText(number);
        }
    }

    private void correctMSISDN(String number)
    {
        if(number == null || number.length() == 0)
            return;

        //Уберем "+", если в записной книге он был
        if(number.charAt(0) == '+')
            number = number.substring(1);

        //Если телефон начинается с 0, то уберем
        if(number.charAt(0) == '0')
            number = number.substring(1);

        //Если телефон начинается с 00, то уберем второй 0
        if(number.charAt(0) == '0')
            number = number.substring(1);

        number = number.replace("-", "");
        number = number.replace(" ", "");

        //Если это какой-то короткий номер, то его тоже не будем обрабатывать
        if(number.length() < 10)
        {
            mEditContacts.setText("");
            mMsisdnB = "";
            Toast.makeText(MainActivity.this, R.string.Error_wrong_number, Toast.LENGTH_SHORT).show();
            return;
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
                mEditContacts.setText("");
                mMsisdnB = "";
                Toast.makeText(MainActivity.this, R.string.Error_wrong_number, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else
        {
            //Теперь разберемся, что введено в коде страны
            //Если мы в России и код начинается с 8, то заменим ее на 7
            if(Settings.isRussianMSISDN && numberCode.charAt(0) == '8' && numberCode.length() == 1)
            {
                numberCode = "7" + numberCode.substring(1);
            }
        }

        mMsisdnB = "+" + numberCode + numberBody;
        Debug.i("Phone = " + mMsisdnB);
    }

    @Override
    public void onAnswerOk(String TAG)
    {
        if(TAG.equals(Settings.CHECK_BALANCE))
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //mBalance.setText(MainActivity.this.getString(R.string.main_balance) + Settings.mBalance);
                    setBalanceUI();
                    isNeedBalance = true;
                }
            });
        }
        if(Settings.mJSON != null && Settings.mJSON.length() > 0 && Settings.mSignature != null && Settings.mSignature.length() > 0)
        {
            HashMap<String, String> jsonFields1 = new HashMap<String, String>();
            jsonFields1.put(RequestService.REQ_RESPONCE_DATA, Settings.mJSON);
            jsonFields1.put(RequestService.REQ_SIGNATURE, Settings.mSignature);
            String data = android.util.Base64.encodeToString(new JSONObject(jsonFields1).toString().getBytes(), android.util.Base64.NO_WRAP);
            HashMap<String, String> jsonFields = new HashMap<String, String>();
            jsonFields.put(RequestService.REQ_PURCHASE_DATA, data);

            mService.addRequest(new JSONRequest(Settings.REFILL_BALANCE, new JSONObject(jsonFields)));
        }
        if(TAG.equals(Settings.REFILL_BALANCE))
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mService.getSaver().saveBuyData("", "");
                    Toast.makeText(MainActivity.this, R.string.fill_balance_fill_ok, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(TAG.equals(Settings.PERFORM_CALLBACK))
        {
            //Запросим статус звонка и каждые 10 секунд его будем менять
            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mStatusCall.setText(R.string.event_wait);
                }
            });
            mHandler.postDelayed(mUpdateStatus, 5000);
        }
        if(TAG.equals(Settings.CHECK_CALL_STATUS))
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mStatusCall.setText(Settings.mCallMessage);
                    if(!Settings.isStopUpdate)
                    {
                        mHandler.postDelayed(mUpdateStatus, 5000);
                    }
                    else //Если статус конечный, то удалим его через 10 секунд
                        mHandler.postDelayed(mClearStatus, 10000);

                }
            });
        }
    }

    @Override
    public void onAnswerError(String TAG, final String message)
    {
        if(message != null && message.length() > 0)
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAnswerOther(String TAG)
    {

    }

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
            mService.setInterface(MainActivity.this);

        }
    };*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.action_settings:

                if(!Settings.isConnectInternet)
                {
                    Toast.makeText(MainActivity.this, R.string.Error_internet_connect, Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(Settings.isSmsWork)
                {
                    Toast.makeText(MainActivity.this, R.string.main_menu_sms_mode, Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent i = new Intent();
                i.setClass(MainActivity.this, AddNumber.class);
                startActivity(i);
                return true;
            case R.id.action_how_to:
                Settings.mCurrMethod = 5;
                Intent p = new Intent();
                p.setClass(MainActivity.this, Hint.class);
                startActivity(p);
                return true;
            case R.id.action_info:
                Intent o = new Intent();
                o.setClass(MainActivity.this, Info.class);
                startActivity(o);
                return true;
            /*case R.id.action_work_state:
                Intent g = new Intent();
                g.setClass(MainActivity.this, WorkState.class);
                startActivity(g);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume()
    {
        setMelodyUI();
        setBalanceUI();
        if(!Settings.isRussianMSISDN)
            mBtnCallSMS.setVisibility(View.GONE);
        else
            mBtnCallSMS.setVisibility(View.VISIBLE);

        if(Settings.isRussianMSISDN)
        {
            if(Settings.isFullWork)
                mBtnCall.setEnabled(true);
            else
                mBtnCall.setEnabled(false);
        }
        else
            mBtnCallSMS.setVisibility(View.GONE);

        if(Settings.isHelpCall /*&& Settings.isHelpCallSMS*/ && Settings.mCurrMethod != 5)
        {
            Settings.mCurrMethod = 5;
            mService.getSaver().saveHelpState();
        }

        mService.mPlayer.setCopleteInterface(ocl);
        mService.setInterface(MainActivity.this);

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        if(mService.mPlayer.isPlay())
        {
            mService.mPlayer.stopPlayer();
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }

        super.onPause();
    }

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

    @Override
    public void clear()
    {
        isFromContacts = false;
        mMsisdnB = "";
    }
}
