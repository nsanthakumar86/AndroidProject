package com.mb.android.lec.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.android.lec.R;
import com.mb.android.lec.dao.Cards;

import java.util.List;

/**
 * Created by LetX on 24-01-2017.
 */

public class LECCardsTypeAdapter extends RecyclerView.Adapter<LECCardsTypeAdapter.PersonViewHolder> {

    private LECCardsListener cardsListener;

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

    List<Cards> cards;

    public LECCardsTypeAdapter(List<Cards> cards, LECCardsListener cardsListener){
        this.cards = cards;
        this.cardsListener = cardsListener;
    }

    public void notifyChange(){
        notifyDataSetChanged();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        final Cards card = cards.get(i);
        personViewHolder.cardTypeName.setText(card.cardName);
        personViewHolder.cardTypeDetails.setText(card.cardDetails);
        personViewHolder.cardTypeImage.setImageResource(card.cardTypeImageId);

        personViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardsListener != null)
                    cardsListener.onCardClicked(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public interface LECCardsListener {
        public void onCardClicked(Cards selectedCard);
    }

}
