package com.mikemiller.gymlog;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Mike on 3/29/14. COPYRIGHT OLIO.
 */
public class DoubleTapDetector extends GestureDetector.SimpleOnGestureListener {
    public Context context;
    public String phno;

    public DoubleTapDetector(Context con)
    {
        this.context=con;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}