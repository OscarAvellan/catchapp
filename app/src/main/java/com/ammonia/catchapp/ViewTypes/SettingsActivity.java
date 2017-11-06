package com.ammonia.catchapp.ViewTypes;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.LocationPin;
import com.ammonia.catchapp.structures.Message;
import com.ammonia.catchapp.structures.NetworkHandler;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.BaseActivity;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Settings");
    }

   @Override
    public int getLayoutResource() {
        return R.layout.activity_settings;
    }

    /* TESTING OSCAR */
    public void sendRequest(View view) {
        NetworkHandler nh = new NetworkHandler();

        UserProfile dalzy = new UserProfile(15,"Dalzy","Mendoza");
        UserProfile david = new UserProfile(16,"David Josue","Lopez Reyes");
        UserProfile user17 = new UserProfile(17,null,null);
        Event event = new Event(1,null,null,null);
        Message message = new Message(1,null,null);
        Conversation convo = new Conversation(3,"E",null);
        Conversation convo2 = new Conversation(1,"P",null);
        LocationPin pin = new LocationPin(1,12.12,12.12,null);
        Location location = new Location("manual");
        location.setLatitude(99.99);
        location.setLongitude(99.99);

        //nh.createUser("thierry","henry","thierry@henry");  // NOT WORKING
        /*ArrayList<UserProfile> members = new ArrayList<>();
        members.add(david);
        nh.createEvent( "comp30026 exam",null,null,david,location,members);*/

        /*ArrayList<UserProfile> users = new ArrayList<>();
        users.add(dalzy);
        users.add(david);
        users.add(user17);
        nh.createConversation(users);*/

        nh.sendMessageToConversation(new Message(0,"baba baba baba",null),
                new Conversation(2,null,null), dalzy);

        /*Event eve = new Event(1,null,null,null);
        nh.createLocationPin(location,eve,dalzy,"arsenal hq");*/


        /*nh.sendFriendRequest(david,user17);
        nh.acceptFriendRequest(user17);
        nh.declineFriendRequest(user17);*/



        //nh.sendEventInvite(new Event(6,null,null,null),david,user17);
        /*nh.acceptEventInvite(new Event(6,13,null,null,null));
        nh.rejectEventInvite(new Event(6,13,null,null,null));
        nh.deleteEvent(new Event(6,null,null,null));*/

        //nh.getFriendInvitesByUser(new UserProfile(2,null,null));
        //nh.getConversationByUser(new UserProfile(4,null,null));

        nh.sendIdTokenToServer("576768686");

        //nh.getEventInvitesByUser(user);

    }

    @Override
    public void refresh(){

    }

}
