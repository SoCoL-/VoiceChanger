package ru.mobstudio.voicechanger.Utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import ru.mobstudio.voicechanger.Interface.OnClearData;

/**
 * Created by Evgenij on 27.11.13.
 *
 */
public class EditWithDrawable extends EditText
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private Drawable mRight;
    private Rect mBound;
    private OnClearData mListener;

    //-----------------------------
    //Ctors
    //-----------------------------

    public EditWithDrawable(Context context) { super(context); }

    public EditWithDrawable(Context context, AttributeSet attrs) { super(context, attrs); }

    public EditWithDrawable(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    //-----------------------------
    //Methods
    //-----------------------------

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom)
    {
        if(right != null)
            mRight = right;

        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        if(event.getAction() == MotionEvent.ACTION_UP && mRight!=null)
        {
            mBound = mRight.getBounds();
            final int x = (int)event.getX();
            final int y = (int)event.getY();
            //check to make sure the touch event was within the bounds of the drawable
            if(x >= (this.getRight() - mBound.width() - 5) && x <= (this.getRight() - this.getPaddingRight() + 5)
                    && y >= this.getPaddingTop() - 5 && y <= (this.getHeight()-this.getPaddingBottom() + 5))
            {
                this.setText("");
                if(mListener != null)
                    mListener.clear();
                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable
    {
        mRight = null;
        mBound = null;
        mListener = null;
        super.finalize();
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public void setListener(OnClearData listener)
    {
        this.mListener = listener;
    }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
