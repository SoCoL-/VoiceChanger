package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import ru.mobstudio.voicechanger.httpCore.JSONRequest;
import ru.mobstudio.voicechanger.Interface.IResponce;
import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Debug;
import ru.mobstudio.voicechanger.Utils.IabHelper;
import ru.mobstudio.voicechanger.Utils.IabResult;
import ru.mobstudio.voicechanger.Utils.Inventory;
import ru.mobstudio.voicechanger.Utils.Purchase;
import ru.mobstudio.voicechanger.httpCore.RequestService;

/**
 * Created by Evgenij on 18.11.13.
 *
 */
public class FillBalance extends Activity implements IResponce
{
    //-----------------------------
    //Constants
    //-----------------------------

    private static final String ITEM1 = "buy_five_minutes";
    private static final String ITEM2 = "buy_10_minutes";
    private static final String ITEM3 = "buy_20_minutes";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    //-----------------------------
    //Variables
    //-----------------------------

    // The helper object
    IabHelper mHelper;
    /*private AppService mService;
    private boolean isServiceBind;*/
    private RequestService mService;
    private boolean isFirstError = true;
    private Handler mErrorHandler;
    private Runnable mErrorRunnable;

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fill_balance);

        /*Intent mServiceIntent = new Intent(this, AppService.class);
        bindService(mServiceIntent, mConnect, Context.BIND_AUTO_CREATE);*/
        mService = new RequestService(this, this);

        Button mPayItem1 = (Button)findViewById(R.id.Fill_Item1);
        Button mPayItem2 = (Button)findViewById(R.id.Fill_Item2);
        Button mPayItem3 = (Button)findViewById(R.id.Fill_Item3);

        Button mPaySMS = (Button)findViewById(R.id.Fill_SMS);
        mErrorHandler = new Handler();

        if(!Settings.isRussianMSISDN)
            mPaySMS.setVisibility(View.GONE);

        if(!Settings.isConnectInternet || Settings.isSmsWork)
        {
            mPayItem1.setVisibility(View.GONE);
            mPayItem2.setVisibility(View.GONE);
            mPayItem3.setVisibility(View.GONE);
        }

        if(Settings.isConnectInternet)
        {
            String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgTEoFTDJszBfjpyNNeVFe9xbT5RXSPNLQAG1sxnFS6QJur9K/NyV1zPpmIqCiquI1GfyLuWqA5e16u+L+uhKo8GloQ7rD+l/M5K9WNBwFNiwbNXiJRb0nxJwRDTpce+3obn8OWr0o+1ibFDQRrBm7HLBUQtRjoA3rH3U/RFP7nqJFZA+auOXDDsw/i3aXePsZmbKsh+jLflB3kglhS1D8PHWIbtn7dWAizGCkGoWF+EIKtWHG263D/WKi91rWochWTLJoYsyyFOofLfg4d8n6ZMTAnFhgdJpeYtyhHC+wnC2HWmIbq6u4J5+2Su8SdN7K25UV5Cu1sYeRI/QLLLnYwIDAQAB";

            mHelper = new IabHelper(this, base64EncodedPublicKey);

            // enable debug logging (for a production application, you should set this to false).
            mHelper.enableDebugLogging(true);

            // Start setup. This is asynchronous and the specified listener
            // will be called once setup completes.
            Debug.i("Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
            {
                public void onIabSetupFinished(IabResult result)
                {
                    Debug.i("Setup finished.");

                    if (!result.isSuccess())
                    {
                        // Oh noes, there was a problem.
                        //complain("Problem setting up in-app billing: " + result);
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Debug.i("Setup successful. Querying inventory.");
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            });
        }

        mPayItem1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Debug.i("Launching purchase flow for ITEM1.");

                if(!Settings.isConnectInternet)
                {
                    Toast.makeText(FillBalance.this, R.string.Error_internet_connect, Toast.LENGTH_SHORT).show();
                    return;
                }

                /* TODO: for security, generate your payload here for verification. See the comments on
                 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                 *        an empty string, but on a production app you should carefully generate this. */
                String payload = "0";

                mHelper.launchPurchaseFlow(FillBalance.this, ITEM1, RC_REQUEST, mPurchaseFinishedListener, payload);
            }
        });

        mPayItem2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Debug.i("Launching purchase flow for ITEM1.");

                if(!Settings.isConnectInternet)
                {
                    Toast.makeText(FillBalance.this, R.string.Error_internet_connect, Toast.LENGTH_SHORT).show();
                    return;
                }

                /* TODO: for security, generate your payload here for verification. See the comments on
                 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                 *        an empty string, but on a production app you should carefully generate this. */
                String payload = "1";

                mHelper.launchPurchaseFlow(FillBalance.this, ITEM2, RC_REQUEST, mPurchaseFinishedListener, payload);
            }
        });

        mPayItem3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Debug.i("Launching purchase flow for ITEM1.");

                if(!Settings.isConnectInternet)
                {
                    Toast.makeText(FillBalance.this, R.string.Error_internet_connect, Toast.LENGTH_SHORT).show();
                    return;
                }

                /* TODO: for security, generate your payload here for verification. See the comments on
                 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
                 *        an empty string, but on a production app you should carefully generate this. */
                String payload = "2";

                mHelper.launchPurchaseFlow(FillBalance.this, ITEM3, RC_REQUEST, mPurchaseFinishedListener, payload);
            }
        });

        mPaySMS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uri uri = Uri.parse(Settings.TAG_SMS_ADDRESS);
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra(Settings.TAG_SMS_BODY, getString(R.string.sms_create_fill));
                startActivity(it);
            }
        });
    }

    //-----------------------------
    //Methods
    //-----------------------------

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            Debug.i("Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure())
            {
                //complain("Failed to query inventory: " + result);
                return;
            }

            Debug.i("Query inventory was successful.");
            Debug.i("Initial inventory query finished; enabling main UI.");
        }
    };

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            Debug.i("Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure())
            {
                //complain("Error purchasing: " + result);
                Debug.i("Error purchasing: " + result);
                Toast.makeText(FillBalance.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                //setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase))
            {
                //complain("Error purchasing. Authenticity verification failed.");
                //setWaitScreen(false);
                return;
            }

            Debug.i("Purchase successful.");

            if (purchase.getSku().equals(ITEM1) || purchase.getSku().equals(ITEM2) || purchase.getSku().equals(ITEM3))
            {
                // bought 1/4 tank of gas. So consume it.
                Debug.i("Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
    {
        public void onConsumeFinished(Purchase purchase, IabResult result)
        {
            Debug.i("Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess())
            {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Debug.i("Consumption successful. Provisioning.");
                Debug.i("Signature = " + purchase.getSignature());
                Debug.i("Data = " + purchase.getOriginalJson());
                //TODO отправка данных на сервак о покупке

                if(Settings.isConnectInternet)
                {
                    /** The | delimited response data from the licensing server
                        $responseData = $receipt['responseData'];
                        //The signature provided with the response data (Base64)
                        $signature = $receipt['signature'];
                     */
                    HashMap<String, String> jsonFields1 = new HashMap<String, String>();
                    jsonFields1.put(RequestService.REQ_RESPONCE_DATA, purchase.getOriginalJson());
                    jsonFields1.put(RequestService.REQ_SIGNATURE, purchase.getSignature());
                    Settings.mJSON = purchase.getOriginalJson();
                    Settings.mSignature = purchase.getSignature();
                    mService.getSaver().saveBuyData(purchase.getSignature(), purchase.getOriginalJson());
                    Debug.i("Данные для Base64 = " + new JSONObject(jsonFields1).toString());
                    String data = android.util.Base64.encodeToString(new JSONObject(jsonFields1).toString().getBytes(), android.util.Base64.NO_WRAP);
                    HashMap<String, String> jsonFields = new HashMap<String, String>();
                    jsonFields.put(RequestService.REQ_PURCHASE_DATA, data);

                    mService.addRequest(new JSONRequest(Settings.REFILL_BALANCE, new JSONObject(jsonFields)));
                }
            }
            else
            {
                //complain("Error while consuming: " + result);
                Debug.i("Error while consuming: " + result);
                Toast.makeText(FillBalance.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
            //updateUi();
            //setWaitScreen(false);
            Debug.i("End consumption flow.");
        }
    };

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
            mService.setInterface(FillBalance.this);
        }
    };*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Debug.i("onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data))
        {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
            Debug.i("onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p)
    {
        String payload = p.getDeveloperPayload();


        Debug.i("payLoad = " + payload);


        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    @Override
    public void onAnswerOk(String TAG)
    {
        if(TAG.equals(Settings.REFILL_BALANCE))
        {
            FillBalance.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Settings.mJSON = null;
                    Settings.mSignature = null;
                    mService.getSaver().saveBuyData("", "");
                    Toast.makeText(FillBalance.this, R.string.fill_balance_fill_ok, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onAnswerError(String TAG, final String message)
    {
        FillBalance.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                //задержка на 3 сек и повторная отправка
                if(isFirstError)
                {
                    isFirstError = false;
                    mErrorRunnable = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            HashMap<String, String> jsonFields1 = new HashMap<String, String>();
                            jsonFields1.put(RequestService.REQ_RESPONCE_DATA, Settings.mJSON);
                            jsonFields1.put(RequestService.REQ_SIGNATURE, Settings.mSignature);
                            Debug.i("Error: Данные для Base64 = " + new JSONObject(jsonFields1).toString());
                            String data = android.util.Base64.encodeToString(new JSONObject(jsonFields1).toString().getBytes(), android.util.Base64.NO_WRAP);
                            HashMap<String, String> jsonFields = new HashMap<String, String>();
                            jsonFields.put(RequestService.REQ_PURCHASE_DATA, data);

                            mService.addRequest(new JSONRequest(Settings.REFILL_BALANCE, new JSONObject(jsonFields)));
                        }
                    };
                    mErrorHandler.postDelayed(mErrorRunnable, 3000);
                }
                else//если и второй раз ошибка, то сообщение
                {
                    Toast.makeText(FillBalance.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAnswerOther(String TAG) {

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
