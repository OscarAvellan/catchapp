package com.ammonia.catchapp.mockImplementations;

import android.location.Location;
import android.util.Log;

import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.AuthUser;
import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.LocationPin;
import com.ammonia.catchapp.structures.Message;
import com.ammonia.catchapp.structures.UserProfile;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by morley on 1/10/17.
 */

/*This mock implementation of the NetworkManagerInterface works as a "fake" database
* saving changes to it locally for the lifetime of the instance*/

public class MockNetworkManager implements NetworkManagerInterface {
    protected static final int FRIENDSHIP_PENDING = 0;
    protected static final int FRIENDSHIP_ACTIVE = 1;
    protected static final int FRIENDSHIP_TERMINATED = 2;

    protected static final int STANDARD_MESSAGE = 0;
    protected static final int FRIEND_INVITE = 1;
    protected static final int EVENT_INVITE = 2;

    protected static final int UNANSWERED = 0;
    protected static final int ACCEPTED = 1;
    protected static final int REJECTED = 2;

    private static final int DB_INTIAL_CONFIG = 0;

    private Factory factory;
    private FakeDB database;

    public MockNetworkManager(){
        factory = Factory.getFactory();
        database = factory.getDatabase();
    }


    @Override
    public UserProfile createUser(String firstName, String lastName, String emailAddress) {
        int numUsers = database.dbUserList.size();
        DBUser newUser = new DBUser(numUsers,firstName,lastName,emailAddress,null);
        database.dbUserList.add(newUser);
        UserProfile newUserprofile = new UserProfile(newUser.userID, newUser.firstName,
                newUser.lastName);
        return newUserprofile;
    }

    @Override
    public Event createEvent(String eventName, Date startTime, Date endTime,
                             UserProfile creator, Location eventLocation, ArrayList<UserProfile> members) {
        int numEvents = database.dbEventList.size();
        DBEvent newEvent = new DBEvent(numEvents+1, eventName, startTime,
                endTime, creator.getUserID(), eventLocation);
        database.dbEventList.add(newEvent);
        int numEventMembers = database.dbEventMemberList.size();

        Log.i("creator", Integer.toString(creator.getUserID()));
        DBEventMember newEventMember = new DBEventMember(numEventMembers+1, creator.getUserID(),
                newEvent.eventID);
        newEventMember.adminPrivileges = true;
        database.dbEventMemberList.add(newEventMember);
        Event newEventObject = new Event(newEvent.eventID, newEvent.eventName, newEvent.startTime,
                newEvent.endTime);
        Log.i("db", Integer.toString(database.dbEventList.size()));
        Log.i("db", Integer.toString(database.dbEventMemberList.size()));
        return newEventObject;
    }

    @Override
    public boolean updateUserDetails(UserProfile user, String firstName, String lastName,
                                     String emailAddress, String accountBio) {
        return false;
    }

    @Override
    public boolean acceptFriendRequest(UserProfile friend) {
        return false;
    }

    @Override
    public boolean deleteEvent(Event event) {
        return false;
    }

    @Override
    public ArrayList<Event> getEventsByUser(UserProfile user) {
        int userID = user.getUserID();
        Log.i("ID", Integer.toString(userID));
        ArrayList<Event> returnList = new ArrayList<Event>();
        Log.i("getEventsbyUser", Integer.toString(database.dbEventMemberList.size()));
        for(int i = 0; i < database.dbEventMemberList.size();i++){
            DBEventMember currentEventMember = database.dbEventMemberList.get(i);
            if(currentEventMember.userID == userID){
                int matchEventID = currentEventMember.eventID;

                DBEvent matchEvent = null;
                for (int j = 0; j < database.dbEventList.size();j++){
                    if(matchEventID == database.dbEventList.get(j).eventID){
                        matchEvent = database.dbEventList.get(j);
                        break;
                    }
                }

                if (matchEvent != null) {
                    Event newEvent = new Event(matchEventID, matchEvent.eventName,
                            matchEvent.startTime, matchEvent.endTime);
                    returnList.add(newEvent);
                }
            }
        }
        return returnList;
    }

    @Override
    public ArrayList<Conversation> getConversationByUser(UserProfile user) {
        int userID = user.getUserID();
        ArrayList<Conversation> returnList = new ArrayList<Conversation>();
        for(int i = 0; i < database.dbConversationMemberList.size();i++){
            DBConversationMember currentConversationMember = database.dbConversationMemberList.get(i);
            if(currentConversationMember.userID == userID){
                int matchConversationID = currentConversationMember.conversationID;

                DBConversation matchConversation = null;
                for (int j = 0; j < database.dbConversationList.size();j++){
                    if(matchConversationID == database.dbConversationList.get(j).conversationID){
                        matchConversation = database.dbConversationList.get(j);
                        break;
                    }
                }

                if (matchConversation != null){
                    Conversation newConversation = new Conversation(matchConversationID,
                            matchConversation.hasAssociatedEvent);
                    returnList.add(newConversation);
                }
            }
        }
        return returnList;
    }

    /*To be implemented*/
    @Override
    public boolean createConversation(ArrayList<UserProfile> users) {
        return false;
    }

    @Override
    public String getConversationName(Conversation convo) {
        return null;
    }

    @Override
    public ArrayList<UserProfile> searchUser(String searchString) {
        return null;
    }

    @Override
    public boolean sendFriendRequest(UserProfile currentUser, UserProfile toAdd) {
        return false;
    }

    @Override
    public ArrayList<Event> getSharedEvents(UserProfile user1, UserProfile user2) {
        return null;
    }

    @Override
    public UserProfile getUserByEmail(String emailAddress) {
        return null;
    }

    @Override
    public ArrayList<UserProfile> getFriendsByUser(UserProfile user) {
        int userID = user.getUserID();
        ArrayList<UserProfile> returnList = new ArrayList<UserProfile>();
        for(int i = 0; i < database.dbFriendshipList.size();i++){
            DBFriendship currentFriendship = database.dbFriendshipList.get(i);
            if((currentFriendship.userOneID == userID || currentFriendship.userTwoID == userID)
                    && currentFriendship.friendshipStatus == FRIENDSHIP_ACTIVE) {
                int matchFriendID;
                if(userID == currentFriendship.userOneID){
                    matchFriendID = currentFriendship.userTwoID;
                }else{
                    matchFriendID = currentFriendship.userOneID;
                }
                DBUser matchFriend = null;
                for (int j = 0; j < database.dbUserList.size();j++){
                    if(matchFriendID == database.dbUserList.get(j).userID){
                        matchFriend = database.dbUserList.get(j);
                        break;
                    }
                }
                if(matchFriend != null) {
                    UserProfile newFriend = new UserProfile(matchFriend.userID, matchFriend.firstName,
                            matchFriend.lastName);
                    returnList.add(newFriend);
                }
            }
        }
        return returnList;
    }

    /*To be implemented*/

    public void friendInvite(UserProfile currentUser, UserProfile toAdd) {

    }

    /*To be implemented*/
    public void eventInvite(Event event, UserProfile sender, UserProfile receiver) {

    }

    public boolean declineFriendRequest(UserProfile friend) {
        return false;
    }

    @Override
    public int authenticateUser(String username, String password) {
        return 0;
    }

    @Override
    public boolean addPinToEvent(Location loc, Event event, UserProfile creator, String message) {
        DBLocationPin newPin = new DBLocationPin(database.dbLocationPinList.size() + 1,
                event.getEventID(),
                creator.getUserID(),
                loc,message);
        database.dbLocationPinList.add(newPin);
        return true;
    }


    @Override
    public String getEmailAddressByUser(UserProfile user) {
        int userID = user.getUserID();
        for(int i = 0; i < database.dbUserList.size();i++){
            if(userID == database.dbUserList.get(i).userID){
                return database.dbUserList.get(i).emailAddress;
            }
        }
        return null;
    }

    @Override
    public String getAccountBioByUser(UserProfile user) {
        int userID = user.getUserID();
        for(int i = 0; i < database.dbUserList.size();i++){
            if(userID == database.dbUserList.get(i).userID){
                return database.dbUserList.get(i).accountBio;
            }
        }
        return null;
    }

    @Override
    public boolean updateUserLocation(UserProfile user, Location location) {
        int userID = user.getUserID();
        for(int i = 0; i < database.dbUserList.size();i++){
            if(userID == database.dbUserList.get(i).userID){
                DBUser matchUser = database.dbUserList.get(i);
                matchUser.location = location;
                matchUser.locationTimestamp = new Date();
            }
        }
        return false;
    }

    @Override
    public Location getUserLocation(UserProfile user) {
        int userID = user.getUserID();
        for(int i = 0; i < database.dbUserList.size();i++){
            if(userID == database.dbUserList.get(i).userID){
                DBUser matchUser = database.dbUserList.get(i);
                if(matchUser.locationTimestamp != null){
                    return matchUser.location;
                }
            }
        }
        return null;
    }

    @Override
    public UserProfile getCreatorByEvent(Event event) {
        int eventID = event.getEventID();
        for(int i = 0; i < database.dbEventList.size();i++){
            if(eventID == database.dbEventList.get(i).eventID){
                DBEvent matchEvent = database.dbEventList.get(i);
                int creatorID = matchEvent.creatorID;
                DBUser creator = database.dbUserList.get(creatorID);
                UserProfile newUserProfile = new UserProfile(creatorID, creator.firstName,
                        creator.lastName);
                return newUserProfile;
            }
        }
        return null;
    }

    @Override
    public ArrayList<UserProfile> getFriendInvitesByUser(UserProfile user) {
        return null;
    }

    @Override
    public ArrayList<Event> getEventInvitesByUser(UserProfile user) {
        return null;
    }

    @Override
    public ArrayList<UserProfile> getAdminListByEvent(Event event) {
        int eventID = event.getEventID();
        ArrayList<UserProfile> returnList = new ArrayList<UserProfile>();
        for(int i = 0; i < database.dbEventMemberList.size();i++){
            DBEventMember currentEventMember = database.dbEventMemberList.get(i);
            if(eventID == currentEventMember.eventID && (currentEventMember.adminPrivileges)){
                int userID = currentEventMember.userID;
                DBUser user = database.dbUserList.get(userID);
                UserProfile newUserProfile = new UserProfile(userID,user.firstName,
                        user.lastName);

            }
        }
        return returnList;
    }

    @Override
    public ArrayList<UserProfile> getMemberListByEvent(Event event) {
        int eventID = event.getEventID();
        ArrayList<UserProfile> returnList = new ArrayList<UserProfile>();
        for(int i = 0; i < database.dbEventMemberList.size();i++){
            DBEventMember currentEventMember = database.dbEventMemberList.get(i);
            if(eventID == currentEventMember.eventID){
                int userID = currentEventMember.userID;
                DBUser user = null;

                for (int j = 0; j < database.dbUserList.size();j++){
                    if(userID == database.dbUserList.get(j).userID){
                        user = database.dbUserList.get(j);
                        break;
                    }
                }

                if (user!= null) {
                    UserProfile newUserProfile = new UserProfile(userID, user.firstName,
                            user.lastName);
                    returnList.add(newUserProfile);
                }
            }
        }
        return returnList;
    }

    @Override
    public ArrayList<LocationPin> getPinsByEvent(Event event) {
        int eventID = event.getEventID();
        ArrayList<LocationPin> returnList = new ArrayList<LocationPin>();
        for(int i = 0; i < database.dbLocationPinList.size(); i++){
            DBLocationPin currentLocationPin = database.dbLocationPinList.get(i);
            Log.d("MockNetwork", String.valueOf(currentLocationPin.locationPinID));
            Log.d("MockNetwork", String.valueOf(currentLocationPin.eventID));
            Log.d("MockNetwork", String.valueOf(currentLocationPin.locationPinComment));
            if(eventID == currentLocationPin.eventID){
                int locationPinID = currentLocationPin.locationPinID;
                LocationPin newLocationPin = new LocationPin(currentLocationPin.locationPinID,
                        currentLocationPin.locationPinLocation.getLatitude(),
                        currentLocationPin.locationPinLocation.getLongitude(),
                        currentLocationPin.locationPinComment);
                returnList.add(newLocationPin);
            }
        }
        return  returnList;
    }

    @Override
    public Location getLocationByEvent(Event event) {
        int eventID = event.getEventID();
        for(int i = 0; i < database.dbEventList.size();i++){
            DBEvent currentEvent = database.dbEventList.get(i);
            if(currentEvent.eventID == eventID){
                return currentEvent.eventLocation;
            }
        }
        return null;
    }

    @Override
    public boolean sendEventInvite(Event event, UserProfile sender, UserProfile receiever) {
        return false;
    }

    @Override
    public boolean acceptEventInvite(Event event) {
        return false;
    }

    @Override
    public boolean rejectEventInvite(Event event) {
        return false;
    }

    @Override
    public ArrayList<UserProfile> getUsersByConversation(Conversation conversation) {
        int convID = conversation.getConversationID();
        ArrayList<UserProfile> returnList = new ArrayList<UserProfile>();
        for(int i = 0; i < database.dbConversationMemberList.size();i++){
            DBConversationMember currentConvMember = database.dbConversationMemberList.get(i);
            if(currentConvMember.conversationID == convID){
                int userID = currentConvMember.userID;

                DBUser user = null;

                for (int j = 0; j < database.dbUserList.size();j++){
                    if(userID == database.dbUserList.get(j).userID){
                        user = database.dbUserList.get(j);
                        break;
                    }
                }

                if (user != null) {
                    UserProfile newUserProfile = new UserProfile(userID, user.firstName, user.lastName);
                    returnList.add(newUserProfile);
                }
            }

        }
        return returnList;
    }

    @Override
    public ArrayList<Message> getMessagesByConversation(Conversation conversation) {
        int convID = conversation.getConversationID();
        ArrayList<Message> returnList = new ArrayList<Message>();
        for(int i = 0; i < database.dbMessageList.size();i++){
            DBMessage currentMessage = database.dbMessageList.get(i);
            if(currentMessage.conversationID == convID){

                int messageID = currentMessage.messageID;

                DBMessage message = null;
                for (int j = 0; j < database.dbMessageList.size();j++){
                    if(messageID == database.dbMessageList.get(j).messageID){
                        message = database.dbMessageList.get(j);
                        break;
                    }
                }

                if (message != null) {
                    Message newMessage = new Message(currentMessage.messageID,
                            currentMessage.content, currentMessage.timestamp);
                    returnList.add(newMessage);
                }
            }
        }
        return returnList;
    }

    @Override
    public Event getEventByConversation(Conversation conversation) {
        if(conversation.hasAssociatedEvent() == false){
            return null;
        }else{
            int convID = conversation.getConversationID();
            for(int i = 0; i < database.dbConversationList.size();i++){
                DBConversation currentConv = database.dbConversationList.get(i);
                if(currentConv.hasAssociatedEvent){
                    int eventID = currentConv.eventID;
                    DBEvent event = database.dbEventList.get(eventID);
                    Event newEvent = new Event(eventID, event.eventName, event.startTime,
                            event.endTime);
                    return newEvent;
                }
            }
        }
        return null;
    }

    @Override
    public Message sendMessageToConversation(Message message, Conversation conversation, UserProfile sender) {
        int numMessages = database.dbMessageList.size();
        DBMessage newMessage = new DBMessage(numMessages,sender.getUserID(),
                conversation.getConversationID(), message.getMessageBody(), message.getTimeStamp());
        database.dbMessageList.add(newMessage);

        return null;
    }

    @Override
    public LocationPin createLocationPin(Location location, Event event, UserProfile user, String comment) {
        return null;
    }

    @Override
    public UserProfile getCreatorByLocationPin(LocationPin pin) {
        int pinID = pin.getLocationPinID();
        for(int i = 0;i < database.dbLocationPinList.size();i++){
            DBLocationPin currentPin = database.dbLocationPinList.get(i);
            if(currentPin.locationPinID == pinID){
                int creatorID = currentPin.creatorID;
                DBUser creator = database.dbUserList.get(creatorID);
                UserProfile newUserProfile = new UserProfile(creatorID, creator.firstName,
                        creator.lastName);
                return newUserProfile;
            }
        }
        return null;
    }

    @Override
    public UserProfile getSenderByMessage(Message message) {
        int messageID = message.getMessageID();
        for(int i = 0; i < database.dbMessageList.size();i++){
            DBMessage currentMsg = database.dbMessageList.get(i);
            if(currentMsg.messageID == messageID){
                int senderID = currentMsg.senderID;

                DBUser sender = null;

                for (int j = 0; j < database.dbUserList.size();j++){
                    if(senderID == database.dbUserList.get(j).userID){
                        sender = database.dbUserList.get(j);
                        break;
                    }
                }

                if(sender != null) {
                    UserProfile newUserProfile = new UserProfile(senderID, sender.firstName,
                            sender.lastName);
                    return newUserProfile;
                }
            }
        }
        return null;
    }

    @Override
    public Conversation getConversationByMessage(Message message) {
        int messageID = message.getMessageID();
        for(int i = 0; i < database.dbMessageList.size();i++){
            DBMessage currentMsg = database.dbMessageList.get(i);
            if(currentMsg.messageID == messageID){
                int convID = currentMsg.conversationID;
                DBConversation conversation = database.dbConversationList.get(convID);
                Conversation newConversation = new Conversation(convID,
                        conversation.hasAssociatedEvent);
            }
        }
        return null;
    }

    @Override
    public UserProfile sendIdTokenToServer(String idToken){
//        return new AuthUser(1, "testAuthUser", "testAuthToken");
          return new UserProfile(4, "Oscar", "Avellan");
    }

    @Override
    public boolean updateUserFirebaseToken(UserProfile user, String firebaseToken){
        return true;
    }

}

