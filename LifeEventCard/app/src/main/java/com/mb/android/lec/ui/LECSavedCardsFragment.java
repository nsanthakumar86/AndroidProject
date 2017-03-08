package com.mb.android.lec.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.adapter.LECCardsTypeAdapter;
import com.mb.android.lec.adapter.LECCardsTypeAdapter.LECCardsListener;
import com.mb.android.lec.adapter.LECSavedCardsAdapter;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.LECStoredCard;
import com.mb.android.lec.db.UserSession;

import java.util.ArrayList;
import java.util.List;

public class LECSavedCardsFragment extends Fragment {

    public static final String TAG = LECSavedCardsFragment.class.getName();
    private List<LECStoredCard> lecStoredCardList;
    private RecyclerView rv;
    private TextView noItemsView;

    public static LECSavedCardsFragment newInstance() {
        LECSavedCardsFragment fragment = new LECSavedCardsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


       View view  = LayoutInflater.from(getActivity()).inflate(R.layout.activity_lec_cards_type, null);

        rv=(RecyclerView)view.findViewById(R.id.rv);
        noItemsView = (TextView) view.findViewById(R.id.no_items);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        initializeData();
        super.onResume();
    }

    class LoadSavedCards extends AsyncTask<Void, Void, Void> {


        private ProgressDialog progressDialog;
        private Context context;

        public LoadSavedCards(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            lecStoredCardList = LECQueryManager.getAllCardsByUser(UserSession.getInstance().getActiveUser().getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if(lecStoredCardList != null && lecStoredCardList.size() >0) {
                initializeAdapter();
            }else {
                Toast.makeText(context,"No Saved Cards", Toast.LENGTH_SHORT).show();
                noItemsView.setVisibility(View.VISIBLE);
            }

        }
    }
    private void initializeData(){
        LoadSavedCards loadSavedCards = new LoadSavedCards(getActivity());
        loadSavedCards.execute();
    }

    private void initializeAdapter(){
        LECSavedCardsAdapter adapter = new LECSavedCardsAdapter(getActivity(), lecStoredCardList, new LECSavedCardsAdapter.LECSavedCardsListener(){
            @Override
            public void onCardClicked(LECStoredCard lecStoredCard) {
                Intent intent = new Intent(getActivity(), LECCardViewActivity.class);
                intent.putExtra(LECCardViewActivity.VIEW_CARD, lecStoredCard.makeAsCard());
                intent.putExtra(LECCardViewActivity.VIEW_CARD_ID, lecStoredCard.getId());
                startActivity(intent);
            }
        });


        rv.setAdapter(adapter);
    }
}
