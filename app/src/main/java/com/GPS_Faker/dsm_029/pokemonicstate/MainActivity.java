package com.GPS_Faker.dsm_029.pokemonicstate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.*;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //권한 부여 메소드
    protected void permissionCheck() {
        //GPS 권한
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
        }
    }

    private GoogleMap map;
    private LatLng loc_center;
    private Marker marker;
    private Button setButton, resetButton;
    private LocationManager locationManager;
//    private LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    };
    private ImageView aim;
    private ActionBar actionBar;
    private boolean isGpsEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);

        permissionCheck();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AdView adView = (AdView) findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        setButton = (Button) findViewById(R.id.setButton);
        setButton.setOnClickListener(set);

        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(reset);

        aim = (ImageView) findViewById(R.id.aimMark);
        aim.setImageResource(R.drawable.aimmark);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
    }

    Button.OnClickListener set = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                loc_center = map.getCameraPosition().target;

                try {
                    marker.remove();
                } catch (Exception e) {
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(loc_center)
                        .visible(true)
                        //.alpha(0.8f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
                marker = map.addMarker(markerOptions);

                Toast.makeText(MainActivity.this, "위도 : " + loc_center.latitude + "\n경도 : " + loc_center.longitude, Toast.LENGTH_SHORT).show();
                setMockLocation(loc_center);
            }
         catch (Exception e) {
                //Toast.makeText(MainActivity.this, "Location set failed!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Button.OnClickListener reset = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                marker.remove();
            } catch (Exception e) {
            }
            try {
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception e) { }
        }
    };

    private void setMockLocation(LatLng center) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        Location location = new Location(LocationManager.GPS_PROVIDER);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        try {
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }

        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, true, 0, 1);

        location.setLatitude(center.latitude);
        location.setLongitude(center.longitude);
        location.setAccuracy(10);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        try {
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);
        }
        catch (Exception e)
        {
            //
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_button) {

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}