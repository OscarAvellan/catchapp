package com.ammonia.catchapp.mockImplementations;

import android.location.Location;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by morley on 1/10/17.
 */

public class FakeDB {
    protected ArrayList<DBUser> dbUserList;
    protected ArrayList<DBFriendship> dbFriendshipList;
    protected ArrayList<DBEventMember> dbEventMemberList;
    protected ArrayList<DBEvent> dbEventList;
    protected ArrayList<DBLocationPin> dbLocationPinList;
    protected ArrayList<DBConversation> dbConversationList;
    protected ArrayList<DBConversationMember> dbConversationMemberList;
    protected ArrayList<DBMessage> dbMessageList;

    final int NUM_EVENTS = 5;

    public FakeDB(){
        try {
            this.dbConversationList = fill_dbConversationList();
            this.dbConversationMemberList = fill_dbConversationMemberList();
            this.dbEventList = fill_dbEventList();
            this.dbEventMemberList = fill_dbEventMemberList();
            this.dbFriendshipList = fill_dbFriendshipList();
            this.dbLocationPinList = fill_dbLocationPinList();
            this.dbUserList = fill_dbUserList();
            this.dbMessageList = fill_dbMessageList();
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    private SimpleDateFormat getDateFormat(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        myFormat.setTimeZone(TimeZone.getTimeZone("Australia/Melbourne"));
        return myFormat;
    }

    private Location getLocation(String place){
        HashMap<String,Location> locationMap = new HashMap<String, Location>();

        //Generate some fake locations for our DBUsers
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

        Location grilld = new Location("manual");
        grilld.setLatitude(-37.798873);
        grilld.setLongitude(144.967947);

        Location unimelb = new Location("manual");
        unimelb.setLatitude(-37.7964);
        unimelb.setLongitude(144.9612);

        Location sarawak = new Location("manual");
        sarawak.setLatitude(-37.807741);
        sarawak.setLongitude(144.960027);

        locationMap.put("Baillieu", baillieu);
        locationMap.put("ERC", erc);
        locationMap.put("Alice Hoy", aliceHoy);
        locationMap.put("Old Engineering", oldEng);
        locationMap.put("Kwong Lee", kwongLee);
        locationMap.put("Unimelb", unimelb);
        locationMap.put("Grilld", grilld);
        locationMap.put("Sarawak Kitchen", sarawak);

        return locationMap.get(place);
    }

    private ArrayList<DBUser> fill_dbUserList(){
        ArrayList<DBUser> list= new ArrayList<DBUser>();
        DBUser user_1 = new DBUser(1, "Jonathan",  "Montaluisa", "j.mont@fakemail.com", "hi im jon");
        DBUser user_2 = new DBUser(2, "Dalzy",  "Mendoza", "d.mend@fakemail.com", "hi im dal");
        DBUser user_3 = new DBUser(3, "Josh",  "Morley", "j.morl@fakemail.com", "hi im josh");
        DBUser user_4 = new DBUser(4, "Oscar",  "Avellan", "o.avel@fakemail.com", "hi im osc");
        DBUser user_5 = new DBUser(5, "David",  "Lopez", "d.lopez@fakemail.com", "hi im dave");

        user_1.location = getLocation("Kwong Lee");
        user_1.locationTimestamp = new Date();

        user_2.location = getLocation("Alice Hoy");
        user_2.locationTimestamp = new Date();

        user_3.location = getLocation("ERC");
        user_3.locationTimestamp = new Date();

        user_4.location = getLocation("Old Engineering");
        user_4.locationTimestamp = new Date();

        user_5.location = getLocation("Baillieu");
        user_5.locationTimestamp = new Date();

        list.add(user_1);
        list.add(user_2);
        list.add(user_3);
        list.add(user_4);
        list.add(user_5);
        return list;
    }

    private ArrayList<DBEvent> fill_dbEventList() throws ParseException{
        ArrayList<DBEvent> list = new ArrayList<DBEvent>();

        Date date_1_start = getDateFormat().parse("2017-10-01T10:00:00");
        Date date_1_end = getDateFormat().parse("2017-10-01T18:00:00");

        Date date_2_start = getDateFormat().parse("2017-07-27T09:00:00");
        Date date_2_end = getDateFormat().parse("2017-11-01T21:00:00");

        Date date_3_start = getDateFormat().parse("2017-12-05T18:00:00");
        Date date_3_end = getDateFormat().parse("2017-12-05T20:00:020");

        DBEvent event_1 = new DBEvent(1, "Study Session", date_1_start, date_2_end, 4, getLocation("Kwong Lee"));
        DBEvent event_2 = new DBEvent(2, "IT Project", date_2_start, date_2_end, 4, getLocation("Unimelb"));
        DBEvent event_3 = new DBEvent(3, "Dinner", date_3_start, date_3_end, 1, getLocation("Grilld"));

        list.add(event_1);
        list.add(event_2);
        list.add(event_3);
        return list;
    }

    private ArrayList fill_dbEventMemberList(){
        ArrayList<DBEventMember> list = new ArrayList<DBEventMember>();
        DBEventMember event_member_1 = new DBEventMember(1,1,1);
        DBEventMember event_member_2 = new DBEventMember(2,2,1);
        DBEventMember event_member_3 = new DBEventMember(3,3,1);
        DBEventMember event_member_4 = new DBEventMember(4,4,1);
        DBEventMember event_member_5 = new DBEventMember(5,5,1);
        DBEventMember event_member_6 = new DBEventMember(6,1,2);
        DBEventMember event_member_7 = new DBEventMember(7,2,2);
        DBEventMember event_member_8 = new DBEventMember(8,3,2);
        DBEventMember event_member_9 = new DBEventMember(9,4,2);
        DBEventMember event_member_10 = new DBEventMember(10,5,2);
        DBEventMember event_member_11 = new DBEventMember(11,1,3);
        DBEventMember event_member_12 = new DBEventMember(12,3,3);
        DBEventMember event_member_13 = new DBEventMember(13,4,3);

        event_member_1.adminPrivileges = false;
        event_member_2.adminPrivileges = false;
        event_member_3.adminPrivileges = false;
        event_member_4.adminPrivileges = true;
        event_member_5.adminPrivileges = false;
        event_member_6.adminPrivileges = false;
        event_member_7.adminPrivileges = true;
        event_member_8.adminPrivileges = false;
        event_member_9.adminPrivileges = true;
        event_member_10.adminPrivileges = true;
        event_member_11.adminPrivileges = true;
        event_member_12.adminPrivileges = false;
        event_member_13.adminPrivileges = false;

        list.add(event_member_1);
        list.add(event_member_2);
        list.add(event_member_3);
        list.add(event_member_4);
        list.add(event_member_5);
        list.add(event_member_6);
        list.add(event_member_7);
        list.add(event_member_8);
        list.add(event_member_9);
        list.add(event_member_10);
        list.add(event_member_11);
        list.add(event_member_12);
        list.add(event_member_13);
        return list;
    }

    private ArrayList<DBFriendship> fill_dbFriendshipList() {
        ArrayList<DBFriendship> list = new ArrayList<DBFriendship>();
        DBFriendship friend_1 = new DBFriendship(1,4,1);
        DBFriendship friend_2 = new DBFriendship(2,4,2);
        DBFriendship friend_3 = new DBFriendship(3,4,3);
        DBFriendship friend_4 = new DBFriendship(4,4,5);
        DBFriendship friend_5 = new DBFriendship(5,2,5);
        DBFriendship friend_6 = new DBFriendship(6,1,3);
        DBFriendship friend_7 = new DBFriendship(7,5,1);
        DBFriendship friend_8 = new DBFriendship(8,2,1);

        friend_1.friendshipStatus = MockNetworkManager.FRIENDSHIP_ACTIVE;
        friend_2.friendshipStatus = MockNetworkManager.FRIENDSHIP_ACTIVE;
        friend_3.friendshipStatus = MockNetworkManager.FRIENDSHIP_ACTIVE;
        friend_4.friendshipStatus = MockNetworkManager.FRIENDSHIP_ACTIVE;
        friend_5.friendshipStatus = MockNetworkManager.FRIENDSHIP_ACTIVE;
        friend_6.friendshipStatus = MockNetworkManager.FRIENDSHIP_ACTIVE;
        friend_7.friendshipStatus = MockNetworkManager.FRIENDSHIP_TERMINATED;
        friend_8.friendshipStatus = MockNetworkManager.FRIENDSHIP_PENDING;

        list.add(friend_1);
        list.add(friend_2);
        list.add(friend_3);
        list.add(friend_4);
        list.add(friend_5);
        list.add(friend_6);
        list.add(friend_7);
        list.add(friend_8);

        return list;
    }

    private ArrayList<DBLocationPin> fill_dbLocationPinList(){
        ArrayList<DBLocationPin> list = new ArrayList<DBLocationPin>();

        DBLocationPin pin_1 = new DBLocationPin(1,1,5, getLocation("Sarawak Kitchen"), "eating here");

        list.add(pin_1);
        return list;
    }

    private ArrayList<DBConversation> fill_dbConversationList(){
        ArrayList<DBConversation> list = new ArrayList<DBConversation>();

        DBConversation conversation_1 = new DBConversation(1, false, 0);
        DBConversation conversation_2 = new DBConversation(2, false, 0);
        DBConversation conversation_3 = new DBConversation(3, false, 0);
        DBConversation conversation_4 = new DBConversation(4, false, 0);
        DBConversation conversation_5 = new DBConversation(5, false, 0);
        DBConversation conversation_6 = new DBConversation(6, false, 0);
        DBConversation conversation_7 = new DBConversation(7, true, 1);
        DBConversation conversation_8 = new DBConversation(8, true, 2);
        DBConversation conversation_9 = new DBConversation(9, true, 3);

        list.add(conversation_1);
        list.add(conversation_2);
        list.add(conversation_3);
        list.add(conversation_4);
        list.add(conversation_5);
        list.add(conversation_6);
        list.add(conversation_7);
        list.add(conversation_8);
        list.add(conversation_9);

        return list;
    }

    private ArrayList<DBConversationMember> fill_dbConversationMemberList(){
        ArrayList<DBConversationMember> list = new ArrayList<DBConversationMember>();

        DBConversationMember member_1 = new DBConversationMember(1,1,4);
        DBConversationMember member_2 = new DBConversationMember(2,1,1);
        DBConversationMember member_3 = new DBConversationMember(3,2,4);
        DBConversationMember member_4 = new DBConversationMember(4,2,5);
        DBConversationMember member_5 = new DBConversationMember(5,3,3);
        DBConversationMember member_6 = new DBConversationMember(6,3,1);
        DBConversationMember member_7 = new DBConversationMember(7,4,2);
        DBConversationMember member_8 = new DBConversationMember(8,4,1);
        DBConversationMember member_9 = new DBConversationMember(9,5,2);
        DBConversationMember member_10 = new DBConversationMember(10,5,4);
        DBConversationMember member_11 = new DBConversationMember(11,5,5);
        DBConversationMember member_12 = new DBConversationMember(12,6,5);
        DBConversationMember member_13 = new DBConversationMember(13,6,1);

        list.add(member_1);
        list.add(member_2);
        list.add(member_3);
        list.add(member_4);
        list.add(member_5);
        list.add(member_6);
        list.add(member_7);
        list.add(member_8);
        list.add(member_9);
        list.add(member_10);
        list.add(member_11);
        list.add(member_12);
        list.add(member_13);

        return list;
    }

    private ArrayList<DBMessage> fill_dbMessageList() throws ParseException{
        ArrayList<DBMessage> list = new ArrayList<DBMessage>();

        Date date_1 = getDateFormat().parse("2017-10-01T10:00:00");
        Date date_2 = getDateFormat().parse("2017-10-01T10:21:10");
        Date date_3 = getDateFormat().parse("2017-10-01T10:23:05");
        Date date_4 = getDateFormat().parse("2017-09-11T15:10:10");
        Date date_5 = getDateFormat().parse("2017-09-11T15:11:27");
        Date date_6 = getDateFormat().parse("2017-09-11T15:24:06");
        Date date_7 = getDateFormat().parse("2017-10-02T10:00:00");
        Date date_8 = getDateFormat().parse("2017-10-02T11:25:00");
        Date date_9 = getDateFormat().parse("2017-10-02T11:55:30");
        Date date_10 = getDateFormat().parse("2017-10-03T09:10:00");
        Date date_11 = getDateFormat().parse("2017-10-03T09:10:50");
        Date date_12 = getDateFormat().parse("2017-10-03T10:15:00");
        Date date_13 = getDateFormat().parse("2017-08-01T05:30:00");
        Date date_14 = getDateFormat().parse("2017-08-01T05:45:11");
        Date date_15 = getDateFormat().parse("2017-08-01T05:59:10");
        Date date_16 = getDateFormat().parse("2017-10-01T10:00:00");
        Date date_17 = getDateFormat().parse("2017-10-01T11:30:00");
        Date date_18 = getDateFormat().parse("2017-10-01T12:15:00");
        Date date_19 = getDateFormat().parse("2017-10-01T10:00:00");
        Date date_20 = getDateFormat().parse("2017-10-01T10:17:20");
        Date date_21 = getDateFormat().parse("2017-10-01T10:19:27");
        Date date_22 = getDateFormat().parse("2017-10-01T10:23:03");
        Date date_23 = getDateFormat().parse("2017-10-01T10:00:00");
        Date date_24 = getDateFormat().parse("2017-10-02T10:00:00");


        DBMessage message_1 = new DBMessage(1,4,1,"Hey, what's up?", date_1);
        DBMessage message_2 = new DBMessage(2,1,1,"Nothing much, you?", date_2);
        DBMessage message_3 = new DBMessage(3,4,1,"nothing either.", date_3);
        DBMessage message_4 = new DBMessage(4,5,2,"Hey, what's up?", date_4);
        DBMessage message_5 = new DBMessage(5,4,2,"Nothing much, you?", date_5);
        DBMessage message_6 = new DBMessage(6,5,2,"nothing either.", date_6);
        DBMessage message_7 = new DBMessage(7,1,3,"Hey, what's up?", date_7);
        DBMessage message_8 = new DBMessage(8,3,3,"Nothing much, you?", date_8);
        DBMessage message_9 = new DBMessage(9,1,3,"nothing either", date_9);
        DBMessage message_10 = new DBMessage(10,2,4,"Hey, what's up?", date_10);
        DBMessage message_11 = new DBMessage(11,1,4,"Nothing much, you?", date_11);
        DBMessage message_12 = new DBMessage(12,2,4,"nothing either", date_12);
        DBMessage message_13 = new DBMessage(13,5,5,"Hey, what's up?", date_13);
        DBMessage message_14 = new DBMessage(14,2,5,"the ceiling", date_14);
        DBMessage message_15 = new DBMessage(15,4,5,"roflmao", date_15);
        DBMessage message_16 = new DBMessage(16,5,6,"Hey, what's up?", date_16);
        DBMessage message_17 = new DBMessage(17,1,6,"Nothing much, you?", date_17);
        DBMessage message_18 = new DBMessage(18,5,6,"nothing either.", date_18);
        DBMessage message_19 = new DBMessage(19,5,7,"Where are we meeting?", date_19);
        DBMessage message_20 = new DBMessage(20,5,7,"at kwong lee", date_20);
        DBMessage message_21 = new DBMessage(21,3,7,"thanks!", date_21);
        DBMessage message_22 = new DBMessage(22,4,7,"cool", date_22);
        DBMessage message_23 = new DBMessage(23,2,8,"Luca is so cool.", date_23);
        DBMessage message_24 = new DBMessage(24,1,8,"I know right? the best!", date_24);

        list.add(message_1);
        list.add(message_2);
        list.add(message_3);
        list.add(message_4);
        list.add(message_5);
        list.add(message_6);
        list.add(message_7);
        list.add(message_8);
        list.add(message_9);
        list.add(message_10);
        list.add(message_11);
        list.add(message_12);
        list.add(message_13);
        list.add(message_14);
        list.add(message_15);
        list.add(message_16);
        list.add(message_17);
        list.add(message_18);
        list.add(message_19);
        list.add(message_20);
        list.add(message_21);
        list.add(message_22);
        list.add(message_23);
        list.add(message_24);

        return list;
    }
}

class DBUser{
    public int userID;
    public String firstName;
    public String lastName;
    public String emailAddress;
    public String accountBio;
    public Location location;
    public Date locationTimestamp;

    DBUser(int newUserID, String newFirstName, String newLastName,
           String newEmailAddress, String newAccountBio){
        this.userID = newUserID;
        this.firstName = newFirstName;
        this.lastName = newLastName;
        this.emailAddress = newEmailAddress;
        this.accountBio = newAccountBio;
        locationTimestamp = null;
    }
}

class DBFriendship{
    public int friendshipID;
    public int userOneID;
    public int userTwoID;
    public int friendshipStatus = MockNetworkManager.FRIENDSHIP_PENDING;

    DBFriendship(int newFriendshipID, int newUserOneID, int newUserTwoID){
        this.friendshipID = newFriendshipID;
        this.userOneID = newUserOneID;
        this.userTwoID = newUserTwoID;
    }
}

class DBEventMember{
    public int eventMemberID;
    public int userID;
    public int eventID;
    public boolean adminPrivileges = false;

    DBEventMember(int newEventMemberID, int newUserID, int newEventID){
        this.eventMemberID = newEventMemberID;
        this.userID = newUserID;
        this.eventID = newEventID;
    }
}

class DBEvent{
    public int eventID;
    public String eventName;
    public Date startTime;
    public Date endTime;
    public int creatorID;
    public Location eventLocation;

    DBEvent(int newEventID, String newEventName, Date newStartTime, Date newEndTime, int newCreatorID,
            Location newEventLocation){
        this.eventID = newEventID;
        this.eventName = newEventName;
        this.startTime = newStartTime;
        this.endTime = newEndTime;
        this.creatorID = newCreatorID;
        this.eventLocation = newEventLocation;
    }
}

class DBLocationPin{
    public int locationPinID;
    public int eventID;
    public int creatorID;
    public Location locationPinLocation;
    public String locationPinComment;

    DBLocationPin(int newLocationPinID, int newEventID, int newCreatorID, Location newLocationPin, String newLocationPinComment){
        this.locationPinID = newLocationPinID;
        this.eventID = newEventID;
        this.creatorID = newCreatorID;
        this.locationPinLocation = newLocationPin;
        this.locationPinComment = newLocationPinComment;
    }
}

class DBConversation{
    public int conversationID;
    public boolean hasAssociatedEvent;
    public int eventID;

    DBConversation(int newConversationID, boolean newHasAssociatedEvent, int newEventID){
        this.conversationID = newConversationID;
        this.hasAssociatedEvent = newHasAssociatedEvent;
        this.eventID = newEventID;
    }
}

class DBConversationMember{
    public int conversationMemberID;
    public int conversationID;
    public int userID;

    DBConversationMember(int newConversationMemberID, int newConversationID, int newUserID){
        this.conversationMemberID = newConversationMemberID;
        this.conversationID = newConversationID;
        this.userID = newUserID;
    }
}

class DBMessage{
    public int messageID;
    public int senderID;
    public int conversationID;
    public String content;
    public Date timestamp;


    DBMessage(int newMessageID, int newSenderID, int newConversationID, String newContent,
              Date newTimestamp){
        messageID = newMessageID;
        senderID = newSenderID;
        conversationID = newConversationID;
        content = newContent;
        timestamp = newTimestamp;
    }
}