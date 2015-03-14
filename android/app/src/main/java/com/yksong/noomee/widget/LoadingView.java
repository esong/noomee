package com.yksong.noomee.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yksong.noomee.R;

/**
 * Created by esong on 2015-03-13.
 */
public class LoadingView extends LinearLayout {
    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.progress_merge, this);
    }

    @Override
    public void onFinishInflate() {
        hideAll();
        findViewById(android.R.id.progress).setVisibility(GONE);
    }

    private void hideAll(){
        for (int i = 0; i < getChildCount(); ++i) {
            View view = getChildAt(i);

            if (view.getId() != android.R.id.progress) {
                view.setVisibility(GONE);
            }
        }
    }

    private void showAll() {
        for (int i = 0; i < getChildCount(); ++i) {
            View view = getChildAt(i);

            if (view.getId() != android.R.id.progress) {
                view.setVisibility(VISIBLE);
            }
        }
    }

    public void load() {
        hideAll();
        findViewById(android.R.id.progress).setVisibility(VISIBLE);
    }

    public void finish() {
        findViewById(android.R.id.progress).setVisibility(GONE);
        showAll();
    }
}
