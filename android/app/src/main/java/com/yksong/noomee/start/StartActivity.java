package com.yksong.noomee.start;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.yksong.noomee.start.FacebookFragment;

/**
 * Created by esong on 2014-12-08.
 */
public class StartActivity extends FragmentActivity {
    private FacebookFragment mFbFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mFbFragment = new FacebookFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mFbFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mFbFragment = (FacebookFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }
}
