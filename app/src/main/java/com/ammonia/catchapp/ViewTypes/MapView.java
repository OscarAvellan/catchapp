package com.ammonia.catchapp.ViewTypes;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompatBase;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.LocationPin;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;

import static com.ammonia.catchapp.R.id.textView;
import static java.lang.System.exit;

public class MapView extends FragmentActivity implements OnMapReadyCallback {

    //Added because couldnt access CommonStatusCodes
    private static final int RESOLUTION_REQUIRED = 6;
    //Added because didnt get ported as constant (Possibly deprecated)
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    protected static final int SETTINGS_CHANGE_UNAVAILABLE = 8502;

    private GoogleMap mMap;
    private Event event;
    private ArrayList<UserProfile> members;
    private ArrayList<LocationPin> locationPins;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location myLocation = null;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates = false;
    private Marker myLocationMarker;
    private LatLng myLatLng = null;
    private ArrayList<Marker> personMarkers;
    private ArrayList<Marker> pinMarkers;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<Location> pastLocations;

    private GoogleMap.OnMapLongClickListener clickListener;
    private String newLocationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        clickListener = new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                createLocationPin(latLng);
            }
        };

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Log.d("Mapview", "We were passed something");
            event = (Event) getIntent().getSerializableExtra("event");
        }
        if(event == null){
            Log.d("MapView", "No Event passed to MapView Activity");
        }

        members = event.getMemberList();
        locationPins = event.getLocationPins();
        if(locationPins == null){locationPins = new ArrayList<LocationPin>();}


        pastLocations = new ArrayList<Location>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Check if the user has given the app permission to access gps location
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

        /*Check if user has GPS services enabled*/
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            //Change later to show popup telling user to turn on gps
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    myLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(myLatLng)
                            .title("My Position")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
                }
            }
        });
    }

    private void createLocationPin(final LatLng latLng) {
        newLocationName = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        builder.setView(input);

        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("MapView", "Assigning");

                newLocationName = input.getText().toString();

                Log.d("Success", "Name not null");
                NetworkManagerInterface nh;
                Location newLocation = new Location("manual");
                newLocation.setLatitude(latLng.latitude);
                newLocation.setLongitude(latLng.longitude);
                nh = Factory.getFactory().getNetworkManager();
                nh.addPinToEvent(newLocation, event, Factory.getFactory().getUser(), newLocationName);
                locationPins = event.getLocationPins();
                Log.d("Mapview", newLocationName);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        builder.setTitle("Enter a name for the new Location Pin");
        builder.show();

        Log.d("Mapview", "Name " + newLocationName);


    }


    private void locationUpdate(LocationResult locationResult) {
        Location newLocation = locationResult.getLastLocation();
        pastLocations.add(0,newLocation);
        myLocation = stabilizedLocation();
        myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        Factory.getFactory().getUser().sendLocation((float)myLocation.getLatitude(),
                (float)myLocation.getLongitude(), new Date());
        updateMyPosMarker();
        updatePersonMarkers();
        updateLocationPinMarkers();
    }

    private void updateLocationPinMarkers() {
        for(int i = 0; i < pinMarkers.size(); i++){
            pinMarkers.get(i).remove();
        }
        pinMarkers = new ArrayList<Marker>();
        for(int i = 0; i < locationPins.size();i++){
            LocationPin member = locationPins.get(i);
            Location loc = member.getLocation();
            if (loc != null) {
                Marker newMarker;
                MarkerOptions newMarkOpts = new MarkerOptions();
                newMarkOpts.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                newMarkOpts.title(member.getComment());
                newMarkOpts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                newMarker = mMap.addMarker(newMarkOpts);
                pinMarkers.add(newMarker);
            }
        }
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

    private void updateMyPosMarker(){
        LatLng latlng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        myLocationMarker.setPosition(latlng);
    }

    private void updatePersonMarkers(){
        for(int i = 0; i < personMarkers.size(); i++){
            personMarkers.get(i).remove();
        }
        personMarkers = new ArrayList<Marker>();
        for(int i = 0; i < members.size();i++){
            UserProfile member = members.get(i);
            if(member.getUserID() != Factory.getFactory().getUser().getUserID()) {
                Location loc = member.getLocation();
                if (loc != null) {
                    Marker newMarker;
                    MarkerOptions newMarkOpts = new MarkerOptions();
                    newMarkOpts.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    newMarkOpts.title(member.getFirstName() + member.getLastName());
                    newMarkOpts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    newMarker = mMap.addMarker(newMarkOpts);
                    personMarkers.add(newMarker);
                }
            }
        }

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

        googleMap.setOnMapLongClickListener(this.clickListener);

        Location eventLoc = event.getLocation();
        LatLng eventLatLng = new LatLng(eventLoc.getLatitude(), eventLoc.getLongitude());
        mMap.addMarker(new MarkerOptions().position(eventLatLng).title("Event Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 15));

        personMarkers = new ArrayList<Marker>();
        for(int i = 0; i < members.size();i++){
            UserProfile member = members.get(i);
            if(member.getUserID() != Factory.getFactory().getUser().getUserID()) {
                Location loc = member.getLocation();
                if (loc != null) {
                    Marker newMarker;
                    MarkerOptions newMarkOpts = new MarkerOptions();
                    newMarkOpts.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    newMarkOpts.title(member.getFirstName() + member.getLastName());
                    newMarkOpts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    newMarker = mMap.addMarker(newMarkOpts);
                    personMarkers.add(newMarker);
                }
            }
        }

        pinMarkers = new ArrayList<Marker>();
        for(int i = 0; i < locationPins.size();i++){
            LocationPin pin = locationPins.get(i);
            Location loc = pin.getLocation();
            if(loc != null){
                Marker newMarker;
                MarkerOptions newMarkOpts = new MarkerOptions();
                newMarkOpts.position(new LatLng(loc.getLatitude(),loc.getLongitude()));
                newMarkOpts.title(pin.getComment());
                newMarkOpts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                newMarker = mMap.addMarker(newMarkOpts);
                personMarkers.add(newMarker);
            }

        }
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
