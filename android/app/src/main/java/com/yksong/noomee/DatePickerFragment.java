package com.yksong.noomee;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by Ed on 16/01/2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    NewEventActivity callingActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        callingActivity = (NewEventActivity) getActivity();

        // Use the current date as the default date in the picker
        int year = callingActivity.getEventDateYear();
        int month = callingActivity.getEventDateMonth();
        int day = callingActivity.getEventDateDay();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Button dateButton = (Button) callingActivity.findViewById(R.id.buttonPickDate);
        callingActivity.onUserSetTimeInfo(dateButton, year+" / "+pad(month+1)+" / "+pad(day));
        callingActivity.setEventDate(year,month,day);
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}