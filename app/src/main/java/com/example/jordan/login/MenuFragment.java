package com.example.jordan.login;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private int initialX;
    private ViewGroup.MarginLayoutParams par;
    private TableRow tr;
    private View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_menu, container, false);

        tr = (TableRow) v.findViewById(R.id.menu_row_1);
        initialX = ((ViewGroup.MarginLayoutParams) tr.getLayoutParams()).leftMargin;


        tr.setOnTouchListener(new View.OnTouchListener() {

            float prevX;
            private static final int MAX_CLICK_DURATION = 100;
            private long startClickTime;

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {

                TableRow tr = (TableRow) v.findViewById(R.id.menu_row_1);

                par = (ViewGroup.MarginLayoutParams) tr.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE: {
                        float xCoord = event.getRawX();
                        par.leftMargin += xCoord - prevX;
                        par.rightMargin -= xCoord - prevX;
                        prevX = event.getRawX();
                        v.setLayoutParams(par);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if (clickDuration < MAX_CLICK_DURATION) {
                            ((WelcomeScreen) getContext()).logout(v);
                            return true;
                        }
                        float xCoord = event.getRawX();
                        par.leftMargin += xCoord - prevX;
                        par.rightMargin -= xCoord - prevX;

                        snapBack();
                        return true;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        prevX = event.getRawX();
                        v.setLayoutParams(par);
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }

    private void snapBack() {
        if (Math.abs(par.leftMargin) > tr.getWidth() / 2) {
            removeRow();
            return;
        }
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", par.leftMargin, 20);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", par.rightMargin, 20);

        final ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(pvhLeft, pvhRight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                par.leftMargin = (int) animator.getAnimatedValue("left");
                par.rightMargin = (int) animator.getAnimatedValue("right");
                tr.requestLayout();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    private void removeRow() {
        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", par.leftMargin, tr.getWidth()*2);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", par.rightMargin, 0 - tr.getWidth());
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1, 0);

        final ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(pvhLeft, pvhRight, alpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                par.leftMargin = (int) animator.getAnimatedValue("left");
                par.rightMargin = (int) animator.getAnimatedValue("right");
                tr.setAlpha((float) animator.getAnimatedValue("alpha"));
                tr.requestLayout();
            }
        });
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                TableLayout tl = (TableLayout) v.findViewById(R.id.menu_container);
                tl.removeView(tl.getChildAt(0));
            }
        });
        animator.start();
    }
}
