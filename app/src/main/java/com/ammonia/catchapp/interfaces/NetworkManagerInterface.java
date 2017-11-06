package com.ammonia.catchapp.interfaces;

import android.location.Location;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by morley on 13/09/17.
 */

public interface NetworkManagerInterface {

    /*----------------------------------- METHODS IMPLEMENTED ----------------------------------- */

                                    /********** GET **********/
    public UserProfile getUserByEmail(String emailAddress);                             // WORKING
    public ArrayList<UserProfile> searchUser(String firstname);                         // WORKING
    public ArrayList<UserProfile> getFriendsByUser(UserProfile user);                   // WORKING
    public Conversation getConversationByMessage(Message message);                      // WORKING
    public String getAccountBioByUser(UserProfile user);                                // WORKING
    public Location getUserLocation(UserProfile user);                                  // WORKING
    public String getEmailAddressByUser(UserProfile user);                              // WORKING
    public Location getLocationByEvent(Event event);                                    // WORKING
    public ArrayList<UserProfile> getAdminListByEvent(Event event);                     // WORKING
    public ArrayList<UserProfile> getMemberListByEvent(Event event);                    // WORKING
    public ArrayList<LocationPin> getPinsByEvent(Event event);                          // WORKING
    public ArrayList<Message> getMessagesByConversation(Conversation conversation);     // WORKING
    public Event getEventByConversation(Conversation conversation);                     // WORKING
    public UserProfile getSenderByMessage(Message message);                             // WORKING
    public UserProfile getCreatorByLocationPin(LocationPin pin);                        // WORKING
    public ArrayList<Event> getSharedEvents(UserProfile user1, UserProfile user2);      // WORKING
    public ArrayList<Conversation> getConversationByUser(UserProfile user);             // WORKING
    public ArrayList<UserProfile> getUsersByConversation(Conversation convo);           // WORKING
    public ArrayList<Event> getEventsByUser(UserProfile user);                          // WORKING
    public UserProfile getCreatorByEvent(Event event);                                  // WORKING
    public ArrayList<UserProfile> getFriendInvitesByUser(UserProfile user);             // WORKING (Not in tests)
    public ArrayList<Event> getEventInvitesByUser(UserProfile user);                    // WORKING (Not in tests)

                             /********** POST **********/
    public boolean sendFriendRequest(UserProfile currentUser, UserProfile toAdd);           // WORKING
    public UserProfile createUser(String firstName, String lastName, String emailAddress);  // NOT WORKING
    public boolean createConversation(ArrayList<UserProfile> users);                        // WORKING (Need to get details of convo)
    public boolean sendEventInvite(Event event, UserProfile sender, UserProfile receiever); // WORKING
    public Event createEvent(String eventName, Date startTime, Date endTime,                // WORKING
                             UserProfile creator, Location eventLocation
                            , ArrayList<UserProfile> members);                              // WORKING
    public Message sendMessageToConversation(Message message,Conversation conversation,
                                             UserProfile sender);                           // WORKING
    public LocationPin createLocationPin(Location location, Event event, UserProfile user,
                                     String comment);                                       // WORKING

                            /********** PATCH **********/
    public boolean acceptFriendRequest(UserProfile friend);                              // WORKING
    public boolean declineFriendRequest(UserProfile friend);                             // WORKING
    public boolean deleteEvent(Event event);                                             // WORKING
    public boolean acceptEventInvite(Event event);                                       // WORKING
    public boolean rejectEventInvite(Event event);                                       // WORKING
    public boolean updateUserLocation(UserProfile user, Location location);              // WORKING
    public boolean updateUserDetails(UserProfile user, String firstName, String lastName,// WORKING
                                     String emailAddress, String accountBio);
    public boolean updateUserFirebaseToken(UserProfile user, String firebaseToken);

    /*------------------------------- E-N-D METHODS IMPLEMENTED --------------------------------- */


    /*------------------------------ METHODS PARTIALLY IMPLEMENTED ------------------------------ */
    // NOTE: Not implemented server side
    public String getConversationName(Conversation convo);
    /*------------------------------------------- E N D ------------------------------------------*/

    //User authentification
    public enum AuthenticationResponseCode{LOGIN_APPROVED, WRONG_PASSWORD, NO_ACCOUNT}
    public int authenticateUser(String username, String password);

    public boolean addPinToEvent(Location loc, Event event,UserProfile creator, String message);

    public UserProfile sendIdTokenToServer(String idToken);
}
