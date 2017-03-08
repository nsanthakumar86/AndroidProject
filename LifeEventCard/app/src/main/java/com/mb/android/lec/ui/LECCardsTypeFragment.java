package com.mb.android.lec.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.adapter.LECCardsTypeAdapter;
import com.mb.android.lec.adapter.LECCardsTypeAdapter.LECCardsListener;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.UserSession;
import com.mb.android.lec.util.LECStorage;

import java.util.ArrayList;
import java.util.List;

public class LECCardsTypeFragment extends Fragment {

    private List<Cards> cardsList;
    private RecyclerView rv;
    private LECCardsTypeAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


       View view  = LayoutInflater.from(getActivity()).inflate(R.layout.activity_lec_cards_type, null);

        rv=(RecyclerView)view.findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
//        initializeAdapter();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class LoadSavedEvent extends AsyncTask<Void, Void, Void> {


        private ProgressDialog progressDialog;
        private Context context;

        public LoadSavedEvent(Context context){
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

            cardsList = LECStorage.getALlStoredEvents();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if(cardsList != null && cardsList.size() >0) {
                initializeAdapter();
            }else {
                Toast.makeText(context,"No Saved Event", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void initializeData(){
        LoadSavedEvent loadSavedEvent = new LoadSavedEvent(getActivity());
        loadSavedEvent.execute();

    }

    public void refreshEventList(){
        cardsList = LECStorage.getALlStoredEvents();
        initializeAdapter();
        if(adapter != null)
            adapter.notifyChange();
    }

    private void initializeAdapter(){
        adapter = new LECCardsTypeAdapter(cardsList, new LECCardsListener(){
            @Override
            public void onCardClicked(Cards selectedCard) {
                Intent intent = new Intent(getActivity(), LECCreateCardActivity.class);
                intent.putExtra(LECCreateCardActivity.CREATE_CARD, selectedCard);
                startActivity(intent);
            }
        });


        rv.setAdapter(adapter);
    }
}
