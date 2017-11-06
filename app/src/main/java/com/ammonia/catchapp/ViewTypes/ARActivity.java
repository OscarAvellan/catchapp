package com.ammonia.catchapp.ViewTypes;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.structures.UserProfile;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;

import static com.ammonia.catchapp.ViewTypes.MapView.REQUEST_CHECK_SETTINGS;
import static com.google.android.gms.common.ConnectionResult.RESOLUTION_REQUIRED;
import static com.google.android.gms.location.LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE;
import static java.lang.System.exit;

/**
 * Created by morley on 24/08/17.
 * Edited by Dalzy on 10/09/17.
 */

public class ARActivity extends AppCompatActivity {

    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    Location myLocation;
    LocationCallback mLocationCallback;
    boolean requestingLocationUpdates = false;
    UserProfile user = null;
    AROverlayView myOverlay = null;
    private BroadcastReceiver broadcastReceiver;

    private ArrayList<Location> pastLocations;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pastLocations = new ArrayList<Location>();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            user = (UserProfile)getIntent().getSerializableExtra("user");
        }

        if(user == null){
            Log.d("AR", "No user passed to AR activity");
            exit(0);
        }else{
            Log.d("AR", user.getFirstName());
        }

        setContentView(R.layout.activity_ar);

        FrameLayout arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);

        ARDisplayView arDisplay = new ARDisplayView(getApplicationContext(), this);
        arViewPane.addView(arDisplay);

        final AROverlayView arContent = new AROverlayView(getApplicationContext(), user);
        arViewPane.addView(arContent);

        myOverlay = arContent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Service", "Receieved");
                    Log.d("Service", ((LocationResult)intent.getExtras().get("locationResult")).toString());
                    if((boolean)intent.getExtras().get("isEnabled")) {
                        locationUpdate((LocationResult)intent.getExtras().get("locationResult"));
                    }else{
                        Intent newIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(newIntent);
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }


    private void locationUpdate(LocationResult locationResult){
        Location loc = locationResult.getLastLocation();
        pastLocations.add(0,loc);
        if(pastLocations.size() > 10){
            pastLocations.remove(10);
        }
        myLocation = stabilizedLocation();
        myOverlay.onLocationChanged(myLocation);
        Factory.getFactory().getUser().sendLocation((float)loc.getLatitude(),
                (float)loc.getLongitude(), new Date());
    }

    private Location stabilizedLocation(){
        Location newLocation = new Location("manual");
        Location baseLocation = pastLocations.get(0);
        double newLat = baseLocation.getLatitude();
        double newLong = baseLocation.getLongitude();
        for(int i = 1; i < pastLocations.size();i++){
            float modifier = 1 / (i * 2);
            newLat += modifier * (pastLocations.get(i).getLatitude() - baseLocation.getLatitude());
            newLong += modifier * (pastLocations.get(i).getLongitude() - baseLocation.getLongitude());
        }
        newLocation.setLatitude(newLat);
        newLocation.setLongitude(newLong);
        return newLocation;
    }
}
