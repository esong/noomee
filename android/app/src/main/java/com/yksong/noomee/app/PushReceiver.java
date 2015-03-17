package com.yksong.noomee.app;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;
import com.yksong.noomee.MainActivity;

/**
 * Created by esong on 2015-03-17.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {
    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
