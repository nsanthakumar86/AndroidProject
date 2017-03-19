package com.mb.android.lec.util;

import android.os.Environment;

import com.mb.android.lec.R;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECOtherEvent;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.UserSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by LetX on 26-02-2017.
 */

public class LECStorage {
    public  static  final  String CARD_CREATED_DIR  = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/leccards/created";

    public  static  final  String CARD_SHARED_DIR  = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/leccards/shared";

    public static final String CARD_AUDIO_DIR = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/lecaudio";

    public static final File  folder = new File(Environment.getExternalStorageDirectory() +
    File.separator + "LECCardTemp");

    public static final String lecCardInfoFile = "leccardinfo.txt";

    public static File getRandamLECFileNameToShare(){
        return new File(folder.getAbsolutePath()+File.separator + "lec"+ new Random().nextInt());
    }


    public static ArrayList getALlStoredEvents(){
        ArrayList cardsList = new ArrayList<>();
        cardsList.add(new Cards("BirthDay Celebration", "Memories of Birthday", Cards.CardType.BIRTH_DAY, R.drawable.birthday_icon));
        cardsList.add(new Cards("Wedding Day Celebration", "Memories of Wedding Day", Cards.CardType.WEEDING_DAY, R.drawable.wedding_day_icon));
        cardsList.add(new Cards("Get Together", "Memories of Get together Party", Cards.CardType.GET_TOGETHER,  R.drawable.get_together_icon));
        cardsList.add(new Cards("Valentines Day", "Memories of Happy Valentines Day", Cards.CardType.GET_TOGETHER,  R.drawable.lovers_day_icon));
        cardsList.add(new Cards("Christmas", "Memories of Christmas Celebration", Cards.CardType.GET_TOGETHER,  R.drawable.chritsmas));
        cardsList.add(new Cards("New Year", "Memories of Happy New Year Celebration", Cards.CardType.GET_TOGETHER,  R.drawable.new_year_icon));
        cardsList.add(new Cards("Pongal", "Memories of Pongal Celebration", Cards.CardType.GET_TOGETHER,  R.drawable.pongal_icon));

        List<LECOtherEvent> lecOtherEventList = LECQueryManager.getLECOtherEventsByUserId(UserSession.getInstance().getActiveUser().getId());
        if(lecOtherEventList != null && lecOtherEventList.size()>0){
            for (LECOtherEvent lecOtherEvent:lecOtherEventList)
                cardsList.add(new Cards(lecOtherEvent.eventName, lecOtherEvent.eventDetails, Cards.CardType.BIRTH_DAY, R.drawable.events));
        }

        return cardsList;
    }
}
