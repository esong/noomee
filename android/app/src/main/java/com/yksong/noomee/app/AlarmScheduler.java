package com.yksong.noomee.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by esong on 2015-03-18.
 */
public class AlarmScheduler {
    public static final String action = "com.yksong.noomee.RECOMMEND_ACTION";

    public static void scheduleRecommendPush(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("Time", "Lunch");
        intent.setAction(action);

        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 11);
            cal.set(Calendar.MINUTE, 30 + new Random().nextInt(10));

            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(action);
            intent.putExtra("Time", "Dinner");
            pendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            cal.set(Calendar.HOUR_OF_DAY, 17);
            cal.set(Calendar.MINUTE, 30 + new Random().nextInt(10));

            alarmManager.cancel(pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        }
    }
}
