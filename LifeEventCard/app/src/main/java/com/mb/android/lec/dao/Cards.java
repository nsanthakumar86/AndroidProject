package com.mb.android.lec.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LetX on 24-01-2017.
 */

public class Cards implements Parcelable {
    public enum CardType {BIRTH_DAY, WEEDING_DAY, GET_TOGETHER, OTHER};
    public  String cardName="";
    public String cardDetails="";
    public CardType cardType;
    public int cardTypeImageId;
    public String lecCardImg1="";
    public String lecCardImg2="";
    public String lecCardImg3="";
    public String lecCardImg4="";

    public String lecAudio="";

    public String notes="";

    public Cards(String cardName, String cardDetails, CardType cardType){
        this.cardName = cardName;
        this.cardDetails = cardDetails;
        this.cardType = cardType;
    }

    public Cards(String cardsAsJson){
        try {
            setCardJson(new JSONObject(cardsAsJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Cards(String cardName, String cardDetails, CardType cardType, int cardTypeImageId){
        this(cardName, cardDetails, cardType);
        this.cardTypeImageId = cardTypeImageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardName);
        dest.writeString(this.cardDetails);
        dest.writeInt(this.cardType == null ? -1 : this.cardType.ordinal());
        dest.writeInt(this.cardTypeImageId);
        dest.writeString(lecCardImg1);
        dest.writeString(lecCardImg2);
        dest.writeString(lecCardImg3);
        dest.writeString(lecCardImg4);
        dest.writeString(lecAudio);
        dest.writeString(notes);
    }

    protected Cards(Parcel in) {
        this.cardName = in.readString();
        this.cardDetails = in.readString();
        int tmpCardType = in.readInt();
        this.cardType = tmpCardType == -1 ? null : CardType.values()[tmpCardType];
        this.cardTypeImageId = in.readInt();
        this.lecCardImg1 = in.readString();
        this.lecCardImg2 = in.readString();
        this.lecCardImg3 = in.readString();
        this.lecCardImg4 = in.readString();
        this.lecAudio = in.readString();
        this.notes = in.readString();

    }

    public static final Parcelable.Creator<Cards> CREATOR = new Parcelable.Creator<Cards>() {
        @Override
        public Cards createFromParcel(Parcel source) {
            return new Cards(source);
        }

        @Override
        public Cards[] newArray(int size) {
            return new Cards[size];
        }
    };

    public String getCardsJson(){
        JSONObject cardsJson = new JSONObject();
        try {
            cardsJson.put("cardName",this.cardName);
            cardsJson.put("cardDetails",this.cardDetails);
            cardsJson.put("lecCardImg1",this.lecCardImg1);
            cardsJson.put("lecCardImg2",this.lecCardImg2);
            cardsJson.put("lecCardImg3",this.lecCardImg3);
            cardsJson.put("lecCardImg4",this.lecCardImg4);
            cardsJson.put("lecAudio",this.lecAudio);
            cardsJson.put("notes",this.notes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardsJson.toString();
    }

    public void setCardJson(JSONObject cardJson){
        try {
            this.cardName = cardJson.getString("cardName");
            this.cardDetails = cardJson.getString("cardDetails");
            this.lecCardImg1 = cardJson.getString("lecCardImg1");
            this.lecCardImg2 = cardJson.getString("lecCardImg2");
            this.lecCardImg3 = cardJson.getString("lecCardImg3");
            this.lecCardImg4 = cardJson.getString("lecCardImg4");
            this.lecAudio = cardJson.getString("lecAudio");
            this.notes = cardJson.getString("notes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Drawable getDrawableFromFile(Context context, String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        BitmapDrawable photo = new BitmapDrawable(context.getResources(), bitmap);
        return photo;
    }

}
