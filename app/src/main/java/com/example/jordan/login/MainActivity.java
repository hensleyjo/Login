package com.example.jordan.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Client.AsyncResponse {

    private CancellationSignal cs;
    private String userNameInput;
    private String passwordInput;
    private boolean fingerPrintUsed = false;
    private boolean touchLoginUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((RelativeLayout)findViewById(R.id.home_container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                hideSoftKeyboard((Activity)v.getContext());
                return true;
            }
        });

        userNameInput = "";

        SharedPreferences sharedPref = getSharedPreferences("login_prefs", this.MODE_PRIVATE);
        boolean useFP = sharedPref.getBoolean("useFingerprint", false);

        if (Build.VERSION.SDK_INT >= 23 && useFP) {
            fingerPrintUsed = true;
            cs = new CancellationSignal();
            FingerPrintModel fpm = new FingerPrintModel(this, cs);
            fpm.startFingerScan();
        }
    }

    public void signUpRequest(View view){

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag("signUP") == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            ft.add(R.id.home_container, new SignUpFrag(), "signUP");
            ft.commit();
        }
    }

    public void exitSignUp(View view){

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        ft.remove(fm.findFragmentByTag("signUP"));
        ft.commit();
    }

    public void loginAttempt(View view){
        TextView warning = ((TextView)findViewById(R.id.login_fail));
        warning.setText("");
        EditText uNameEdit = ((EditText)findViewById(R.id.user_login_edit));
        EditText passEdit = ((EditText)findViewById(R.id.pass_login_edit));

        userNameInput = uNameEdit.getText().toString();
        passwordInput = passEdit.getText().toString();

        if (userNameInput == null || userNameInput.isEmpty() || passwordInput == null || passwordInput.isEmpty()){
            warning.setText("All fields are necessary for login");
            return;
        }
        uNameEdit.setText("");
        passEdit.setText("");
        Client c = new Client(0);
        c.delegate = this;
        c.execute(userNameInput, passwordInput);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.home_container, new LoadingFrag(), "loader");
        ft.commit();
    }

    public void signUpAttempt(View view){

        TextView warning = ((TextView)findViewById(R.id.sign_up_fail));
        warning.setText("");

        EditText uNameEdit = ((EditText)findViewById(R.id.user_signup_text));
        EditText passEdit = ((EditText)findViewById(R.id.pass_signup_text));
        EditText cellEdit = ((EditText)findViewById(R.id.cellphone_text));

        String userName = uNameEdit.getText().toString();
        String passWord = passEdit.getText().toString();
        String cellPhone = cellEdit.getText().toString();

        if (userName == null || userName.isEmpty() || passWord == null || passWord.isEmpty()
               || cellPhone == null || cellPhone.length() != 10){
            warning.setText("All fields are not complete!");
            return;
        }

        Client c = new Client(1);
        c.delegate = this;
        c.execute(userName, passWord, cellPhone);

        this.userNameInput = userName;
        this.passwordInput = passWord;

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.home_container, new LoadingFrag(), "loader");
        ft.commit();
    }

    @Override
    public void processLogin(Integer output) {

        if (Build.VERSION.SDK_INT >= 23 && fingerPrintUsed && !touchLoginUsed){
            cs.cancel();
        }

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.findFragmentByTag("loader"));
        ft.commit();

        if (output == 10){
            if (!touchLoginUsed) {
                SharedPreferences sharedPref = getSharedPreferences("login_prefs", this.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userName", userNameInput);
                editor.putString("password", passwordInput);
                editor.putBoolean("useFingerprint", false);
                editor.commit();
            }

            startWelcomeActivity();
        }
        else{
            TextView warning = ((TextView)findViewById(R.id.login_fail));
            warning.setText("Invalid Username/Password");
        }
    }

    @Override
    public void processSignUp(Integer output) {

        if (Build.VERSION.SDK_INT >= 23 && fingerPrintUsed && !touchLoginUsed){
            cs.cancel();
        }

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.findFragmentByTag("loader"));
        ft.commit();

        if (output == 10){
            SharedPreferences sharedPref = getSharedPreferences("login_prefs",this.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userName", userNameInput);
            editor.putString("password", passwordInput);
            editor.putBoolean("useFingerprint", false);
            editor.commit();
            startWelcomeActivity();
        }

        else{
            TextView warning = ((TextView)findViewById(R.id.sign_up_fail));
            warning.setText("Username is already in use");
        }
    }

    public void startWelcomeActivity(){
        String uName;
        Intent intent = new Intent(this, WelcomeScreen.class);
        if (fingerPrintUsed){
            SharedPreferences sharedPref = getSharedPreferences("login_prefs", this.MODE_PRIVATE);
            uName = sharedPref.getString("userName", null);
        }
        else{
            uName = userNameInput;
        }
        intent.putExtra("userName", uName);
        finish();
        startActivity(intent);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void touchLogin(){
        touchLoginUsed = true;

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.home_container, new LoadingFrag(), "loader");
        ft.commit();

        SharedPreferences sp = getSharedPreferences("login_prefs", this.MODE_PRIVATE);
        String uName = sp.getString("userName", null);
        String password = sp.getString("password", null);
        Client c = new Client(0);
        c.delegate = this;
        c.execute(uName, password);
    }

}
