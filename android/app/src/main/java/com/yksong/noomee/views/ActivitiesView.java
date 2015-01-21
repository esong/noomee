package com.yksong.noomee.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yksong.noomee.MainActivity;
import com.yksong.noomee.NewEventActivity;
import com.yksong.noomee.R;

/**
 * Created by esong on 2015-01-01.
 */
public class ActivitiesView extends FrameLayout {
    public ActivitiesView(Context context) {
        this(context, null);
    }

    public ActivitiesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivitiesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ( (Activity) getContext() ).startActivity(
                                new Intent(getContext(), NewEventActivity.class));
            }
        });
    }
}
