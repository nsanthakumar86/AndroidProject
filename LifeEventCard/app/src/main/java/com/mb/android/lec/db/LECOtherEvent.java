package com.mb.android.lec.db;

import android.util.Log;

import com.orm.SugarRecord;

/**
 * Created by LetX on 28-02-2017.
 */

public class LECOtherEvent extends SugarRecord{
    public Long id;
    public String eventName;
    public String eventDetails;
    public String userId;

    public  LECOtherEvent(){
        super();
    }



}
