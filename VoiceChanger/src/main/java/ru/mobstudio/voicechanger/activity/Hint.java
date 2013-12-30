package ru.mobstudio.voicechanger.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;
import ru.mobstudio.voicechanger.Utils.Debug;
import ru.mobstudio.voicechanger.Utils.ImagePagerAdapter;

/**
 * Created by Evgenij on 22.11.13.
 * Будет показ слайдов для повтора подсказки
 */
public class Hint extends Activity
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private ImagePagerAdapter mAdapter;
    private Button mDone;
    private TextView mCount;

    //-----------------------------
    //Ctors
    //-----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hint_activity);

        mDone = (Button)findViewById(R.id.Hint_Done);
        mCount = (TextView)findViewById(R.id.Hint_Count);
        final ViewPager mPager = (ViewPager)findViewById(R.id.Hint_Container);
        mAdapter = new ImagePagerAdapter();
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(1);
        mPager.setPageMargin(0);

        updateInterfase();

        mDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mPager.getCurrentItem() !=  mAdapter.getCount()-1)
                {
                    mPager.setCurrentItem(mPager.getCurrentItem()+1);
                }
                else if(mPager.getCurrentItem() == mAdapter.getCount()-1)
                    finish();
            }
        });

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int i, float v, int i2){}

            @Override
            public void onPageSelected(int i)
            {
                Debug.i("Текущая позиция подсказки = " + i);
                /*if(i == mAdapter.getCount()-1)
                    mDone.setVisibility(View.VISIBLE);
                else
                    mDone.setVisibility(View.GONE);*/

                mCount.setText("" + (i+1) + "/" + mAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int i){}
        });
    }

    //-----------------------------
    //Methods
    //-----------------------------

    private void updateInterfase()
    {
        mAdapter.setMethod(Settings.mCurrMethod);

        /*if(Settings.mCurrMethod == 1)
            mAdapter.addItems(Settings.mHelpReg);*/
        if(Settings.mCurrMethod == 2)
            mAdapter.addItems(Settings.mSetOfImagesFull);
        /*if(Settings.mCurrMethod == 3)
            mAdapter.addItems(Settings.mHelpSMS);*/
        if(Settings.mCurrMethod == 5)
            mAdapter.addItems(Settings.mSetOfImagesFull);

        /*if(Settings.mCurrMethod == 5)
            mDone.setVisibility(View.GONE);
        else
            mDone.setVisibility(View.VISIBLE);

        if(mAdapter.getCount() == 1)
            mDone.setVisibility(View.VISIBLE);
            //mDone.setEnabled(true);
        else
            mDone.setVisibility(View.GONE);*/
            //mDone.setEnabled(false);

        mCount.setText("" + 1 + "/" + mAdapter.getCount());
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
