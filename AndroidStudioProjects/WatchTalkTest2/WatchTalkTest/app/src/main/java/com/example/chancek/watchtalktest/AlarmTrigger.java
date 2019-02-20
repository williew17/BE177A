package com.example.chancek.watchtalktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmTrigger extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();
        // Start the MainActivity
        Intent i = new Intent(context, TitleScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
