package ru.mobstudio.voicechanger.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mobstudio.voicechanger.R;

/**
 * Created by Evgenij on 03.12.13.
 *
 */
public class AdapterPhones extends BaseAdapter
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private ArrayList<String> mPhones;
    private ArrayList<String> mLabels;
    private final LayoutInflater inflater;
    private final Context mCont;

    //-----------------------------
    //Ctors
    //-----------------------------

    public AdapterPhones(Context c)
    {
        mPhones = new ArrayList<String>();
        mLabels = new ArrayList<String>();

        //Получаем область видимости ресурсов
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCont = c;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    public void addPhones(Cursor phones)
    {
        if(mPhones == null)
            return;
        if(mLabels == null)
            return;

        fillData(phones);
        notifyDataSetChanged();
    }

    private void fillData(Cursor c)
    {
        do
        {
            String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phone != null && phone.length() >= 10)
            {
                mPhones.add(phone);
                String label = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String label_name = mCont.getString(R.string.label_mobile);
                if(label == null)
                    label_name = mCont.getString(R.string.label_mobile);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_HOME))
                    label_name = mCont.getString(R.string.label_home);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE))
                    label_name = mCont.getString(R.string.label_mobile);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME))
                    label_name = mCont.getString(R.string.label_home_fax);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_WORK))
                    label_name = mCont.getString(R.string.label_work);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK))
                    label_name = mCont.getString(R.string.label_work_fax);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_OTHER))
                    label_name = mCont.getString(R.string.label_other);
                else if(label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_PAGER) || label.equals(""+ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER))
                    label_name = mCont.getString(R.string.label_pager);

                mLabels.add(label_name);
            }
        }
        while (c.moveToNext());
    }

    public void clearAdapter()
    {
        if(mPhones == null)
            return;
        if(mLabels == null)
            return;

        mPhones.clear();
        mLabels.clear();

        notifyDataSetChanged();
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    @Override
    public int getCount()
    {
        return mPhones.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mPhones.get(position);
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
            convertView = inflater.inflate(R.layout.phone_item_adapter, parent, false);

        TextView mPhone = (TextView)convertView.findViewById(R.id.Phone_Phone);
        mPhone.setText(mPhones.get(position));

        TextView mLabel = (TextView)convertView.findViewById(R.id.Phone_Label);
        mLabel.setText(mLabels.get(position));

        return convertView;
    }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
