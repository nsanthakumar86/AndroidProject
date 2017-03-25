package com.mb.android.lec.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.adapter.LECSavedCardsAdapter;
import com.mb.android.lec.adapter.LECSharedCardsAdapter;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.LECSharedCard;
import com.mb.android.lec.db.LECStoredCard;
import com.mb.android.lec.db.UserSession;
import com.mb.android.lec.util.LECSharedPreferenceManager;
import com.mb.android.lec.util.LECStorage;
import com.mb.android.lec.util.LECZipManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LECSharedCardsFragment extends Fragment {

    public static final String TAG = LECSharedCardsFragment.class.getName();
    private List<LECSharedCard> lecStoredCardList;
    private RecyclerView rv;
    private TextView noItemsView;

    public static LECSharedCardsFragment newInstance() {
        LECSharedCardsFragment fragment = new LECSharedCardsFragment();
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
        setHasOptionsMenu(true);
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

    class LoadSharedCards extends AsyncTask<Void, Void, String> {

        private ProgressDialog progressDialog;
        private Context context;
        private File defaultLocation;

        public LoadSharedCards(Context context, File defaultLocation){
            this.context = context;
            this.defaultLocation = defaultLocation;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            if(defaultLocation != null){

               //UnZip All Shared ZIP file
                final FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if(name.contains(".zip") && name.contains("lec"))
                            return true;
                        else
                        return false;
                    }
                };

                File[] files = defaultLocation.listFiles(filter);

                if(files != null && files.length >0){
                    List<File> fileList = Arrays.asList(files);
                    final List<File> sharedCards = new ArrayList<File>();
                    for(File file:fileList) {
                        Log.d(TAG, "LEC-FILE : " +file.getAbsolutePath());
                        try {
                            LECZipManager.unzip(file.getAbsolutePath(), LECStorage.CARD_SHARED_DIR+File.separator+ FilenameUtils.removeExtension(file.getName()));
                            sharedCards.add(new File(LECStorage.CARD_SHARED_DIR+File.separator+ FilenameUtils.removeExtension(file.getName())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //After Unzip Delete the shared zip files from default location
                    for (File file : fileList) {
                        file.delete();
                    }

                    //Create a Cards from shared folder and delete.



                    if(sharedCards != null && sharedCards.size()>0){
                        for(File cardFolder : sharedCards){
                            Log.d(TAG, "card name:"+cardFolder.getName());

                            final FilenameFilter f2 = new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    if(name.contains(LECStorage.lecCardInfoFile))
                                        return true;
                                    else
                                        return false;
                                }
                            };
                            List<File> infoFile = Arrays.asList(cardFolder.listFiles(f2));
                            if(infoFile != null && infoFile.size() > 0){
//                            FilenameUtils.
                                Log.d(TAG, "card file name:"+infoFile.get(0).getAbsolutePath());
                                String data = readFromFile(infoFile.get(0).getAbsolutePath());
                                try {
                                    JSONObject newCard = new JSONObject();
                                    JSONObject cardJson = new JSONObject(data);
                                    newCard.put("cardName", cardJson.getString("cardName")) ;

                                    newCard.put("cardDetails", cardJson.getString("cardDetails"));

                                    newCard.put("lecCardImg1", changePath(cardFolder.getAbsolutePath(),cardJson.getString("lecCardImg1")));
                                    newCard.put("lecCardImg2", changePath(cardFolder.getAbsolutePath(),cardJson.getString("lecCardImg2")));
                                    newCard.put("lecCardImg3", changePath(cardFolder.getAbsolutePath(),cardJson.getString("lecCardImg3")));
                                    newCard.put("lecCardImg4", changePath(cardFolder.getAbsolutePath(),cardJson.getString("lecCardImg4")));
                                    newCard.put("lecAudio", changePath(cardFolder.getAbsolutePath(),cardJson.getString("lecAudio")));

                                    newCard.put("notes", cardJson.getString("notes"));
                                    newCard.put("location", cardJson.getString("location"));

                                    Log.d(TAG, "JSON :"+cardJson.toString());
                                    Log.d(TAG, "CHANGE JSON :"+newCard.toString());

                                    LECQueryManager.saveSharedLECCard(newCard.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        }


                    }
                    return "Done.";
                }else {
                    return "There is no new shared LEC Cards in the Shared Location.";
                }



            }else{
                 return "Please set the Shared Location.";
            }

        }


        @Override
        protected void onPostExecute(String msg) {
            if(progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
            initializeData();
        }

        private String changePath(String basePath, String path){
            if(TextUtils.isEmpty(path)){
                return "";
            }

            String changePath = basePath +File.separator + FilenameUtils.getName(path);
            Log.d(TAG, "change Path:"+changePath);
            return changePath;
        }
        private String readFromFile(String fileName) {

            String ret = "";

            try {
                InputStream inputStream = new FileInputStream(new File(fileName));

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            return ret;
        }


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

            if(lecStoredCardList != null){
                lecStoredCardList.clear();
            }
            lecStoredCardList = LECQueryManager.getAllSharedCardsByUser(UserSession.getInstance().getActiveUser().getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if(lecStoredCardList != null && lecStoredCardList.size() >0) {
                noItemsView.setVisibility(View.GONE);
                initializeAdapter();
            }else {
                Toast.makeText(context,"No Shared Cards", Toast.LENGTH_SHORT).show();
                noItemsView.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shared_card, menu);
    }

    private static final int FILE_SELECT_CODE = 0;





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
       if(id == R.id.sync){
            String location = LECSharedPreferenceManager.getLECCardSharedLocation(getActivity());
            if(!TextUtils.isEmpty(location)) {
                LoadSharedCards loadSharedCards = new LoadSharedCards(getActivity(), LECStorage.folder);
                loadSharedCards.execute();
            }else {
                Toast.makeText(getActivity(), "Please save the LEC Shared Card Location and try again.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeData(){


        LoadSavedCards loadSavedCards = new LoadSavedCards(getActivity());
        loadSavedCards.execute();
    }

    private void initializeAdapter(){
        LECSharedCardsAdapter adapter = new LECSharedCardsAdapter(getActivity(), lecStoredCardList, new LECSharedCardsAdapter.LECSharedCardsListener(){
            @Override
            public void onCardClicked(LECSharedCard lecSharedCard) {
                Intent intent = new Intent(getActivity(), LECCardViewActivity.class);
                intent.putExtra(LECCardViewActivity.VIEW_CARD, lecSharedCard.makeAsCard());
                intent.putExtra(LECCardViewActivity.VIEW_CARD_ID, lecSharedCard.getId());
                intent.putExtra(LECCardViewActivity.IS_SHARED_CARD, true);
                startActivity(intent);
            }
        });


        rv.setAdapter(adapter);
    }
}
