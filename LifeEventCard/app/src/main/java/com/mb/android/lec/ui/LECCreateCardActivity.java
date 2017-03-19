package com.mb.android.lec.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mb.android.lec.LECMapActivity;
import com.mb.android.lec.R;
import com.mb.android.lec.dao.Cards;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.LECStoredCard;
import com.mb.android.lec.util.LECStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.R.attr.delay;

public class LECCreateCardActivity extends AppCompatActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener, DatePicker.OnDateChangedListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = LECCreateCardActivity.class.toString();
    private static final int CAMERA_REQUEST = 1888;
    public static final String CREATE_CARD = "card";
    private Cards selectedCard;
    private ImageView cardImg1, cardImg2, cardImg3, cardImg4;
    private int imageID;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null, outputFileForRecord = null;
    private Button startBtn;
    private Button stopBtn;
    private Button playBtn;
    private Button stopPlayBtn;
    private TextView text;
    private EditText notes;
    private DateTimeDialog myDialog;
    private CheckBox reminderCheck;
    private CheckBox locationCheck;
    private Calendar reminderCal;
    public static boolean needReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcard);

        Intent intent  = getIntent();
        if(intent != null)
            selectedCard  = (Cards)intent.getParcelableExtra(CREATE_CARD);

        cardImg1 = (ImageView) findViewById(R.id.lec_card_img1);
        cardImg2 = (ImageView) findViewById(R.id.lec_card_img2);
        cardImg3 = (ImageView) findViewById(R.id.lec_card_img3);
        cardImg4 = (ImageView) findViewById(R.id.lec_card_img4);

        cardImg1.setOnClickListener(this);
        cardImg2.setOnClickListener(this);
        cardImg3.setOnClickListener(this);
        cardImg4.setOnClickListener(this);

        //Notes
        notes = (EditText) findViewById(R.id.lec_card_notes);
        text = (TextView) findViewById(R.id.text1);
        // store it to sd card
        outputFileForRecord = LECStorage.CARD_AUDIO_DIR;
        File audioDir = new File(outputFileForRecord);
        if(!audioDir.exists()){
            audioDir.mkdir();
        }
        outputFileForRecord = audioDir.getAbsolutePath()+"/"+new Random().nextInt()+".3gpp";

//        myRecorder = new MediaRecorder();
//        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//        myRecorder.setOutputFile(outputFile);

        startBtn = (Button)findViewById(R.id.start);
        startBtn.setOnClickListener(this);

        stopBtn = (Button)findViewById(R.id.stop);
        stopBtn.setOnClickListener(this);

        playBtn = (Button)findViewById(R.id.play);
        playBtn.setOnClickListener(this);

        stopPlayBtn = (Button)findViewById(R.id.stopPlay);
        stopPlayBtn.setOnClickListener(this);



        reminderCheck = (CheckBox) findViewById(R.id.reminder_chk);
        reminderCheck.setOnCheckedChangeListener(this);

        locationCheck = (CheckBox) findViewById(R.id.current_location_chk);
        locationCheck.setOnCheckedChangeListener(this);


        reminderCal = Calendar.getInstance();
        int year = reminderCal.get(Calendar.YEAR);
        int monthOfYear = reminderCal.get(Calendar.MONTH);
        int dayOfMonth = reminderCal.get(Calendar.DAY_OF_MONTH);
        int hour = reminderCal.get(Calendar.HOUR_OF_DAY);
        int minute = reminderCal.get(Calendar.MINUTE);

        needReminder  = false;
        myDialog = new DateTimeDialog(this);
        myDialog.setTimeListener(this);
        myDialog.setDateListener(year, monthOfYear, dayOfMonth, this);

        Log.d(TAG, "Cards  " + selectedCard.cardName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_card, menu);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        needReminder = isChecked;
        int id = buttonView.getId();
        if(isChecked) {

            switch (id) {
                case R.id.reminder_chk:
                    if (isChecked) {
                        myDialog.show();
                    }
                    break;
                case R.id.current_location_chk:
                    Intent intent = new Intent(LECCreateCardActivity.this, LECMapActivity.class);
                    startActivity(intent);
                    break;
            }
        }

    }

    static class CardSave extends AsyncTask<Void, Void, Long>{

        private ProgressDialog progressDialog;
        private Context context;
        private Cards selectedCard;
        private Calendar reminderCal;

        public CardSave(Context context, Cards cards, Calendar calendar){
            this.context = context;
            this.selectedCard = cards;
            this.reminderCal = calendar;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("LEC Card Saving...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Long doInBackground(Void... params) {


            String cardJson = selectedCard.getCardsJson();
            Log.d(TAG, "" + cardJson);
            return LECQueryManager.saveLECCard(cardJson);


        }

        @Override
        protected void onPostExecute(Long cardId) {
            progressDialog.dismiss();

            Intent intent = new Intent(context, LECCardViewActivity.class);
            intent.putExtra(LECCardViewActivity.VIEW_CARD, selectedCard);
            intent.putExtra(LECCardViewActivity.VIEW_CARD_ID, cardId);

            if(needReminder){
                setReminder(intent);
            }
            ((Activity)context).startActivity(intent);
            ((Activity)context).finish();
        }

        private void setReminder(Intent myIntent){

            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(selectedCard.cardName);
            builder.setContentText(selectedCard.cardDetails);
            builder.setSmallIcon(R.drawable.ic_event_available_white_24dp);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
            builder.setContentIntent(PendingIntent.getActivity(context, 0, myIntent, 0));
            Notification notification = builder.build();

            Intent notificationIntent = new Intent(context, CustomReceiver.class);
            notificationIntent.putExtra(CustomReceiver.NOTIFICATION_ID, 1);
            notificationIntent.putExtra(CustomReceiver.NOTIFICATION, notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long futureInMillis = reminderCal.getTimeInMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
//            dateFormat.setCalendar(reminderCal);
            Log.d(TAG, ""+dateFormat.format(reminderCal.getTime()));
//            long futureInMillis  = SystemClock.elapsedRealtime() + 10000;
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);


        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_card_cancel) {
            finish();
            return true;
        }else if(id == R.id.action_card_save){
            selectedCard.lecAudio = outputFile;
            selectedCard.notes = notes.getEditableText().toString();
            Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
            CardSave cardSave = new CardSave(LECCreateCardActivity.this, selectedCard, reminderCal);
            cardSave.execute();
            return true;
        }else if(id == R.id.action_card_audio){
            Intent intent = new Intent();
            intent.setType("audio/*");
//            intent.setType("audio/3gp|audio/AMR|audio/mp3");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG,"onTimeChanged()");
        reminderCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        reminderCal.set(Calendar.MINUTE, minute);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG,"onDateChanged()");
        reminderCal.set(Calendar.YEAR, year);
        reminderCal.set(Calendar.MONTH, monthOfYear);
        reminderCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    private String getRealPathFromURI(Context context, Uri contentUri, boolean isImage, boolean isAudio) {
        CursorLoader loader = null;
        if(isAudio) {
            String[] proj = {MediaStore.Audio.Media.DATA};
            loader = new CursorLoader(context, contentUri, proj, null, null, null);
        }else if(isImage){
            String[] proj = {MediaStore.Images.Media.DATA};
            loader = new CursorLoader(context, contentUri, proj, null, null, null);
        }
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                //the selected audio.
                Uri uri = data.getData();
                outputFile = getRealPathFromURI(LECCreateCardActivity.this, uri,false, true);
                selectedCard.lecAudio = outputFile;
                Toast.makeText(this, "Path"+outputFile, Toast.LENGTH_SHORT).show();
                playBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
            }
        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo1 = (Bitmap) data.getExtras().get("data");
            BitmapDrawable photo = new BitmapDrawable(getResources(), photo1);
            Uri uri = data.getData();
            String photoPath = getRealPathFromURI(LECCreateCardActivity.this,uri, true, false);
            showImage(photo, photoPath);

        }else if(requestCode == LECMultiPhotoSelectActivity.GALLERY_REQUEST_CODE && resultCode == LECMultiPhotoSelectActivity.GALLERY_RESULT_CODE){
            ArrayList<String> images = data.getExtras().getStringArrayList(LECMultiPhotoSelectActivity.SELECTED_IMAGES);
            if(images == null || images.size()==0) return;
//            Uri uri = data.getData();
//            String image = getRealPathFromURI(LECCreateCardActivity.this, uri);
            Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));
            BitmapDrawable photo = new BitmapDrawable(getResources(), bitmap);

            showImage(photo, images.get(0));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImage(BitmapDrawable photo, String photoPath) {
        switch (imageID){
            case R.id.lec_card_img1:
                cardImg1.setBackgroundDrawable(photo);
                cardImg1.setImageBitmap(null);
                cardImg1.invalidate();
                selectedCard.lecCardImg1 = photoPath;
                break;
            case R.id.lec_card_img2:
                cardImg2.setBackgroundDrawable(photo);
                cardImg2.setImageBitmap(null);
                cardImg2.invalidate();
                selectedCard.lecCardImg2 = photoPath;
                break;
            case R.id.lec_card_img3:
                cardImg3.setBackgroundDrawable(photo);
                cardImg3.setImageBitmap(null);
                cardImg3.invalidate();
                selectedCard.lecCardImg3 = photoPath;
                break;
            case R.id.lec_card_img4:
                cardImg4.setBackgroundDrawable(photo);
                cardImg4.setImageBitmap(null);
                cardImg4.invalidate();
                selectedCard.lecCardImg4 = photoPath;
                break;

        }
    }


    @Override
    public void onClick(View v) {

        final  int id = v.getId();
        switch (id){
            case R.id.start:
                start(v);
                break;
            case R.id.stop:
                stop(v);
                break;
            case R.id.play:
                play(v);
                break;
            case R.id.stopPlay:
                stopPlay(v);
                break;

            default:
                showSelectionOption(id);
                break;

        }


    }



    public void start(View view){
        try {
            if(myRecorder == null){
                myRecorder = new MediaRecorder();
                myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                outputFile = outputFileForRecord;

                myRecorder.setOutputFile(outputFile);
            }
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            // start:it is called before prepare()
            // prepare: it is called after start() or before setOutputFormat()
            e.printStackTrace();
        } catch (IOException e) {
            // prepare() fails
            e.printStackTrace();
        }

        text.setText("Recording...");
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Start recording...",
                Toast.LENGTH_SHORT).show();
    }

    public void stop(View view){
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder  = null;

            stopBtn.setEnabled(false);
            startBtn.setEnabled(true);
            playBtn.setEnabled(true);
            text.setText("Stop recording");

            Toast.makeText(getApplicationContext(), "Stop recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            //  it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
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

    private void showSelectionOption(final int viewId){
        CharSequence colors[] = new CharSequence[] {"Gallery", "Camera"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which){
                    case 0:
                        imageID = viewId;
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                        intent.setAction(Intent.ACTION_GET_CONTENT);

//                        Intent galleryIntent = new Intent(Intent.EXTRA_ALLOW_MULTIPLE, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(intent, LECMultiPhotoSelectActivity.GALLERY_REQUEST_CODE);
                        Intent intent = new Intent(LECCreateCardActivity.this, LECMultiPhotoSelectActivity.class);
                        startActivityForResult(intent, LECMultiPhotoSelectActivity.GALLERY_REQUEST_CODE);
                        break;
                    case 1:
                        imageID = viewId;
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        break;
                }
            }
        });
        builder.show();
    }
}
