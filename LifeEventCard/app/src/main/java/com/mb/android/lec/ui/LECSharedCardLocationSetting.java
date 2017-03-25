package com.mb.android.lec.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.util.LECSharedPreferenceManager;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Text;

public class LECSharedCardLocationSetting extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 525;
    private TextView txtViewSharedLocationPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecshared_card_location_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtViewSharedLocationPath = (TextView) findViewById(R.id.shared_location_path);
        String location = LECSharedPreferenceManager.getLECCardSharedLocation(this);
        if(!TextUtils.isEmpty(location))
         txtViewSharedLocationPath.setText("PATH:" + location);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/zip");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(LECSharedCardLocationSetting.this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            String path = getRealPathFromURI(this, uri);
            if(!TextUtils.isEmpty(path)) {
                String location = FilenameUtils.getFullPathNoEndSeparator(path);
                txtViewSharedLocationPath.setText("PATH:" + location);
                LECSharedPreferenceManager.saveLECCardSharedLocation(LECSharedCardLocationSetting.this, location);
            }else {
                Toast.makeText(LECSharedCardLocationSetting.this, "Invalid Path Selection. Please try again", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        if("content".equalsIgnoreCase(contentUri.getScheme())){
            String[] proj = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if(cursor.moveToFirst()){
                    return cursor.getString(column_index);
                }

            }catch (Exception e){

            }
        }else if("file".equalsIgnoreCase(contentUri.getScheme())){
            System.out.print(contentUri.getPath());
            return "/"+FilenameUtils.getPath(contentUri.getPath());
//            file:///storage/emulated/0/leccardtemp/lec-1143409468.zip
        }
        return "";
    }

}
