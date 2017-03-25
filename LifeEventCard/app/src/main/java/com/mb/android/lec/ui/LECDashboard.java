package com.mb.android.lec.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.db.LECQueryManager;
import com.mb.android.lec.db.LECUser;
import com.mb.android.lec.db.UserSession;
import com.mb.android.lec.util.ImageUtil;
import com.mb.android.lec.util.LECSharedPreferenceManager;

public class LECDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private LECCardsTypeFragment lecCardsTypeFragment;
    private LECSavedCardsFragment savedCardsFragment;
    private LECSharedCardsFragment sharedCardsFragment;
    private LECProfileFragment lecProfileFragment;
    private LECAboutFragment lecAboutFragment;
    private FloatingActionButton fab;
    private static final String TAG = LECDashboard.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecdashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(LECDashboard.this);
                LayoutInflater inflater = LECDashboard.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.lec_other_card_type, null);
                builder.setView(dialogView);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String eventName = ((EditText)dialogView.findViewById(R.id.card_event_name)).getText().toString();
                        String eventDetails = ((EditText)dialogView.findViewById(R.id.card_event_details)).getText().toString();
                        Log.d(TAG, "Event Name :"+eventName+" // Event Details : "+eventDetails);
                        LECQueryManager.saveLECOtherEvents(eventName, eventDetails, UserSession.getInstance().getActiveUser().getId());
                        lecCardsTypeFragment.refreshEventList();
                        fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, lecCardsTypeFragment).commit();
                        dialog.dismiss();

                    }
                });
                builder.show();


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final String emailId = LECSharedPreferenceManager.getLoggedinUserMailId(LECDashboard.this);
        Log.d(TAG, ""+emailId);


        //Log.d(TAG, ""+lecUser!=null?lecUser.toString():" NULL");

        final View headerView = navigationView.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.nav_header_email)).setText(emailId);
        //((TextView)headerView.findViewById(R.id.nav_header_name)).setText(lecUser.getUserFirstName() +" "+lecUser.getUserLastName());
        ((TextView)headerView.findViewById(R.id.nav_header_name)).setText("Kumar" +" "+ "S");
        final LECUser lecUser = UserSession.getInstance().getActiveUser();

        if(lecUser == null) finish();

        if(!TextUtils.isEmpty(lecUser.getProfileImg())){
            ImageView userImage = (ImageView)headerView.findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(lecUser.getProfileImg());
            ImageUtil.setCircularImage(bitmap, userImage);
        }

//        FrameLayout frame = (FrameLayout) findViewById(R.id.content_lecdashboard);

        fragmentManager = getSupportFragmentManager();
        lecCardsTypeFragment = new LECCardsTypeFragment();
        lecProfileFragment = LECProfileFragment.newInstance("","");
        lecAboutFragment = LECAboutFragment.newInstance("","");
        savedCardsFragment = LECSavedCardsFragment.newInstance();
        sharedCardsFragment = LECSharedCardsFragment.newInstance();

        lecProfileFragment.setListener(new LECProfileFragment.OnFragmentInteractionListener() {
            @Override
            public void onProfileImageChanged() {
                final String emailId = LECSharedPreferenceManager.getLoggedinUserMailId(LECDashboard.this);
                final LECUser lecUser = LECQueryManager.getUserByMailId(emailId);
                 UserSession.getInstance().setActiveUser(lecUser);
                if(!TextUtils.isEmpty(lecUser.getProfileImg())){
                    ImageView userImage = (ImageView)headerView.findViewById(R.id.imageView);
                    Bitmap bitmap = BitmapFactory.decodeFile(lecUser.getProfileImg());
                    ImageUtil.setCircularImage(bitmap, userImage);
                }
            }
        });
        fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, lecCardsTypeFragment, "CardType").commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.lecdashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            LECSharedPreferenceManager.loggedOutLECUser(LECDashboard.this);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            if(fab.isShown())
                fab.hide();

            fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, lecAboutFragment).commit();
        } else if (id == R.id.nav_profile) {
            if(fab.isShown())
            fab.hide();

            fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, lecProfileFragment).commit();
        } else if (id == R.id.nav_life_event) {

            if(!fab.isShown())
                fab.show();
            fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, lecCardsTypeFragment).commit();

        }else if (id == R.id.nav_gallery) {

            if(!fab.isShown())
                fab.show();
            Intent intent = new Intent(LECDashboard.this, LECMultiPhotoSelectActivity.class);
            startActivity(intent);


        }else if (id == R.id.nav_lec_stored_cards) {

            if(fab.isShown())
                fab.hide();
            fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, savedCardsFragment).commit();


        }else if(id == R.id.nav_shared_card_location){
            Intent intent  = new Intent(LECDashboard.this, LECSharedCardLocationSetting.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_lec_shared_cards) {

            if(fab.isShown())
                fab.hide();
            fragmentManager.beginTransaction().replace(R.id.content_lecdashboard, sharedCardsFragment).commit();
//            Toast.makeText(LECDashboard.this, "Shared LEC Cards", Toast.LENGTH_SHORT).show();


        }   else if (id == R.id.nav_logout) {
            LECSharedPreferenceManager.loggedOutLECUser(LECDashboard.this);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
