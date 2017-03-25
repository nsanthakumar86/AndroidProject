package com.mb.android.lec.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.LECMapActivity;
import com.mb.android.lec.R;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.util.LECStorage;
import com.mb.android.lec.util.LECZipManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static com.mb.android.lec.util.LECStorage.getRandamLECFileNameToShare;

public class LECCardViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String VIEW_CARD = "view_card";
    public static final String VIEW_CARD_ID = "view_card_id";
    public static final String IS_SHARED_CARD = "isSharedCard";
    private ImageView cardImg1, cardImg2, cardImg3, cardImg4;
    private MediaPlayer myPlayer;
    private TextView text;
    private Button playBtn;
    private Button stopPlayBtn;
    private String outputFile;
    private Cards selectedCard;
    private Long cardID;
    private boolean isSharedCard;
    private TextView locate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leccard_view);

        Intent intent  = getIntent();
        if(intent != null) {
            selectedCard = (Cards) intent.getParcelableExtra(VIEW_CARD);
            cardID = intent.getLongExtra(VIEW_CARD_ID,-1);
            isSharedCard = intent.getBooleanExtra(IS_SHARED_CARD, false);
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

        locate = (TextView)findViewById(R.id.locate);
        if(!TextUtils.isEmpty(selectedCard.location)){
            locate.setVisibility(View.VISIBLE);
        }else {
            locate.setVisibility(View.GONE);
        }

        locate.setOnClickListener(this);
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
            case R.id.locate:
                Intent intent = new Intent(LECCardViewActivity.this, LECMapActivity.class);
                intent.putExtra(LECMapActivity.LOCATION, selectedCard.location);
                startActivity(intent);
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
                if(!isSharedCard) {
                    LECQueryManager.deleteLECCard(cardID);
                }else {
                    LECQueryManager.deleteSharedLECCard(cardID);
                }
                finish();
                break;
            case R.id.action_share:
                LECCardShareTask lecCardShareTask = new LECCardShareTask(LECCardViewActivity.this, selectedCard);
                lecCardShareTask.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class LECCardShareTask extends AsyncTask<Void, Void, File>
    {
        private Cards sharingCard;
        private Context context;
        private ProgressDialog progressDialog;
        LECCardShareTask(Context context, Cards cards){
            this.context = context;
            this.sharingCard = cards;
        }


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected File doInBackground(Void... params) {

            File folder = shareCardViaBluetooth();
            if(folder != null){
                File[] filesToSend = new File(folder.getAbsolutePath()).listFiles();

//                ArrayList<Uri> files = new ArrayList<Uri>();
                String[] compressFiles = new String[filesToSend.length];
                int i = 0;
                for (File file : filesToSend) {
//                    Uri uri = Uri.fromFile(file);
//                    files.add(uri);
                    compressFiles[i++] = file.getAbsolutePath();
                }
                File zipFile = new File(folder.getAbsolutePath() + ".zip");
                try {
                    LECZipManager.zip(compressFiles, zipFile.getAbsolutePath());
                    deleteDirectory(folder);
                    return  zipFile;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        public void writeToFile(File path, String data)
        {


            final File file = new File(path, LECStorage.lecCardInfoFile);

            // Save your stream, don't forget to flush() it before closing it.

            try
            {
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(data);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
        public  boolean deleteDirectory(File path) {
            if( path.exists() ) {
                File[] files = path.listFiles();
                if (files == null) {
                    return true;
                }
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
            return( path.delete() );
        }

        @Override
        protected void onPostExecute(File shareFile) {
            progressDialog.dismiss();

            if(shareFile != null) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("application/zip");
                sharingIntent.setPackage("com.android.bluetooth");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
                startActivity(Intent.createChooser(sharingIntent, "Share LEC Card"));
            }
        }

        private File shareCardViaBluetooth(){
            File folder = makeTempFolder();
            if(folder != null){
                makeLECShareFolder(folder);

            }
            return folder;
        }

        private void makeLECShareFolder(File folder){
            if(!TextUtils.isEmpty(selectedCard.lecCardImg1)){
                File imgSrc = new File(selectedCard.lecCardImg1);
                String fileName = imgSrc.getName();
                File imgDest = new File(folder, fileName);
                copy(imgSrc, imgDest);

            }
            if(!TextUtils.isEmpty(selectedCard.lecCardImg2)){
                File imgSrc = new File(selectedCard.lecCardImg2);
                String fileName = imgSrc.getName();
                File imgDest = new File(folder, fileName);
                copy(imgSrc, imgDest);
            }
            if(!TextUtils.isEmpty(selectedCard.lecCardImg3)){
                File imgSrc = new File(selectedCard.lecCardImg3);
                String fileName = imgSrc.getName();
                File imgDest = new File(folder, fileName);
                copy(imgSrc, imgDest);
            }
            if(!TextUtils.isEmpty(selectedCard.lecCardImg4)){
                File imgSrc = new File(selectedCard.lecCardImg4);
                String fileName = imgSrc.getName();
                File imgDest = new File(folder, fileName);
                copy(imgSrc, imgDest);
            }
            if(!TextUtils.isEmpty(selectedCard.lecAudio)){
                File imgSrc = new File(selectedCard.lecAudio);
                String fileName = imgSrc.getName();
                File imgDest = new File(folder, fileName);
                copy(imgSrc, imgDest);
            }

            writeToFile(folder, selectedCard.getCardsJson());
        }

        public void copy(File src, File dst)  {

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dst);

// Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(in != null) try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(out != null){
                    try {
                        out.close();
                        out = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private File makeTempFolder(){
            File folder = getRandamLECFileNameToShare();
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }else {
                File[] files = folder.listFiles();
                if(files != null && files.length >0) {
                    int j;
                    for(j = 0; j < files.length; j++) {
                        files[j].delete();
                    }
                }
            }
            if (success) {
                // Do something on success
                return folder;
            } else {
                // Do something else on failure
                Toast.makeText(context, "System is not supporting to share. Please check your exterenal storage.", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
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
