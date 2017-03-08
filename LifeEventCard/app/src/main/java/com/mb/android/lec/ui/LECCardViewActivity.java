package com.mb.android.lec.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECQueryManager;

public class LECCardViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String VIEW_CARD = "view_card";
    public static final String VIEW_CARD_ID = "view_card_id";
    private ImageView cardImg1, cardImg2, cardImg3, cardImg4;
    private MediaPlayer myPlayer;
    private TextView text;
    private Button playBtn;
    private Button stopPlayBtn;
    private String outputFile;
    private Cards selectedCard;
    private Long cardID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leccard_view);

        Intent intent  = getIntent();
        if(intent != null) {
            selectedCard = (Cards) intent.getParcelableExtra(VIEW_CARD);
            cardID = intent.getLongExtra(VIEW_CARD_ID,-1);
        }

        cardImg1 = (ImageView) findViewById(R.id.lec_card_img1);
        cardImg2 = (ImageView) findViewById(R.id.lec_card_img2);
        cardImg3 = (ImageView) findViewById(R.id.lec_card_img3);
        cardImg4 = (ImageView) findViewById(R.id.lec_card_img4);

        if(!TextUtils.isEmpty(selectedCard.cardName)){
            ((TextView)findViewById(R.id.event_name)).setText(selectedCard.cardName);
        }
        if(!TextUtils.isEmpty(selectedCard.lecCardImg1)){
            cardImg1.setBackgroundDrawable(getBitMapFromPath(selectedCard.lecCardImg1));
            findViewById(R.id.card_img1).setVisibility(View.VISIBLE);
        }



        if(!TextUtils.isEmpty(selectedCard.lecCardImg2)){
            cardImg2.setBackgroundDrawable(getBitMapFromPath(selectedCard.lecCardImg2));
            findViewById(R.id.card_img2).setVisibility(View.VISIBLE);
        }


        if(!TextUtils.isEmpty(selectedCard.lecCardImg3)){
            cardImg3.setBackgroundDrawable(getBitMapFromPath(selectedCard.lecCardImg3));
            findViewById(R.id.card_img3).setVisibility(View.VISIBLE);
        }

        if(!TextUtils.isEmpty(selectedCard.lecCardImg4)){
            cardImg4.setBackgroundDrawable(getBitMapFromPath(selectedCard.lecCardImg4));
            findViewById(R.id.card_img4).setVisibility(View.VISIBLE);
        }
        playBtn = (Button)findViewById(R.id.play);
        playBtn.setOnClickListener(this);

        stopPlayBtn = (Button)findViewById(R.id.stopPlay);
        stopPlayBtn.setOnClickListener(this);

        if(TextUtils.isEmpty(selectedCard.lecAudio)){
            findViewById(R.id.audio_layer).setVisibility(View.GONE);
        }else {
            outputFile = selectedCard.lecAudio;playBtn.setEnabled(true);
        }

        if(!TextUtils.isEmpty(selectedCard.notes)){
            ((TextView)findViewById(R.id.notes)).setText(selectedCard.notes);
        }else {
            findViewById(R.id.notes).setVisibility(View.GONE);
        }
        text = (TextView) findViewById(R.id.text1);
    }

    private Drawable getBitMapFromPath(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        BitmapDrawable photo = new BitmapDrawable(getResources(), bitmap);
        return photo;
    }
    @Override
    public void onClick(View v) {
        final  int id = v.getId();
        switch (id){

            case R.id.play:
                play(v);
                break;
            case R.id.stopPlay:
                stopPlay(v);
                break;
            default:

                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_done:
                finish();
                break;
            case R.id.action_delete:
                LECQueryManager.deleteLECCard(cardID);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void play(View view) {
        try{
            myPlayer = new MediaPlayer();
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();
            myPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setEnabled(true);
                    stopPlayBtn.setEnabled(false);
                    text.setText("Stop playing");
                }
            });
            playBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
            text.setText("Playing...");

            Toast.makeText(getApplicationContext(), "Start play the recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopPlay(View view) {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
                playBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                text.setText("Stop playing");

                Toast.makeText(getApplicationContext(), "Stop playing the recording...",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
