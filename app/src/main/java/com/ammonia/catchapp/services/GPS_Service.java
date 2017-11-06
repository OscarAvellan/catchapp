package com.ammonia.catchapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ammonia.catchapp.factory.Factory;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.google.android.gms.common.ConnectionResult.RESOLUTION_REQUIRED;
import static com.google.android.gms.location.LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE;

/**
 * Created by morley on 9/10/17.
 */

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location myLocation;


    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){

        /*Check if the GPS is enabled*/
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enabled){
            Intent i = new Intent("location_update");
            i.putExtra("isEnabled", false);
            sendBroadcast(i);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /*Get initial location form lastLocation*/
        //noinspection MissingPermission
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    myLocation = location;
                    Log.d("Service", "Location found");
                }
            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Intent i = new Intent("location_update");
                Location newLocation = locationResult.getLastLocation();
                i.putExtra("isEnabled", true);
                i.putExtra("locationResult", locationResult);
                sendBroadcast(i);
                Factory.getFactory().setLastLocation(locationResult.getLastLocation());
                Log.d("Service", "newLocation" + locationResult.getLastLocation().toString());
            }
        };

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        //noinspection MissingPermission
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
    }

    @Override
    public void onDestroy() {

    }
}
