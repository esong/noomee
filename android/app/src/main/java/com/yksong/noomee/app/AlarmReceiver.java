package com.yksong.noomee.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.yksong.noomee.NewEventActivity;
import com.yksong.noomee.R;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.network.HttpConfig;
import com.yksong.noomee.util.GeoProvider;
import com.yksong.noomee.util.NoomeeAPI;
import com.yksong.noomee.util.YelpUtil;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import retrofit.RestAdapter;

/**
 * Created by esong on 2015-03-09.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 0;

    RestAdapter mRestAdapter = new RestAdapter.Builder()
            .setEndpoint(HttpConfig.NOOMEE_PROTOCOL + HttpConfig.NOOMEE_HOST)
            .build();

    NoomeeAPI mNoomeeAPI = mRestAdapter.create(NoomeeAPI.class);
    GeoProvider mGeoProvider = GeoProvider.getInstance();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Location location = mGeoProvider.getLocation();

        if (intent.getStringExtra("Time") == null) {
            return;
        }

        if (location != null) {
            Task.callInBackground(new Callable<Restaurant>() {
                @Override
                public Restaurant call() throws Exception {
                    return mNoomeeAPI.randomRestaurant(
                            location.getLatitude(), location.getLongitude());
                }
            }).continueWith(new Continuation<Restaurant, Object>() {
                @Override
                public Object then(Task<Restaurant> task) throws Exception {
                    Restaurant restaurant = task.getResult();
                    String[] promoteTexts = context.getResources()
                            .getStringArray(R.array.push_promote);
                    String contentText = String.format(
                            promoteTexts[new Random().nextInt(promoteTexts.length)],
                            restaurant.name);

                    NotificationCompat.BigPictureStyle bigPictureStyle =
                            new NotificationCompat.BigPictureStyle();

                    restaurant.image_url = YelpUtil.switchTo348ImageUrl(restaurant.image_url);

                    try {
                        bigPictureStyle.bigPicture(
                                Picasso.with(context).load(restaurant.image_url).get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    bigPictureStyle.setSummaryText(contentText);

                    Intent newEventIntent = new Intent(context, NewEventActivity.class);

                    newEventIntent.putExtra("restaurantName", restaurant.name);
                    newEventIntent.putExtra("restaurantId", restaurant.id);

                    PendingIntent yelpUrlIntent = PendingIntent.getActivity(context, 0,
                            new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.mobile_url)),
                            PendingIntent.FLAG_ONE_SHOT );
                    PendingIntent postIntent = PendingIntent.getActivity(context, 0,
                            newEventIntent, PendingIntent.FLAG_ONE_SHOT);

                    String message = "It's " + intent.getStringExtra("Time") + " time !";

                    final NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_push)
                                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                                    .setContentTitle(message)
                                    .setContentText(contentText)
                                    .setStyle(bigPictureStyle)
                                    .addAction(R.drawable.ic_fork, "Details", yelpUrlIntent)
                                    .addAction(R.drawable.ic_add_white_24dp, "Post", postIntent);

                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    return null;
                }
            });
        }
    }
}
