package ru.mobstudio.voicechanger;

import android.content.Context;

import java.util.ArrayList;

import ru.mobstudio.voicechanger.Utils.CountryInfo;

/**
 * Created by Evgenij on 11.11.13.
 *
 */
public class Settings
{
    //-----------------------------
    //Constants
    //-----------------------------

    public final static boolean IS_DEBUG = true;
    public final static boolean IS_DELETE_USER = false;

    public final static String CHECK_AUTH               = "checkAuth.php";
    public final static String DELETE_USER_ID           = "deleteUserId.php";             //ТОлько для тестов
    public final static String REQ_ATTACH_MSISDN        = "requestAttachMSISDN.php";
    public final static String CONFIRM_ATTACH_MSISDN    = "confirmAttachMSISDN.php";
    public final static String CHECK_BALANCE            = "checkBalance.php";
    public final static String REFILL_BALANCE           = "refillBalance.php";
    public final static String PERFORM_CALLBACK         = "performCallBack.php";
    public final static String GET_ATTACHED_MSISDN      = "getAttachedMSISDNs.php";
    public final static String START_ATTACH_MSISDN      = "startAttachMSISDN.php";
    public final static String DETACH_MSISDN            = "requestDetachMSISDN.php";
    public final static String CHECK_COUNTRY_LIST       = "checkCountryList.php";
    public final static String GET_COUNTRY_LIST         = "getCountryList.php";
    public final static String REQUEST_AUTH             = "requestAuth.php";
    public final static String CONFIRM_AUTH             = "confirmAuth.php";
    public final static String CHECK_CALL_STATUS        = "checkCallStatus.php";
    public final static String CHECK_API                = "version.php";

    public final static String RECEIVE_SMS = "ru.mobstudio.voicechanger.receive_sms";
    public final static String TAG_STORE_SMS = "pass";

    public final static String TYPE_STORE = "google";

    public final static String TAG_SAVE_MSISDN          = "MSISDN";
    public final static String TAG_SAVE_SECRET          = "Secret";
    public final static String TAG_SAVE_TIME            = "Time";
    public final static String TAG_SAVE_COUNTRIES       = "Countries";
    public final static String TAG_SAVE_WORKSMS         = "work_sms";
    public final static String TAG_SAVE_WORKFULL        = "work_full";
    public final static String TAG_SAVE_SIGNATURE       = "signature";
    public final static String TAG_SAVE_JSON            = "json";
    public final static String TAG_SAVE_HELP            = "help";
    public final static String TAG_SAVE_HELP_CALL       = "help_call";
    public final static String TAG_SAVE_HELP_SMS        = "help_sms";
    public final static String TAG_SAVE_CAPTCHA_TYPE    = "captcha_type";
    public final static String TAG_SAVE_CAPTCHA_DATA    = "captcha_data";
    public final static String TAG_SAVE_CAPTCHA_TEXT    = "captcha_text";

    public final static String TAG_SMS_BODY         = "sms_body";
    public final static String TAG_SMS_ADDRESS      = "smsto:4030";

    public final static String TAG_EVENT_FINAL      = "FINAL_REPORT";
    public final static String TAG_EVENT_CALL_A     = "START_CALL_A";
    public final static String TAG_EVENT_FAIL_A     = "FAIL_CALL_A";
    public final static String TAG_EVENT_PICKUP_A   = "PICKUP_CALL_A";
    public final static String TAG_EVENT_CALL_B     = "START_CALL_B";
    public final static String TAG_EVENT_DISCARD_A  = "DISCART_CALL_A";
    public final static String TAG_EVENT_FAIL_B     = "FAIL_CALL_B";
    public final static String TAG_EVENT_PICKUP_B   = "PICKUP_CALL_B";
    public final static String TAG_EVENT_WAIT       = "WAIT_BACKEND";

    public final static String API_STATE_ACTUAL     = "actual";
    public final static String API_STATE_DEPRECATED = "deprecated";
    public final static String API_STATE_CLOSE      = "close";

    public final static String CURRENT_VERSION      = "v1";


    public final static int mSetOfImagesFull[] =
            {
                    R.drawable.hint_common,
                    R.drawable.hint_voice,
                    R.drawable.hint_call,
                    R.drawable.hint_sms,
            };

    /*public final static int mHelpReg[] = {R.drawable.abc_ic_search};
    public final static int mHelpCall[] = {R.drawable.hint_call};
    public final static int mHelpSMS[] = {R.drawable.hint_sms};*/

    //-----------------------------
    //Variables
    //-----------------------------

    //Эти данные передаем в шапке каждого пост запроса
    public static String mUserID = "";                                                  //Логин пользователя, первая часть почты гугла
    public static String mSecret = "";                                                  //Пароль от сервака
    //Дополнительные данные для работы приложения
    public static String mMSISDN = "";                                                  //Текущий номер телефона
    public static String mBalance = "";                                                 //Текущий баланс пользователя
    public static int mVoiceID = -1;                                                    //Идентификатор выбранного искажения голоса
    public static long mTimeUpdate = 0l;                                                //Время последнего изменения списка городов
    public static long mCurrentTimeUpdate = 0l;                                         //Текущее время последнего изменения списка городов
    public static boolean isRusLocale;                                                  //Для локализации звуковых семплов
    public static boolean isRussianMSISDN;                                              //Определяем принадлежность номера регистрации к России
    public static boolean isConnectInternet;                                            //Определение подключения к интернету
    public static ArrayList<String> mNumbers = new ArrayList<String>();                 //Список номеров, привязанных к аккаунту
    public static ArrayList<CountryInfo> mCountryInfo = new ArrayList<CountryInfo>();   //Список информации по городам, до которых можем дозвониться
    public static String mCountryList = "";                                             //Список информации по городам, до которых можем дозвониться из сохранения
    public static boolean isSmsWork = false;                                            //Выбор в начале работы приложения режима работы через смс
    public static boolean isFullWork = false;                                           //Выбор в начале работы приложения полного режима работы
    public static String mSignature = "";                                               //Если покупка прошла на сервере гугла, а до нашего не долетела по каким-то причинам, то отправим еще раз из сохранения
    public static String mJSON = "";                                                    //Если покупка прошла на сервере гугла, а до нашего не долетела по каким-то причинам, то отправим еще раз из сохранения
    public static String mCurrCallID = "";                                              //Текущий идентификатор звонка
    public static String mCallMessage = "";                                             //Текстовое значение состояния звонка
    public static boolean isStopUpdate = false;                                         //Если статус звонка конечный, от отключим автообновление и включим таймер очистки
    public static String mAPIState = "";                                                //Состояние АПИ
    public static boolean isBanned = false;

    //Данные капчи
    public static String mType = "";
    public static String mTextCaptcha = "";
    public static byte[] mData;

    //Блок переменных для подсказок
    public static int mCurrMethod = 0;                                                  //Тип подсказки: 0 - подсказка на регистрацию перед входом, 1 - подсказка на регистрацию перед смс кодом, 2 - на кнопку звонка без смс/по смс (3), 5 - все подсказки
    public static boolean isHelpCall = false;                                           //Показывать ли подсказку на нажатие кнопки позвонить
    public static boolean isHelpCallSMS = false;                                        //Показывать ли подсказку на нажатие кнопки позвонить с смс


    //-----------------------------
    //Ctors
    //-----------------------------

    //-----------------------------
    //Methods
    //-----------------------------

    public static String getNameByID(Context c, int id)
    {
        String rez = "";

        switch (id)
        {
            case 0:
                rez = c.getString(R.string.voice_1);
                break;
            case 1:
                rez = c.getString(R.string.voice_2);
                break;
            case 2:
                rez = c.getString(R.string.voice_3);
                break;
            case 3:
                rez = c.getString(R.string.voice_4);
                break;
            case 4:
                rez = c.getString(R.string.voice_5);
                break;
            case 5:
                rez = c.getString(R.string.voice_6);
                break;
            case 6:
                rez = c.getString(R.string.voice_7);
                break;
            case 7:
                rez = c.getString(R.string.voice_8);
                break;
            case 8:
                rez = c.getString(R.string.voice_9);
                break;
            case 9:
                rez = c.getString(R.string.voice_10);
                break;
        }

        return rez;
    }

    public static int getVoiceByID(int id)
    {
        int rez = 0;

        switch (id)
        {
            case 0:
                if(isRusLocale)
                    rez = R.raw.ded_moroz;
                else
                    rez = R.raw.ded_moroz_eng;
                break;
            case 1:
                if(isRusLocale)
                    rez = R.raw.zlodey;
                else
                    rez = R.raw.zlodey_eng;
                break;
            case 2:
                if(isRusLocale)
                    rez = R.raw.gelium;
                else
                    rez = R.raw.gelium_eng;
                break;
            case 3:
                if(isRusLocale)
                    rez = R.raw.man;
                else
                    rez = R.raw.man_eng;
                break;
            case 4:
                if(isRusLocale)
                    rez = R.raw.woman;
                else
                    rez = R.raw.woman_eng;
                break;
            case 5:
                if(isRusLocale)
                    rez = R.raw.mult;
                else
                    rez = R.raw.mult_eng;
                break;
            case 6:
                if(isRusLocale)
                    rez = R.raw.bur;
                else
                    rez = R.raw.bur_eng;
                break;
            case 7:
                if(isRusLocale)
                    rez = R.raw.echo;
                else
                    rez = R.raw.echo_eng;
                break;
            case 8:
                if(isRusLocale)
                    rez = R.raw.allien;
                else
                    rez = R.raw.alien_eng;
                break;
            case 9:
                if(isRusLocale)
                    rez = R.raw.dragon;
                else
                    rez = R.raw.dragon_eng;
                break;
        }

        return rez;
    }

    public static int getBackgroundColor(int pos)
    {
        int rez = 0;

        switch (pos)
        {
            case 0:
                rez = R.color.color_ded;
                break;
            case 1://zlodey
                rez = R.color.color_zlodey;
                break;
            case 2://gelium
                rez = R.color.color_helium;
                break;
            case 3://man
                rez = R.color.color_men;
                break;
            case 4://woman
                rez = R.color.color_women;
                break;
            case 5://mult
                rez = R.color.color_mult;
                break;
            case 6://bur
                rez = R.color.color_bur;
                break;
            case 7://echo
                rez = R.color.color_echo;
                break;
            case 8://alien
                rez = R.color.color_alien;
                break;
            case 9://dracon
                rez = R.color.color_draco;
                break;
        }

        return rez;
    }

    public static int getBackgroundColorAlpha(int pos)
    {
        int rez = 0;

        switch (pos)
        {
            case 0://ded_moroz
                rez = R.color.color_ded_alpha;
                break;
            case 1://zlodey
                rez = R.color.color_zlodey_alpha;
                break;
            case 2://gelium
                rez = R.color.color_helium_alpha;
                break;
            case 3://man
                rez = R.color.color_men_alpha;
                break;
            case 4://woman
                rez = R.color.color_women_alpha;
                break;
            case 5://mult
                rez = R.color.color_mult_alpha;
                break;
            case 6://bur
                rez = R.color.color_bur_alpha;
                break;
            case 7://echo
                rez = R.color.color_echo_alpha;
                break;
            case 8://alien
                rez = R.color.color_alien_alpha;
                break;
            case 9://dracon
                rez = R.color.color_draco_alpha;
                break;
        }

        return rez;
    }

    public static int getBackgroundByID(int id)
    {
        int rez = 0;

        switch (id)
        {
            case 0:
                rez = R.drawable.ic_ded;
                break;
            case 1://zlodey
                rez = R.drawable.ic_zlodey;
                break;
            case 2://gelium
                rez = R.drawable.ic_gelium;
                break;
            case 3://man
                rez = R.drawable.ic_men;
                break;
            case 4://woman
                rez = R.drawable.ic_women;
                break;
            case 5://mult
                rez = R.drawable.ic_mult;
                break;
            case 6://bur
                rez = R.drawable.ic_bur;
                break;
            case 7:
                rez = R.drawable.ic_echo;
                break;
            case 8://alien
                rez = R.drawable.ic_alien;
                break;
            case 9://dracon
                rez = R.drawable.ic_draco;
                break;
        }

        return rez;
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
