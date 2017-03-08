package com.mb.android.lec.db;

/**
 * Created by LetX on 28-02-2017.
 */

public class UserSession {

    private static UserSession userSession;
    private LECUser activeUser;

    private UserSession(){}

    public static synchronized UserSession getInstance(){
        if(userSession == null)
            userSession = new UserSession();

        return userSession;

    }

    public LECUser getActiveUser(){
        return activeUser;
    }

    public void setActiveUser(LECUser lecUser){
        this.activeUser = lecUser;
    }


}
