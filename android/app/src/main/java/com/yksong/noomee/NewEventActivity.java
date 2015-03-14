package com.yksong.noomee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.facebook.Session;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.network.NoomeeClient;
import com.yksong.noomee.start.StartActivity;
import com.yksong.noomee.util.APICallback;
import com.yksong.noomee.util.GeoProvider;
import com.yksong.noomee.util.NoomeeAPI;
import com.yksong.noomee.util.ParseAPI;

import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ed on 16/01/2015.
 */
public class NewEventActivity extends ActionBarActivity {
    private int eventTimeHour;
    private int eventTimeMinute;
    private int eventDateYear;
    private int eventDateMonth;
    private int eventDateDay;
    private Restaurant mSelectedRestaurant;

    private AutoCompleteTextView restaurantAutoComplete;

    private NoomeeAPI mNoomeeAPI = NoomeeClient.getApi();
    private GeoProvider mGeoProvider = GeoProvider.getInstance();

    public static final String INTENT_RESTAURANT_NAME = "restaurantName";
    public static final String INTENT_RESTAURANT_ID = "restaurantId";

    /**
     * Used to store the last screen title. For use in
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get current time and date
        Time now = new Time();
        now.setToNow();

        //set event initial values
        eventTimeHour = now.hour;
        eventTimeMinute = now.minute;
        eventDateYear = now.year;
        eventDateMonth = now.month;
        eventDateDay = now.monthDay;

        //use current time and current date as default time information
        Button timeButton = (Button)findViewById(R.id.buttonPickTime);
        timeButton.setText(pad(eventTimeHour)+" : "+pad(eventTimeMinute));

        Button dateButton = (Button)findViewById(R.id.buttonPickDate);
        dateButton.setText(eventDateYear+" / "+pad(eventDateMonth+1)+" / "+pad(eventDateDay));

        //use empty string as default location
        TextView LocationText = (TextView) findViewById(R.id.Location);
        LocationText.setText("Location:");

        Location location = mGeoProvider.getLocation();

        restaurantAutoComplete = (AutoCompleteTextView) findViewById(R.id.eventLocation);
        restaurantAutoComplete.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new String[0]));

        if (location != null) {
            mNoomeeAPI.businesses(location.getLatitude(), location.getLongitude(),
                    new Callback<Restaurant[]>() {
                @Override
                public void success(final Restaurant[] restaurants, Response response) {
                    ArrayAdapter<Restaurant> adapter = new ArrayAdapter<> (NewEventActivity.this,
                            android.R.layout.simple_list_item_1, restaurants);
                    restaurantAutoComplete.setAdapter(adapter);
                    restaurantAutoComplete.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            mSelectedRestaurant = (Restaurant) parent.getItemAtPosition(position);
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }

        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra(INTENT_RESTAURANT_ID);
        String restaurantName = intent.getStringExtra(INTENT_RESTAURANT_NAME);

        if (restaurantId != null) {
            restaurantAutoComplete.setText(restaurantName);
            mSelectedRestaurant = new Restaurant();
            mSelectedRestaurant.name = restaurantName;
            mSelectedRestaurant.id = restaurantId;
        }

        addListeners();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cancel Posting")
                .setMessage("Are you sure you want to close this post?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(NewEventActivity.this);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void addListeners(){
        restaurantAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        restaurantAutoComplete.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from restaurantAutoComplete
                    restaurantAutoComplete.setCursorVisible(false);
                }
                return false;
            }
        });
        restaurantAutoComplete.setOnKeyListener(new AutoCompleteTextView.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // if "Enter" is pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    LinearLayout myLayout = (LinearLayout) findViewById(R.id.topLevelLayout);
                    myLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.buttonPickDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        findViewById(R.id.buttonPickTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newevent, menu);
        return true;
    }

    public void setEventTime(int hour, int minute){
        eventTimeHour = hour;
        eventTimeMinute = minute;
    }

    public void setEventDate(int year, int month, int day){
        eventDateYear = year;
        eventDateMonth = month;
        eventDateDay = day;
    }

    public int getEventTimeHour(){
        return eventTimeHour;
    }

    public int getEventTimeMinute(){
        return eventTimeMinute;
    }

    public int getEventDateYear(){
        return eventDateYear;
    }

    public int getEventDateMonth(){
        return eventDateMonth;
    }

    public int getEventDateDay(){
        return eventDateDay;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_post: {
                Restaurant postingRestaurant;

                if (mSelectedRestaurant == null ||
                     !restaurantAutoComplete.getText().toString().equals(mSelectedRestaurant.name)){
                    postingRestaurant = new Restaurant();
                    postingRestaurant.name = restaurantAutoComplete.getText().toString();
                } else {
                    postingRestaurant = mSelectedRestaurant;
                }

                ParseAPI.createEvent(
                        ParseUser.getCurrentUser(),
                        eventDateYear,
                        eventDateMonth,
                        eventDateDay,
                        eventTimeHour,
                        eventTimeMinute,
                        postingRestaurant
                );

                NewEventActivity.this.finish();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        restaurantAutoComplete.clearFocus();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        restaurantAutoComplete.clearFocus();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onUserSetTimeInfo(TextView view, String time) {
        TextView timeText = (TextView) findViewById(R.id.Time);
        view.setText(time);
    }
}
