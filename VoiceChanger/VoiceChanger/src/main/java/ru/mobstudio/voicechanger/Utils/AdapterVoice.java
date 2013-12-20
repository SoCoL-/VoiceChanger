package ru.mobstudio.voicechanger.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mobstudio.voicechanger.R;
import ru.mobstudio.voicechanger.Settings;

/**
 * Created by Evgenij on 15.11.13.
 *
 */
public class AdapterVoice extends BaseAdapter
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private final MediaPlayer mPlayer;
    private final Context mContext;
    private final LayoutInflater inflater;
    private ArrayList<String> mNames;

    //-----------------------------
    //Ctors
    //-----------------------------

    public AdapterVoice(Context c, MediaPlayer mp)
    {
        this.mContext = c;
        this.mPlayer = mp;
        mNames = new ArrayList<String>(10);
        mNames.add(mContext.getString(R.string.voice_1));
        mNames.add(mContext.getString(R.string.voice_2));
        mNames.add(mContext.getString(R.string.voice_3));
        mNames.add(mContext.getString(R.string.voice_4));
        mNames.add(mContext.getString(R.string.voice_5));
        mNames.add(mContext.getString(R.string.voice_6));
        mNames.add(mContext.getString(R.string.voice_7));
        mNames.add(mContext.getString(R.string.voice_8));
        mNames.add(mContext.getString(R.string.voice_9));
        mNames.add(mContext.getString(R.string.voice_10));

        //Получаем область видимости ресурсов
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //-----------------------------
    //Methods
    //-----------------------------

    @Override
    public int getCount()
    {
        return mNames.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mNames.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.item_adapter, parent, false);

        FrameLayout container = (FrameLayout)convertView.findViewById(R.id.Adapter_Container);
        container.setBackgroundResource(Settings.getBackgroundColor(position));
        ImageView mIcon = (ImageView)convertView.findViewById(R.id.Adapter_Pic);
        mIcon.setBackgroundResource(Settings.getBackgroundByID(position));
        final ImageView mPlay = (ImageView)convertView.findViewById(R.id.Adapter_Play);
        mPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!mPlayer.isPlay())
                {
                    mPlayer.stopPlayer();
                    mPlayer.PlayMusic(Settings.getVoiceByID(position));
                    mPlay.setImageResource(R.drawable.ic_pause_np);
                }
                else
                {
                    mPlay.setImageResource(R.drawable.ic_play);
                    mPlayer.stopPlayer();
                }
            }
        });

        android.media.MediaPlayer.OnCompletionListener ocl = new android.media.MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(android.media.MediaPlayer mp)
            {
                mPlay.setImageResource(R.drawable.ic_play);
            }
        };

        mPlayer.setCopleteInterface(ocl);

        TextView mName = (TextView)convertView.findViewById(R.id.Adapter_Text);
        mName.setText(Settings.getNameByID(mContext, position));

        return convertView;
    }

    public void stopPlay()
    {
        mPlayer.stopPlayer();
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
