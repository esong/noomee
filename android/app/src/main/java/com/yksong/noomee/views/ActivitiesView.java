package com.yksong.noomee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by esong on 2015-01-01.
 */
public class ActivitiesView extends ListView {
    public ActivitiesView(Context context) {
        this(context, null);
    }

    public ActivitiesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivitiesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
