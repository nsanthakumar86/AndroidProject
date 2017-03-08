package com.mb.android.lec.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mb.android.lec.R;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.LECUser;
import com.mb.android.lec.db.UserSession;
import com.mb.android.lec.util.LECSharedPreferenceManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LECStartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();

        Intent loginScreen = new Intent(LECStartActivity.this, LECLoginActivity.class);
        Intent dashboardScreen = new Intent(LECStartActivity.this, LECDashboard.class);
        boolean isUserAlreadyLoggedIn = LECSharedPreferenceManager.isUserAlreadyLoggedIn(LECStartActivity.this);
        if(isUserAlreadyLoggedIn){
            final String emailId = LECSharedPreferenceManager.getLoggedinUserMailId(LECStartActivity.this);
            LECUser lecUser = LECQueryManager.getUserByMailId(emailId);
            if(lecUser != null){
                UserSession.getInstance().setActiveUser(lecUser);
            }else {
                isUserAlreadyLoggedIn = false;
            }

        }
        final Intent nextScreen = isUserAlreadyLoggedIn ? dashboardScreen : loginScreen;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(nextScreen);
                finish();
            }
        }, 5000);

    }

}
