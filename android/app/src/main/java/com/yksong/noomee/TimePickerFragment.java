package com.yksong.noomee;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

/**
 * Created by Ed on 16/01/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    NewEventActivity callingActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        callingActivity = (NewEventActivity) getActivity();

        // Use the current time as the default values for the picker
        int hour = callingActivity.getEventTimeHour();
        int minute = callingActivity.getEventTimeMinute();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Button timeButton = (Button) callingActivity.findViewById(R.id.buttonPickTime);
        callingActivity.onUserSetTimeInfo(timeButton, pad(hourOfDay)+" : "+pad(minute));
        callingActivity.setEventTime(hourOfDay, minute);
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}