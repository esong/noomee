package com.yksong.noomee.start;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import com.yksong.noomee.MainActivity;
import com.yksong.noomee.R;

import java.util.Arrays;

/**
 * Created by esong on 2014-12-08.
 */
public class StartActivity extends FragmentActivity {
    private static final String FB_ID = "fbId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String TAG = "StartActivity";
    private static final String[] FB_PERMISSION = new String [] {"user_friends"};

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            startMainActivity();
        }
    }

    public void onLoginClick(View button){
        progressDialog = ProgressDialog.show(this, "", "Logging in...", true);

        ParseFacebookUtils.logIn(Arrays.asList(FB_PERMISSION), this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else {
                    getFacebookUserInfoInBackground();
                    Log.d(TAG, "User logged in through Facebook!");
                    startMainActivity();
                }
            }
        });
    }

    private void getFacebookUserInfoInBackground() {
        Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    ParseUser parseUser = ParseUser.getCurrentUser();
                    boolean updated = false;
                    if (!user.getId().equals(parseUser.get(FB_ID))) {
                        parseUser.put(FB_ID, user.getId());
                        updated = true;
                    }
                    if (!user.getFirstName().equals(parseUser.get(FIRST_NAME))) {
                        parseUser.put(FIRST_NAME, user.getFirstName());
                        updated = true;
                    }
                    if (!user.getLastName().equals(parseUser.get(LAST_NAME))) {
                        parseUser.put(LAST_NAME, user.getLastName());
                        updated = true;
                    }
                    if (updated) {
                        parseUser.saveInBackground();
                    }
                }
            }
        }).executeAsync();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
