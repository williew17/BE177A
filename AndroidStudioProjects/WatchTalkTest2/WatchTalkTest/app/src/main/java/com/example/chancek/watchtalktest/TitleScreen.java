package com.example.chancek.watchtalktest;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Calendar;

public class TitleScreen extends WearableActivity {

    private TextView mTextView;
    //String surveyOID = "042ED857-B664-4A22-B5FA-6CF3CF15763F"; //Social Impact CAT
    String surveyOID = "80C5D4A3-FC1F-4C1B-B07E-10B796CF8105"; //PROMIS Physical Function

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);

        mTextView = (TextView) findViewById(R.id.textView);

        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(300);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTextView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // Enables Always-on
        setAmbientEnabled();


        /*
        // Create alarm manager
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        // Create pending intent & register it to the alarm notifier class
        Intent intent = new Intent(getBaseContext(), AlarmTrigger.class);
        alarmIntent = PendingIntent.getBroadcast(TitleScreen.this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the current time and add the seconds to it
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set the alarm set off at 10:00 AM
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);

        // Repeat the alarm every minute
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000*60*1, alarmIntent);
        */


        new CountDownTimer(2000,500){
            int tickCount = 0;
            public void onTick(long millisUntilFinished) {
                tickCount++;
                if (tickCount == 4)
                    mTextView.startAnimation(fadeOut);
            }

            public void onFinish() {
                accessMenu();
            }

        }.start();
    }

    public void accessMenu()
    {
        Intent intent;
        intent = new Intent(this, MainMenu.class);
        intent.putExtra("surveyOID", surveyOID);
        startActivity(intent);
    }
}
