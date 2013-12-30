package ru.mobstudio.voicechanger.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mobstudio.voicechanger.R;

/**
 * Created by Evgenij on 25.11.13.
 *
 */
public class AdapterCountry extends BaseAdapter
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private final LayoutInflater inflater;
    private ArrayList<CountryInfo> mInfo;

    //-----------------------------
    //Ctors
    //-----------------------------

    public AdapterCountry(Context c)
    {
        mInfo = new ArrayList<CountryInfo>();

        //Получаем область видимости ресурсов
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //-----------------------------
    //Methods
    //-----------------------------

    public void addInfos(ArrayList<CountryInfo> infos)
    {
        if(mInfo != null)
        {
            mInfo.addAll(infos);
            notifyDataSetChanged();
        }
    }

    public void clear()
    {
        if(mInfo != null)
        {
            mInfo.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount()
    {
        return mInfo.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mInfo.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.country_adapter, parent, false);

        TextView name = (TextView)convertView.findViewById(R.id.Country_Name);

        String data = mInfo.get(position).getPrefix();
        int len = data.length();
        while(len <= 5)
        {
            data += " ";
            len++;
        }

        name.setText(data + " - " + mInfo.get(position).getNameCountry());

        return convertView;
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
