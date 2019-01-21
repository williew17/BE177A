package com.example.chancek.watchtalktest;

import android.Manifest;
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

public class TitleScreen extends WearableActivity {
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    private TextView mTextView;



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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(TitleScreen.this, new String[]{Manifest.permission
                    .RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }




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
        startActivity(intent);
    }
}
