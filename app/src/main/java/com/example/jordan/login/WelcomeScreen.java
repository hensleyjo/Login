package com.example.jordan.login;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
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
}
