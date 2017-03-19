package com.mb.android.lec.db;

import android.util.Log;

import java.util.List;

/**
 * Created by LetX on 07-01-2017.
 */

public class LECQueryManager {

    public static boolean isUserExists(String userMailId){
        List<LECUser> books = LECUser.find(LECUser.class, "user_Mail_Id=?", userMailId);
        return (books != null && books.size()>0) ? true : false;

    }

    public static LECUser getUser(Long id){
        LECUser lecUser = LECUser.findById(LECUser.class, id);
        return lecUser;
    }

    public static LECUser getUserByMailId(String  userMailId){
        List<LECUser> lecUsers = LECUser.find(LECUser.class, "user_Mail_Id=?", userMailId);
        return (lecUsers != null && lecUsers.size()>0) ? lecUsers.get(0) : null;
    }

    public static LECUser getUserByMailIdAndPassword(String  userMailId, String password){
        List<LECUser> lecUsers = LECUser.find(LECUser.class, "user_Mail_Id=? and password=?", userMailId, password);
        return (lecUsers != null && lecUsers.size()>0) ? lecUsers.get(0) : null;
    }

    public static  void saveUser(String fName, String lName, String mailId, String password, String phNumber, String address){
        LECUser lecUser = new LECUser(fName, lName, mailId, password, phNumber, address);
        lecUser.save();
    }

    public static void updateProfileImage(Long id, String imagePath){
        LECUser lecUser = LECUser.findById(LECUser.class, id);
        lecUser.profileImg = imagePath;
        lecUser.save();
    }

    public static Long saveLECCard(String lecCardAsString){
        LECStoredCard lecStoredCard = new LECStoredCard(lecCardAsString, UserSession.getInstance().getActiveUser().getId()+"");

        return lecStoredCard.save();
    }

    public static List<LECStoredCard> getAllCardsByUser(Long userId){
        List<LECStoredCard> lecStoredCards = LECStoredCard.find(LECStoredCard.class, "user_Id=?", userId.toString());
        return  lecStoredCards;
    }

    public static LECStoredCard getLECCard(Long id){
        LECStoredCard lecStoredCard = LECStoredCard.findById(LECStoredCard.class, id);
        return lecStoredCard;
    }

    public static Long saveSharedLECCard(String lecSharedCardAsString){
        LECSharedCard lecSharedCard = new LECSharedCard(lecSharedCardAsString, UserSession.getInstance().getActiveUser().getId()+"");

        return lecSharedCard.save();
    }

    public static List<LECSharedCard> getAllSharedCardsByUser(Long userId){
        List<LECSharedCard> lecSharedCards = LECSharedCard.find(LECSharedCard.class, "user_Id=?", userId.toString());
        return  lecSharedCards;
    }

    public static LECSharedCard getSharedLECCard(Long id){
        LECSharedCard lecSharedCard = LECSharedCard.findById(LECSharedCard.class, id);
        return lecSharedCard;
    }

    public static void deleteLECCard(Long id){
        try {
            LECStoredCard lecStoredCard = LECStoredCard.findById(LECStoredCard.class, id);
            lecStoredCard.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveLECOtherEvents(String eventName, String eventDetails, Long userId){
        LECOtherEvent lecOtherEvent = new LECOtherEvent();
        lecOtherEvent.eventName = eventName;
        lecOtherEvent.eventDetails = eventDetails;
        lecOtherEvent.userId = userId.toString();
        lecOtherEvent.save();
    }

    public static  List<LECOtherEvent> getLECOtherEventsByUserId(Long userId){
        List<LECOtherEvent> lecOtherEventList = LECOtherEvent.find(LECOtherEvent.class, "user_Id=?", userId.toString());
        return lecOtherEventList;
    }
}
