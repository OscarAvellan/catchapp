package com.ammonia.catchapp;

import android.location.Location;

import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.LocationPin;
import com.ammonia.catchapp.structures.Message;
import com.ammonia.catchapp.structures.NetworkHandler;
import com.ammonia.catchapp.structures.UserProfile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Oscar on 7/10/2017.
 *
 * NOTE: Network Handler needs huge refactoring to be able to run an actual Local Unit Test.
 *       The tests implemented so far will be enough for now, but they won't work if something
 *       goes wrong with the server and can't check POST and PATCH requests.
 *       Robolectric is a framework that creates Shadow classes of every Android class.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NetworkHandlerTesting {

    NetworkHandler nh;
    UserProfile expected1;
    UserProfile dalzy, david, fakeUser;
    Event fakeEvent, event1;
    Message message1 = new Message(1,null,null);
    Message fakeMessage = new Message(500,null,null);
    Conversation convo = new Conversation(2,"E",null);
    Conversation fakeConvo = new Conversation(500,"E",null);
    LocationPin fakePin = new LocationPin(500,20.00,20.00,null);

    @Before
    public void setup(){
        nh = new NetworkHandler();
        expected1 = new UserProfile(1,"cristiano","ronaldo");
        dalzy = new UserProfile(15,"Dalzy","Mendoza");
        david = new UserProfile(16,"David Josue","Lopez Reyes");
        fakeUser = new UserProfile(500,"________","___________");
        fakeEvent = new Event(500,null,null,null);
        event1 = new Event(1,null,null,null);
    }

    @Test
    public void getUserByEmail_test() throws ExecutionException, InterruptedException {
        assertNotNull(nh.getUserByEmail("hfunisher@gmail.com"));
        assertNull(nh.getUserByEmail("__________"));
    }

    @Test
    public void searchUser_test(){
        assertNotNull( nh.searchUser(david.getFirstName()) );
        assertNull(nh.searchUser(fakeUser.getFirstName()));
    }

    @Test
    public void getUserLocation_test(){
        assertNotNull(nh.getUserLocation(dalzy));
        assertNull(nh.getUserLocation(fakeUser));
    }

    @Test
    public void getEmailAddressByUser_test(){
        assertNotNull(nh.getEmailAddressByUser(dalzy));
        assertNull(nh.getEmailAddressByUser(fakeUser));
    }

    @Test
    public void getLocationByEvent_test(){
        assertNotNull(nh.getLocationByEvent(new Event(1,null,null,null)) );
        assertNull(nh.getLocationByEvent(fakeEvent));
    }

    @Test
    public void getSharedEvents_test() throws ParseException {
        assertNotNull(nh.getSharedEvents(dalzy,david));
        assertNull(nh.getSharedEvents(dalzy,fakeUser));
    }

    @Test
    public void getPinsByEvent_test(){
        assertNull(nh.getPinsByEvent(fakeEvent));
    }

    @Test
    public void getCreatorByEvent_test(){
        assertNotNull(nh.getCreatorByEvent(event1));
        assertNull(nh.getCreatorByEvent(fakeEvent));
    }

    @Test
    public void getMemberListByEvent_test(){
        assertNotNull(nh.getMemberListByEvent(event1));
        assertNull(nh.getMemberListByEvent(fakeEvent));
    }

    @Test
    public void getSenderByMessage_test(){
        assertNotNull(nh.getSenderByMessage(message1));
        assertNull(nh.getSenderByMessage(fakeMessage));
    }

    @Test
    public void getEventByConversation_test() throws ParseException {
        assertNotNull(nh.getEventByConversation(convo));
        assertNull(nh.getEventByConversation(fakeConvo));
    }

    @Test
    public void getConversationByMessage_test(){
        assertNotNull(nh.getConversationByMessage(message1));
        assertNull(nh.getConversationByMessage(fakeMessage));
    }

    @Test
    public void getCreatorByLocationPin_test(){
        assertNull(nh.getCreatorByLocationPin(fakePin));
    }

    @Test
    public void getUsersByConversation_test(){
        assertNotNull(nh.getUsersByConversation(convo));
        assertNull(nh.getUsersByConversation(fakeConvo));
    }

    @Test
    public void getAccountBioByUser_test(){
        assertNotNull(nh.getAccountBioByUser(dalzy));
        assertNull(nh.getAccountBioByUser(fakeUser));
    }

    @Test
    public void getAdminListByEvent_test(){
        assertNotNull(nh.getAdminListByEvent(event1));
        assertNull(nh.getAdminListByEvent(fakeEvent));
    }

    @Test
    public void getMessagesByConversation_test() throws ParseException {
        assertNotNull(nh.getMessagesByConversation(convo));
        assertNull(nh.getMessagesByConversation(fakeConvo));
    }

    @Test
    public void getEventsByUser_test() throws ParseException {
        assertNotNull(nh.getEventsByUser(dalzy));
        assertNull(nh.getEventsByUser(fakeUser));
    }

    @Test
    public void getConversationByUser_test(){
        assertNotNull(nh.getConversationByUser(dalzy));
        assertNull(nh.getConversationByUser(fakeUser));
    }
}
