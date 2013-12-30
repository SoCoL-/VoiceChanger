package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import ru.mobstudio.voicechanger.httpCore.JSONRequest;
import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Debug;
import ru.mobstudio.voicechanger.httpCore.RequestService;

/**
 * Created by Evgenij on 18.11.13.
 *
 */
public class AddNumber extends Activity implements IResponce
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    //private AppService mService;
    //private boolean isServiceBind;
    private RequestService mService;

    private TextView mEmpty;
    private ListView mList;
    private EditText mEditNumber;
    private ArrayAdapter<String> mListAdapter;

    private int mItemIndex;
    private int mNumberError = 0;               //Количесто ошибок ввода смс кода

    //-----------------------------
    //Ctors
    //-----------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_number);

        //Intent mServiceIntent = new Intent(this, AppService.class);
        //bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);

        ImageButton mBtnAdd = (ImageButton)findViewById(R.id.AddNumber_Add);
        mEmpty = (TextView)findViewById(R.id.AddNumber_Empty);
        mEditNumber = (EditText)findViewById(R.id.AddNumber_Edit);
        mList = (ListView)findViewById(R.id.AddNumber_List);
        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mList.setAdapter(mListAdapter);

        mService = new RequestService(this, this);

        registerForContextMenu(mList);

        setInterface();

        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                mItemIndex = position;
                return false;
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if(position < 0)
                    return;

                if(position >= Settings.mNumbers.size())
                    return;

                if(Settings.mMSISDN.equals(Settings.mNumbers.get(position)))
                {
                    Toast.makeText(AddNumber.this, R.string.Error_wrong_choose_number, Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddNumber.this);
                dialog.setTitle(R.string.dialog_title_warning);
                dialog.setMessage(AddNumber.this.getString(R.string.dialog_choose_number_message) + Settings.mNumbers.get(position) + " ?");
                dialog.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //Сохранить номер как текущий
                        Settings.mMSISDN = Settings.mNumbers.get(position);
                        mService.getSaver().saveMSISDN(Settings.mMSISDN);
                        mListAdapter.clear();
                        for(String s : Settings.mNumbers)
                        {
                            if(s.equals(Settings.mMSISDN))
                                s += getString(R.string.add_number_yournumber);
                            mListAdapter.add(s);
                        }
                    }
                });
                dialog.setNegativeButton(R.string.btn_no, null);
                dialog.show();
            }
        });

        mBtnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*if(!isServiceBind)
                    return;*/

                if(mEditNumber.getText() == null || mEditNumber.getText().toString().length() == 0)
                {
                    Toast.makeText(AddNumber.this, R.string.Error_fill_fields, Toast.LENGTH_SHORT).show();
                    return;
                }

                String number = correctMSISDN(mEditNumber.getText().toString());
                if(number == null || number.length() == 0)
                    return;
                else
                    mEditNumber.setText(number);

                mService.addRequest(new JSONRequest(Settings.START_ATTACH_MSISDN, new JSONObject()));
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
        mEditNumber.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});

        mService.addRequest(new JSONRequest(Settings.GET_ATTACHED_MSISDN, new JSONObject()));
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
            mService.setInterface(AddNumber.this);
            mService.addRequest(new JSONRequest(Settings.GET_ATTACHED_MSISDN, new JSONObject()));
        }
    };*/

    @Override
    public void onAnswerOk(final String TAG)
    {
        if(TAG.equals(Settings.GET_ATTACHED_MSISDN))
        {
            AddNumber.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    setInterface();
                }
            });
        }
        if(TAG.equals(Settings.START_ATTACH_MSISDN))
        {
            AddNumber.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    createDialog(TAG);
                }
            });
        }
        if(TAG.equals(Settings.REQ_ATTACH_MSISDN))
        {
            Settings.isBanned = false;
            AddNumber.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    createDialog(TAG);
                }
            });
        }
        if(TAG.equals(Settings.CONFIRM_ATTACH_MSISDN))
        {
            AddNumber.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mEditNumber.setText("");
                }
            });
            mService.addRequest(new JSONRequest(Settings.GET_ATTACHED_MSISDN, new JSONObject()));
        }
        if(TAG.equals(Settings.DETACH_MSISDN))
        {
            AddNumber.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(AddNumber.this, R.string.add_number_delete_ok, Toast.LENGTH_SHORT).show();
                }
            });

            mService.addRequest(new JSONRequest(Settings.GET_ATTACHED_MSISDN, new JSONObject()));
        }
    }

    @Override
    public void onAnswerError(final String TAG, final String message)
    {
        if(TAG.equals("Banned"))
            Settings.isBanned = true;
        if(TAG.equals(Settings.CONFIRM_ATTACH_MSISDN))
        {
            if(mNumberError < 2)    //Даем 3 попытки на ввод капчи
            {
                mNumberError +=1;
                AddNumber.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //Принудительно заставляем создать диалог 3 раза для попыток ввести код с смски
                        createDialog(Settings.REQ_ATTACH_MSISDN);
                    }
                });
            }
            else
            {
                AddNumber.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mEditNumber.setText("");
                    }
                });
            }
        }
        if(TAG.equals("Banned"))
        {
            AddNumber.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mEditNumber.setText("");
                }
            });
        }

        AddNumber.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(AddNumber.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAnswerOther(String TAG)
    {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(mEditNumber.getText() != null && mEditNumber.getText().length() > 0)
            outState.putString("Add_number", mEditNumber.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        String number = savedInstanceState.getString("Add_number");
        if(number == null)
            number = "";
        mEditNumber.setText(number);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        //super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId())
        {
            case R.id.AddNumber_List:
                menu.add(0, 1, 0, R.string.contextmenu_delete);
                break;
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                deleteItem();
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public void deleteItem()
    {
        if(Settings.mMSISDN.equals(Settings.mNumbers.get(mItemIndex)))
        {
            Toast.makeText(AddNumber.this, R.string.Error_delete_same_number, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> jsonFields = new HashMap<String, String>();
        jsonFields.put(RequestService.REQ_MSISDN, Settings.mNumbers.get(mItemIndex));
        mItemIndex = -1;
        mService.addRequest(new JSONRequest(Settings.DETACH_MSISDN, new JSONObject(jsonFields)));
    }

    private void createDialog(final String TAG)
    {
        int mBtnRes = R.string.btn_attach;

        if(TAG.equals(Settings.START_ATTACH_MSISDN) && Settings.mType.equals("code"))
        {
            HashMap<String, String> jsonFields = new HashMap<String, String>();
            String number = correctMSISDN(mEditNumber.getText().toString());

            if(number == null || number.length() == 0)
                return;

            //noinspection ConstantConditions
            jsonFields.put(RequestService.REQ_MSISDN, "+" + number);
            //noinspection ConstantConditions
            jsonFields.put(RequestService.REQ_CAPTCHA_CODE, Settings.mTextCaptcha);
            String locale = "EN";
            if(Settings.isRusLocale)
                locale = "RU";
            jsonFields.put(RequestService.REQ_LOCALE, locale);

            mService.addRequest(new JSONRequest(Settings.REQ_ATTACH_MSISDN, new JSONObject(jsonFields)));
            return;
        }

        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        final RelativeLayout mContent = (RelativeLayout)getLayoutInflater().inflate(R.layout.dialog_add_number, null);
        final TextView mText = (TextView)mContent.findViewById(R.id.Dialog_Message);
        final ImageView mCaptcha = (ImageView)mContent.findViewById(R.id.Dialog_Captcha);
        final EditText mEdit = (EditText)mContent.findViewById(R.id.Dialog_Edit);

        if(TAG.equals(Settings.START_ATTACH_MSISDN))
        {
            if(Settings.isBanned)
                mBtnRes = R.string.btn_check;
            else
                mBtnRes = R.string.btn_send_code;
            mText.setText(R.string.dialog_captcha_text);
            mCaptcha.setVisibility(View.VISIBLE);
            setupInterfaceCaptcha(mCaptcha, mEdit);
        }
        else if(TAG.equals(Settings.REQ_ATTACH_MSISDN))
        {
            mText.setText(R.string.dialog_sms_text);
            mCaptcha.setVisibility(View.GONE);
        }

        mDialog.setView(mContent);
        mDialog.setPositiveButton(mBtnRes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                /*if(!isServiceBind)
                    return;*/
                /*if(mEdit.getText() == null || mEdit.getText().toString().length()== 0)
                {
                    Toast.makeText(AddNumber.this, R.string.Error_fill_fields, Toast.LENGTH_SHORT).show();
                    return;
                }*/

                if(TAG.equals(Settings.START_ATTACH_MSISDN))
                {
                    HashMap<String, String> jsonFields = new HashMap<String, String>();
                    //noinspection ConstantConditions
                    jsonFields.put(RequestService.REQ_MSISDN, "+" + mEditNumber.getText().toString());
                    //noinspection ConstantConditions
                    jsonFields.put(RequestService.REQ_CAPTCHA_CODE, mEdit.getText().toString());

                    mService.addRequest(new JSONRequest(Settings.REQ_ATTACH_MSISDN, new JSONObject(jsonFields)));
                }
                else if(TAG.equals(Settings.REQ_ATTACH_MSISDN))
                {
                    HashMap<String, String> jsonFields = new HashMap<String, String>();
                    //noinspection ConstantConditions
                    jsonFields.put(RequestService.REQ_MSISDN, "+" + mEditNumber.getText().toString());
                    //noinspection ConstantConditions
                    jsonFields.put(RequestService.REQ_SMS_CODE, mEdit.getText().toString());
                    mService.addRequest(new JSONRequest(Settings.CONFIRM_ATTACH_MSISDN, new JSONObject(jsonFields)));
                }
            }
        });
        mDialog.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void setInterface()
    {
        if(Settings.mNumbers.size() == 0)
        {
            mList.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            mList.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            //mListAdapter.getCount();
            mListAdapter.clear();
            for(String s : Settings.mNumbers)
            {
                if(s.equals(Settings.mMSISDN))
                    s += getString(R.string.add_number_yournumber);
                mListAdapter.add(s);
            }
        }
    }

    private void setupInterfaceCaptcha(ImageView mPicCaptcha, EditText mEditCaptcha)
    {
        if(Settings.mType.equals("code"))
        {
            mPicCaptcha.setVisibility(View.GONE);
            mEditCaptcha.setText(Settings.mTextCaptcha);
        }
        else if(Settings.mType.contains("image"))
        {
            ByteArrayInputStream in = new ByteArrayInputStream(Settings.mData);
            mPicCaptcha.setBackgroundDrawable(Drawable.createFromStream(in, "captcha"));
        }
    }

    private String correctMSISDN(String number)
    {
        String mMsisdn;

        if(number == null || number.length() == 0)
            return null;

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
            Toast.makeText(AddNumber.this, R.string.Error_wrong_number, Toast.LENGTH_SHORT).show();
            return null;
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
                Toast.makeText(AddNumber.this, R.string.Error_wrong_number, Toast.LENGTH_SHORT).show();
                return null;
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

        mMsisdn = numberCode + numberBody;
        Debug.i("Phone = " + mMsisdn);
        return mMsisdn;
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

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
