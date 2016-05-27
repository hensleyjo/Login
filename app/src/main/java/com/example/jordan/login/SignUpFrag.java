package com.example.jordan.login;


import android.app.Activity;
import android.os.Bundle;
import android.sax.RootElement;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        v.findViewById(R.id.sign_up_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                MainActivity.hideSoftKeyboard((Activity)v.getContext());
                return true;
            }
        });

        return v;
    }

}
