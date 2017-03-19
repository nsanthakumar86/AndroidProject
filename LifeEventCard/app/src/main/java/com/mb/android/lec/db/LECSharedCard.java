package com.mb.android.lec.db;

import com.mb.android.lec.dao.Cards;
import com.orm.SugarRecord;

/**
 * Created by LetX on 26-02-2017.
 */

public class LECSharedCard extends SugarRecord {
    Long id;
    public String lecCardJson;
    public String userId;

    public LECSharedCard(){
        super();
    }

    public LECSharedCard(String lecCardJson, String userId){
        super();
        this.lecCardJson = lecCardJson;
        this.userId = userId;
    }

    public Long getId(){
        return id;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getLecCardJson(){
        return this.lecCardJson;
    }

    public Cards makeAsCard(){
        return new Cards(this.lecCardJson);
    }
}
