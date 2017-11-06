package com.ammonia.catchapp.ViewTypes;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.MockNetworkManager;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateEventActivity extends BaseActivity {

    EditText textName;
    EditText textStart;
    EditText textEnd;
    EditText textLat;
    EditText textLong;
    Button buttonCreateEvent;
    ArrayList<UserProfile> newEventMembers;
    NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Create Event");

        Bundle bundle = getIntent().getExtras();
        bundle.getSerializable("NEW_EVENT_MEMBERS");

        // This is the list of the members to add to the new event. Use to send the event invites.
        newEventMembers = (ArrayList<UserProfile>) bundle.getSerializable("NEW_EVENT_MEMBERS");

        textName = (EditText) findViewById(R.id.text_event_name);
        textStart = (EditText) findViewById(R.id.text_event_start);
        textEnd = (EditText) findViewById(R.id.text_event_end);
        textLat = (EditText) findViewById(R.id.text_event_lat);
        textLong = (EditText) findViewById(R.id.text_event_long);
        buttonCreateEvent = (Button) findViewById(R.id.button_create_event);
    }

    public int getLayoutResource(){
        return R.layout.activity_create_event;
    }

    /**
     * This is the format in which we should set the date for now, will make the date pickable later.
     */
    private SimpleDateFormat getDateFormat(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'/'HH:mm:ss");
//        myFormat.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));
        return myFormat;
    }

    /**
     * Runs when the button create event is clicked.
     */
    public void createEvent (View view) throws ParseException{
        /* EditTexts in this activity have default values to make it easier as we cannot currently
        pick dates or locations */
        String name = textName.getText().toString();
        try {
            Date start = getDateFormat().parse(textStart.getText().toString());
            Date end = getDateFormat().parse(textEnd.getText().toString());
            Location location = new Location("");
            Double lati = Double.parseDouble(textLat.getText().toString());
            Double longi = Double.parseDouble(textLong.getText().toString());
            if(lati > -90 && lati < 90 && longi > -180 && longi < 180){
                location.setLatitude(Double.parseDouble(textLat.getText().toString()));
                location.setLongitude(Double.parseDouble(textLong.getText().toString()));
                nm.createEvent(name, start, end, Factory.getFactory().getUser(),
                        location, newEventMembers);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Invalid location!",
                        Toast.LENGTH_LONG).show();
                textLat.setText("-37.798375");
                textLong.setText("144.959368");
            }
        }catch(ParseException |  NumberFormatException e){
            Toast.makeText(this, "Invalid date or location!",
                    Toast.LENGTH_LONG).show();
            textStart.setText("2017-10-18/15:00:00");
            textEnd.setText("2017-10-18/15:30:00");
            textLat.setText("-37.798375");
            textLong.setText("144.959368");
        }

    }

    @Override
    public void refresh(){

    }
}
