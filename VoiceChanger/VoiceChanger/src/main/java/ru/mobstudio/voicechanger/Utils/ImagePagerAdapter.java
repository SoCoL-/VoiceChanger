package ru.mobstudio.voicechanger.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.mobstudio.voicechanger.R;

/**
 * Created by Evgenij on 27.11.13.
 *
 */
public class ImagePagerAdapter extends PagerAdapter
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private int[] mSetOfImage;
    private int mMethodShow;

    //-----------------------------
    //Ctors
    //-----------------------------

    //-----------------------------
    //Methods
    //-----------------------------

    public void setMethod(int m)
    {
        this.mMethodShow = m;
    }

    public void addItems(int[] items)
    {
        mSetOfImage = items;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View v = View.inflate(container.getContext(), R.layout.pager_view, null);
        container.addView(v, 0);

        ImageView iv = (ImageView)v.findViewById(R.id.Pager_Img);

        if(position < mSetOfImage.length)
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inSampleSize = 2;
            Bitmap bmp = BitmapFactory.decodeResource(container.getResources(), mSetOfImage[position], o);
            //iv.setBackgroundResource(mSetOfImage[position]);
            iv.setBackgroundDrawable(new BitmapDrawable(container.getResources(), bmp));
        }

        return v;
    }

    @Override
    public int getCount()
    {
        if(mMethodShow == 1)
        {
            return 1;
        }
        if(mMethodShow == 2)
        {
            return 4;
        }
        if(mMethodShow == 3)
        {
            return 1;
        }
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object o)
    {
        return view.equals(o);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
