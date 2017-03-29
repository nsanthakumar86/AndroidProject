package com.mb.android.lec;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LECMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    public static final int REQ_CODE = 222;
    public static final int RES_CODE_LOCATION = 223;
    public static final String LOCATION = "location";
    private GoogleMap mMap;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private String locationStr="";
    private boolean isViewMap;
    private String address = "Your Location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                locationStr = bundle.getString(LOCATION);
                if (!TextUtils.isEmpty(locationStr)) {
                    isViewMap = true;
                }
            }

        }
        if(!isViewMap) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }


    }


    @Override
    public void onLocationChanged(Location location) {
        if(mMap != null){
            LatLng cLoc = new LatLng(location.getLatitude(), location.getLongitude());
            locationStr = cLoc.latitude+","+cLoc.longitude+"";
            mMap.addMarker(new MarkerOptions().position(cLoc).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cLoc, 9.0f));
            mMap.setOnMarkerClickListener(this);
            getAddressFromLocation(cLoc.latitude, cLoc.longitude, LECMapActivity.this, handler);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        setResult(RES_CODE_LOCATION);
        Intent intent = new Intent();
        intent.putExtra(LOCATION , locationStr);
        setResult(RES_CODE_LOCATION, intent);
        finish();
//        super.onBackPressed();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                address = msg.getData().getString("address");
            }
            super.handleMessage(msg);
        }


    };
    public  void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(",");
                        }
                        sb.append(address.getLocality()).append(",");
                        sb.append(address.getPostalCode()).append(",");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Locate map", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Unable to get address.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(isViewMap){
            String[] locArr = locationStr.split(",");
            LatLng cLoc = new LatLng(Double.valueOf(locArr[0]).doubleValue(), Double.valueOf(locArr[1]).doubleValue());
            mMap.addMarker(new MarkerOptions().position(cLoc).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cLoc, 9.0f));
            mMap.setOnMarkerClickListener(this);
            getAddressFromLocation(cLoc.latitude, cLoc.longitude, LECMapActivity.this, handler);
        }
//        mMap.setMaxZoomPreference(1);
        ;
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setTitle(address);
        return false;
    }
}
