package com.yksong.noomee.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yksong.noomee.DatePickerFragment;
import com.yksong.noomee.NewEventActivity;
import com.yksong.noomee.R;

/**
 * Created by Ed on 16/01/2015.
 */
public class NewEventView extends FrameLayout{
    public NewEventView(Context context) {
        this(context, null);
    }

    public NewEventView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
