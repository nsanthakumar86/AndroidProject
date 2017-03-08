package com.mb.android.lec.db;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by LetX on 07-01-2017.
 */

public class LECUser extends SugarRecord {
    Long id;
    String userFirstName;
    String userLastName;
    String userMailId;
    String password;
    String phoneNumber;
    String address;
    String profileImg;

    public  LECUser(){
       super();
    }

    public LECUser(String fName, String lName, String mailId, String password, String phNumber, String address){
        this.userFirstName = fName;
        this.userLastName = lName;
        this.userMailId = mailId;
        this.password = password;
        this.phoneNumber = phNumber;
        this.address = address;
    }

    public Long getId() {
        return id;
    }


    public void setProfileImg(String profileImg){
        this.profileImg = profileImg;
    }

    public String getProfileImg(){
        return this.profileImg;
    }
    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserMailId() {
        return userMailId;
    }

    public void setUserMailId(String userMailId) {
        this.userMailId = userMailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString(){
        return "UserName: "+userFirstName+" "+userLastName+"\n"+" Email: "+userMailId+"\n"+"Phone: "+phoneNumber+"\n"+"Password: "+password;
    }

    public String getFullName() {
        return userFirstName+" "+userLastName;
    }
}
