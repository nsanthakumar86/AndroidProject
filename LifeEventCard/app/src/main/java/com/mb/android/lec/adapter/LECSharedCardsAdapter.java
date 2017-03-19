package com.mb.android.lec.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.android.lec.R;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECSharedCard;
import com.mb.android.lec.db.LECStoredCard;

import java.util.List;

/**
 * Created by LetX on 24-01-2017.
 */

public class LECSharedCardsAdapter extends RecyclerView.Adapter<LECSharedCardsAdapter.PersonViewHolder> {

    private LECSharedCardsListener cardsListener;
    private Context context;

    private LECSharedCardsAdapter(){
    }
    public LECSharedCardsAdapter(Context context){
        this.context = context;
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView cardTypeName;
        TextView cardTypeDetails;
        ImageView cardTypeImage;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            cardTypeName = (TextView)itemView.findViewById(R.id.card_type_name);
            cardTypeDetails = (TextView)itemView.findViewById(R.id.card_type_details);
            cardTypeImage = (ImageView)itemView.findViewById(R.id.card_type_photo);
        }
    }

    List<LECSharedCard> cards;

    public LECSharedCardsAdapter(Context context, List<LECSharedCard> cards, LECSharedCardsListener cardsListener){
        this(context);
        this.cards = cards;
        this.cardsListener = cardsListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
        final Cards card = cards.get(i).makeAsCard();
        final LECSharedCard lecStoredCard = cards.get(i);
        personViewHolder.cardTypeName.setText(card.cardName);
        personViewHolder.cardTypeDetails.setText(card.notes);
        personViewHolder.cardTypeImage.setImageDrawable(card.getDrawableFromFile(context, card.lecCardImg1));

        personViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardsListener != null)
                    cardsListener.onCardClicked(lecStoredCard);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public interface LECSharedCardsListener {
        public void onCardClicked(LECSharedCard lecStoredCard);
    }

}
