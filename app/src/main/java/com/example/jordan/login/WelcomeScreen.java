package com.example.jordan.login;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeScreen extends AppCompatActivity {

    private boolean fingerprintEnabled;
    private boolean displayMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        displayMenu = true;
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        TextView welcomeMSG = (TextView)findViewById(R.id.welcome);
        String msg = "Welcome " + userName + "!";
        welcomeMSG.setText(msg);
    }

    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void sendPushNotification(View view){

        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, WelcomeScreen.class);
        PendingIntent p = PendingIntent.getActivity(this, 1, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("Login Title");
        builder.setTicker("this is ticker text");
        builder.setContentText("This is a notification from the Login App");
        builder.setSmallIcon(R.drawable.message);
        builder.setAutoCancel(true);
        builder.setContentIntent(p);
        builder.setOngoing(false);
        Notification notify = builder.build();

        nm.notify(1, notify);
    }

    public void toggleFingerprintFunctionality(View view){

        if (Build.VERSION.SDK_INT < 23){
            Toast.makeText(this, "This feature is not available for this device.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPref = getSharedPreferences("login_prefs",this.MODE_PRIVATE);
        fingerprintEnabled = sharedPref.getBoolean("useFingerprint", false);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (fingerprintEnabled) {
            fingerprintEnabled = false;
            editor.putBoolean("useFingerprint", false);
            editor.commit();
            Toast.makeText(this, "Fingerprint Disabled", Toast.LENGTH_SHORT).show();
        }
        else {
            fingerprintEnabled = true;
            editor.putBoolean("useFingerprint", true);
            editor.commit();
            Toast.makeText(this, "Fingerprint Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void toggleMenu(View view){

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_menu, R.anim.slide_out_menu);

        if (displayMenu){
            ft.add(R.id.welcome_container, new MenuFragment(), "menu");
            ft.commit();
            displayMenu = false;
            return;
        }

        ft.remove(fm.findFragmentByTag("menu"));
        ft.commit();
        displayMenu = true;
    }
}
