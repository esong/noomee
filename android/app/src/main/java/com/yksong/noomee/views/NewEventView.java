package com.yksong.noomee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

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
