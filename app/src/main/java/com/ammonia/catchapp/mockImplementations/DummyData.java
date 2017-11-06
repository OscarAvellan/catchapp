package com.ammonia.catchapp.mockImplementations;

import android.location.Location;

import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.LocationPin;
import com.ammonia.catchapp.structures.Message;
import com.ammonia.catchapp.structures.UserProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by morley on 6/09/17.
 */


public class DummyData {

    public static float dummyLat = -37.8f;
    public static float dummyLong = 144.95f;
    public static String dummyMsgBody = "This is a fake message";
    public static int defaultGroupMembers = 5;
    public static int defaultEventPins = 3;
    public static int defaultConversationID = 1234;

    public static Message getDummyMessage(int senderID){
        Message dummyMsg = new Message(senderID,
                dummyMsgBody,
                new Date());
        return dummyMsg;
    }
    public static Message getDummyEventInvitation(int senderID){
        Message dummyMsg = new Message(senderID,
                dummyMsgBody,
                new Date());
        return dummyMsg;
    }
    public static Message getDummyFriendInvitation(int senderID){
        Message dummyMsg = new Message(senderID,
                dummyMsgBody,
                new Date());
        return dummyMsg;
    }
    public static Conversation getDummyConversation(int numMsgs, boolean hasEvent){
        ArrayList<Message> dummyMsgList = new ArrayList<Message>();
        int j = 0;
        for(int i = 0; i < numMsgs;i++){
            Message dummyMsg = getDummyMessage(j);
            dummyMsgList.add(dummyMsg);
            j++;
            if (j + 1 > defaultGroupMembers){
                j = 0;
            }
        }

        Event dummyEvent;
        Conversation dummyConversation;
        ArrayList<UserProfile> memberProfiles;
        if (hasEvent){
            dummyEvent = getDummyEvent(defaultGroupMembers,defaultEventPins);
            memberProfiles = dummyEvent.getMemberList();
            dummyConversation = new Conversation(defaultConversationID,
                    true);
        }else{
            dummyEvent = null;
            memberProfiles = new ArrayList<UserProfile>();
            for (int i = 0; i < 2; i++) {
                memberProfiles.add(getDummyUserProfile(i));
            }
            dummyConversation = new Conversation(defaultConversationID,true);
        }


        return dummyConversation;
    }

    public static UserProfile getDummyUserProfile(int i){
        UserProfile dummyProfile = new UserProfile(i,"John" + i, "Smith" + i);
        return dummyProfile;
    }

    public static LocationPin getDummyLocationPin(float latOffset, float longOffset,int creatorID){
        UserProfile dummyUser = getDummyUserProfile(creatorID);
        LocationPin dummyPin = new LocationPin(1,dummyLat + latOffset,
                dummyLong + longOffset,
                "nope");
        return dummyPin;
    }

    /*This is the event for a meetup to work on the project, but due to confusion about
    * the study location, everyone ended up at different buildings around the university campus.
    * After this was realized, Jonathan created a LocationPin at old Eng to signify the
    * real meetup location.*/
    public static Event getComplexEvent(){
        //Initialize the Event object
        Event newEvent = null;
        //Initialize list structures to use in event
        ArrayList<UserProfile> dummyMemberList = new ArrayList<UserProfile>();
        ArrayList<UserProfile> dummyAdminList = new ArrayList<UserProfile>();
        ArrayList<LocationPin> dummyLocationPinList = new ArrayList<LocationPin>();

        //Generate fake start and end times
        Date currentTime = new Date();
        //Half an hour before creation
        Date startTime = new Date(currentTime.getTime() - (1000 * 60 * 30));
        //Half an hour after creation
        Date endTime = new Date(currentTime.getTime() + (1000 * 60 * 30));

        //Generate some fake locations for our UserProfiles
        Location baillieu = new Location("manual");
        baillieu.setLatitude(-37.7985);
        baillieu.setLongitude(144.9596);
        Location erc = new Location("manual");
        erc.setLatitude(-37.7996);
        erc.setLongitude(144.9627);
        Location kwongLee = new Location("manual");
        kwongLee.setLatitude(-37.8042);
        kwongLee.setLongitude(144.9610);
        Location aliceHoy = new Location("manual");
        aliceHoy.setLatitude(-37.7984);
        aliceHoy.setLongitude(144.9632);
        Location oldEng = new Location("manual");
        oldEng.setLatitude(-37.7994);
        oldEng.setLongitude(144.9613);

        /*
        //Generate some fake UserProfiles
        UserProfile user1 = new UserProfile("Dalzy", "Mendoza", "dalzymendoza@nomail.com",
                "My name is Dalzy Mendoza and I attend the University of Melbourne",
                baillieu);
        UserProfile user2 = new UserProfile("David", "Lopez", "davidlopez@nomail.com",
                "My name is David Lopez and I attend the University of Melbourne",
                erc);
        UserProfile user3 = new UserProfile("Jonathan", "Rodrigo", "jonathanrodrigo@nomail.com",
                "My name is Jonathan Rodrigo and I attend the University of Melbourne",
                kwongLee);
        UserProfile user4 = new UserProfile("Josh", "Morley", "joshmorley@nomail.com",
                "My name is Josh Morley and I attend the University of Melbourne",
                aliceHoy);
        UserProfile user5 = new UserProfile("Oscar", "Avellan", "oscaravellan@nomail.com",
                "My name is Oscar Avellan and I attend the University of Melbourne",
                oldEng);



        //Generate fake location pin
        LocationPin pin1 = new LocationPin((float)kwongLee.getLatitude(),(float)kwongLee.getLongitude(),user3,startTime);

        //Add all users to member list
        dummyMemberList.add(user1);
        dummyMemberList.add(user2);
        dummyMemberList.add(user3);
        dummyMemberList.add(user4);
        dummyMemberList.add(user5);

        //Add josh and oscar as admin
        dummyAdminList.add(user4);
        dummyAdminList.add(user5);

        //Add pin to location pin list
        dummyLocationPinList.add(pin1);

        newEvent = new Event(0035, "Project Work Session", user4, dummyAdminList,
                dummyMemberList, -37.7964f, 144.9612f, dummyLocationPinList, startTime, endTime);


        return newEvent;
*/
        return null;
    }

    public static Event getDummyEvent(int numUsers, int numPins){
        ArrayList<UserProfile> dummyMemberList = new ArrayList<UserProfile>();
        ArrayList<UserProfile> dummyAdminList = new ArrayList<UserProfile>();
        ArrayList<LocationPin> dummyLocationPinList = new ArrayList<LocationPin>();
        UserProfile dummyCreator = getDummyUserProfile(0);
        for (int i=0; i < numUsers; i++){
            UserProfile dummyUser;
            dummyUser = getDummyUserProfile(i);
            dummyMemberList.add(dummyUser);
            if((i % 2) == 0){
                dummyAdminList.add(dummyUser);
            }
        }

        for (int i = 0; i < numPins; i++){
            LocationPin dummyPin;
            dummyPin = getDummyLocationPin(0.1f * i,0.1f * i,0);
            dummyLocationPinList.add(dummyPin);
        }

        Date dummyTimeStart = new Date();
        Date dummyTimeEnd = new Date(dummyTimeStart.getTime() + 3600);

        Event dummyEvent = new Event(1234, "My fake Event", dummyTimeStart, dummyTimeEnd);
        return dummyEvent;
    }

}
