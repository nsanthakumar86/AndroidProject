package com.mb.android.lec.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mb.android.lec.R;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.UserSession;

/**
 * Created by LetX on 07-01-2017.
 */

public class LECSharedPreferenceManager {

    public static boolean isUserAlreadyLoggedIn(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        return preferences.getBoolean(context.getString(R.string.isUserLoggedinAlready), false);

    }

    public static void loggedInLECUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.isUserLoggedinAlready), true);
        editor.commit();


    }

    public static void loggedOutLECUser(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.isUserLoggedinAlready), false);
        editor.putString(context.getString(R.string.loggedinuser_mailid), null);
        editor.commit();
    }

    public static void loggedInLECUser(Context context, String mailId){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.loggedinuser_mailid), mailId);
        editor.commit();

        //Load User Session
        UserSession.getInstance().setActiveUser(LECQueryManager.getUserByMailId(mailId));
    }

    public static void saveLECCardSharedLocation(Context context, String lecCardSharedLocation){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.lec_shared_location), lecCardSharedLocation);
        editor.commit();

    }

    public static String getLECCardSharedLocation(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        return preferences.getString(context.getString(R.string.lec_shared_location), null);
    }
    public static String getLoggedinUserMailId(Context context){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.lec_Preference), Context.MODE_PRIVATE);
        return preferences.getString(context.getString(R.string.loggedinuser_mailid), null);
    }
}
