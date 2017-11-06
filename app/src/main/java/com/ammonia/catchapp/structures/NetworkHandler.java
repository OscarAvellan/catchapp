package com.ammonia.catchapp.structures;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.google.android.gms.auth.api.Auth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Oscar on 8/09/2017.
 */

public class NetworkHandler implements NetworkManagerInterface{

    private final String ID_USER = "iduser", SIGN_UP_DATE = "signupdate",
        FIRSTNAME = "firstname", LASTNAME = "lastname", EMAIL = "profileemail", LAT = "latitude",
        LON = "longitude", ID_FRIENDSHIP = "idfriendship", ID_FRIEND = "idfriend", ID_EVENT = "idevent",
        ID_EVENT_MEMBERSHIP = "ideventmembership", EVENT_NAME = "eventname", STARTS_AT = "startsat",
        ENDS_IN = "endsin", ID_CONVERSATION = "idconversation", TYPE = "type", NAME = "name",
        BIO = "bio", ID_LOCATION_PIN = "idlocationpin", COMMENT = "comment", ID_CREATOR = "idcreator",
        EVENT_ADMIN = "eventadmin", SENT_EVENT_INVITE = "senteventinvite", MEMBERS = "members",
        FIREBASE_TOKEN = "firebasetoken", STATUS = "status", ID_SENDER = "idsender", MESSAGE = "message",
        ID_MESSAGE = "idmessage", TIMESTAMP = "timestamp", ID_AUTHUSER = "id", USERNAME = "name",
        TOKEN = "token", LOGIN = "login";

    private final String USERS = "profiles", FRIENDSHIPS = "friendships", EVENTS = "events",
        EVENT_MEMBERSHIPS = "eventmemberships", CONVERSATIONS = "conversations", MESSAGES = "messages",
        P_CONVERSATIONS = "privateconversations", E_CONVERSATIONS = "eventconversations",
        LOCATION_PINS = "locationpins";

    private final String scheme = "http";
    private final String serverIP = "115.146.92.110"; // Dalzy's improved Server with auth
    //private final String serverIP = "115.146.92.203"; // Dalzy's improved Server
    private int responseCode;
    private ArrayList<JSONObject> responsePayload;
    private JSONObject json_object;

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<UserProfile> searchUser(String searchString) {

        Uri.Builder builder = setSchemeAndAuthority();
        ArrayList<UserProfile> listSearch = new ArrayList<>();

        try {
            builder.appendPath(USERS)
                .appendQueryParameter(FIRSTNAME,searchString);

            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(),"single");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject json_object:responsePayload){
                    if(json_object.getString("Error").equals("Not Found"))
                        return null;

                    listSearch.add(new UserProfile(json_object.getInt(ID_USER),
                            json_object.getString(FIRSTNAME),
                            json_object.getString(LASTNAME)) );
                }

                for(UserProfile user:listSearch)
                    Log.i("searchUser: ",user.toString());

                return listSearch;

            }
            else
                Log.e("searchUser", "Request Failed, response code: " + responseCode);
                return null;
            } catch (JSONException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public UserProfile getUserByEmail(String emailAddress) {
        int idUser;
        String fname, lname;

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(USERS)
                .appendQueryParameter(EMAIL,emailAddress);

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(),"single");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                Log.i("Response Code:", Integer.toString(responseCode));
                Log.i("Size ArrayList:",Integer.toString(responsePayload.size()));

                // Get first and only Element of ArrayList
                json_object = responsePayload.get(0);

                Log.i("json_object",json_object.toString());

                if(json_object.getString("Error").equals("None")){
                    idUser = json_object.getInt(ID_USER);
                    fname = json_object.getString(FIRSTNAME);
                    lname = json_object.getString(LASTNAME);

                    Log.i("User",new UserProfile(idUser,fname, lname).toString());
                    return new UserProfile(idUser,fname, lname);
                }
                else
                    Log.i("User","ERROR");
                    return null;

            }
            else
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }

        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public UserProfile createUser(String firstName, String lastName, String emailAddress){

        try {
            JSONObject jobject = new JSONObject().put(FIRSTNAME,firstName)
                    .put(LASTNAME,lastName)
                    .put(EMAIL,emailAddress);

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(USERS+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                UserProfile user = new UserProfile(json_object.getInt(ID_USER),
                        json_object.getString(FIRSTNAME),
                        json_object.getString(LASTNAME),
                        new SimpleDateFormat("yyyy-MM-dd").parse(json_object.getString(SIGN_UP_DATE)),
                        json_object.getDouble(LAT),
                        json_object.getDouble(LON));

                Factory.getFactory().setUser(user);
                Log.i("user",user.toString());

                return user;
            }

            Log.d("createUser", "Request Failed, response code: " + responseCode);
            return null;

        } catch (JSONException | ParseException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;

    }

     /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<UserProfile> getFriendsByUser(UserProfile user) {

        ArrayList<UserProfile> listFriends = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(FRIENDSHIPS).appendPath("get_friends")
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){

                    JSONObject sender = jsonObject.getJSONObject("sentfriendrequest");
                    JSONObject receiver = jsonObject.getJSONObject("receivedfriendrequest");

                    JSONObject friend;

                    if(sender.getInt(ID_USER) == user.getUserID()){
                        friend = receiver;
                    }
                    else{
                        friend = sender;
                    }

                    listFriends.add(new UserProfile(friend.getInt(ID_USER),
                            jsonObject.getInt(ID_FRIENDSHIP),
                            friend.getString(FIRSTNAME),
                            friend.getString(LASTNAME),
                            jsonObject.getString("status")));
                }

                // For debugging purposes
                for(UserProfile friend:listFriends)
                    Log.i("getFriendsByUser",friend.toString());

                return listFriends;
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<Event> getEventsByUser(UserProfile user) {
        ArrayList<Event> listEvents = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENT_MEMBERSHIPS)
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()))
                .appendQueryParameter(STATUS,"J");
        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    JSONObject jsonEvent = jsonObject.getJSONObject("idevent");
                    listEvents.add(new Event(jsonEvent.getInt(ID_EVENT),
                            jsonObject.getInt(ID_EVENT_MEMBERSHIP),
                            jsonEvent.getString(EVENT_NAME),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    .parse(jsonEvent.getString(STARTS_AT)),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    .parse(jsonEvent.getString(ENDS_IN))) );
                }

                if(listEvents.isEmpty()){
                    return null;
                }
                else{
                    for (Event event : listEvents){
                        Log.i("getEventsByUser: ",event.toString());
                    }
                    return listEvents;
                }
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public Location getUserLocation(UserProfile user) {
        Location location = new Location("manual");

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(USERS).appendPath("get_location")
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()));

        try {

            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    location.setLatitude(jsonObject.getDouble(LAT));
                    location.setLongitude(jsonObject.getDouble(LON));
                }

                Log.i("getUserLocation","lat: "+Double.toString(location.getLatitude()) +"   long: "+
                        Double.toString(location.getLongitude()));

                return location;
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<Conversation> getConversationByUser(UserProfile user) {

        ArrayList<Conversation> listConversations = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(CONVERSATIONS).appendPath("get_conversations")
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()));
        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    listConversations.add(new Conversation(jsonObject.getInt(ID_CONVERSATION),
                            jsonObject.getString(TYPE),
                            jsonObject.getString(NAME)));
                }

                if(listConversations.isEmpty()){
                    return null;
                }
                else{
                    // For debugging purposes
                    for(Conversation convo:listConversations)
                        Log.i("getConversationByUser",convo.toString());

                    return listConversations;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean updateUserDetails(UserProfile user, String firstName, String lastName,
                                     String emailAddress, String accountBio) {
        try {
            JSONObject jobject = new JSONObject()
                    .put(FIRSTNAME,firstName)
                    .put(LASTNAME,lastName)
                    .put(EMAIL,emailAddress)
                    .put(BIO,accountBio);

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(USERS).appendPath(Integer.toString(user.getUserID())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("updateUserLocation", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public String getEmailAddressByUser(UserProfile user) {
        String email = null;

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(USERS)
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "single");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    email = jsonObject.getString(EMAIL);
                }

                // For debugging purposes
                Log.i("getEmailAddresByUser",email);

                return email;
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<UserProfile> getMemberListByEvent(Event event) {
        ArrayList<UserProfile> members = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENT_MEMBERSHIPS)
                .appendQueryParameter(ID_EVENT,Integer.toString(event.getEventID()))
                .appendQueryParameter(STATUS,"J");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                //JSONArray jarray = responsePayload.get(0).getJSONArray(ID_USER);

                /*for(int i = 0; i < jarray.length(); i++){
                    JSONObject user = jarray.getJSONObject(i);
                    members.add(new UserProfile(user.getInt(ID_USER),
                            user.getString(FIRSTNAME),
                            user.getString(LASTNAME)));
                }*/

                for(JSONObject jobject:responsePayload){
                    JSONObject json = jobject.getJSONObject(ID_USER);
                    members.add(new UserProfile(json.getInt(ID_USER),
                            json.getString(FIRSTNAME),
                            json.getString(LASTNAME)));
                }


                // For debugging purposes
                for(UserProfile member:members)
                    Log.i("getMemberListByEvent",member.toString());

                return members;
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<LocationPin> getPinsByEvent(Event event) {

        ArrayList<LocationPin> pins = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(LOCATION_PINS)
                .appendQueryParameter(ID_EVENT,Integer.toString(event.getEventID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    pins.add(new LocationPin(jsonObject.getInt(ID_LOCATION_PIN),
                            jsonObject.getDouble(LAT),
                            jsonObject.getDouble(LON),
                            jsonObject.getString(COMMENT)));
                }

                if(pins.isEmpty()){
                    return null;
                }
                else{
                    // For debugging purposes
                    for(LocationPin pin:pins)
                        Log.i("getPinsByEvent",pin.toString());

                    return pins;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

      /*--------------------------------------------------------------------------------------------*/

    @Override
    public Location getLocationByEvent(Event event) {

        ArrayList<Location> locations = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENTS)
                .appendPath(Integer.toString(event.getEventID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    Location location = new Location("manual");
                    location.setLatitude(jsonObject.getDouble(LAT));
                    location.setLongitude(jsonObject.getDouble(LON));
                    locations.add(location);
                }

                // For debugging purposes
                for(Location place:locations){
                    Log.i("getLocationByEvent",
                            "lat: "+Double.toString(place.getLatitude())+" long:"+ Double.toString(place.getLongitude()));
                    return place;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    /*--------------------------------------------------------------------------------------------*/

    @Override
    public String getAccountBioByUser(UserProfile user) {

        ArrayList<String> bios = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(USERS).appendPath(Integer.toString(user.getUserID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    bios.add(jsonObject.getString(BIO));
                }

                // For debugging purposes
                for(String bio:bios){
                    Log.i("getAccountBioByUsert",bio);
                    return bio;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean sendFriendRequest(UserProfile currentUser, UserProfile toAdd) {
        try {
            JSONObject jobject = new JSONObject()
                    .put("sentfriendrequest",currentUser.getUserID())
                    .put("receivedfriendrequest",toAdd.getUserID());

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(FRIENDSHIPS+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                toAdd.setIdFriendship(json_object.getInt(ID_FRIENDSHIP));

                Log.i("toAdd", toAdd.toString());

                return true;
            }

            Log.d("SendFriendRequest", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;


    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean acceptFriendRequest(UserProfile friend) {
        try {
            JSONObject jobject = new JSONObject().put("status","F");

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(FRIENDSHIPS).appendPath(Integer.toString(friend.getIdFriendship())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("acceptFriendRequest", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean declineFriendRequest(UserProfile friend) {
        try {
            JSONObject jobject = new JSONObject().put(STATUS,"D");

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(FRIENDSHIPS).appendPath(Integer.toString(friend.getIdFriendship())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("acceptFriendRequest", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public Event createEvent(String eventName, Date startTime, Date endTime, UserProfile creator,
                             Location eventLocation, ArrayList<UserProfile> members) {

        try {
            JSONObject jobject = new JSONObject()
                    .put(EVENT_NAME,eventName)
                    .put(ID_CREATOR,Integer.toString(creator.getUserID()))
                    //.put("startsat",startTime)
                    //.put("endsin",endTime)
                    .put(STARTS_AT,"2017-10-04T19:35:12Z")
                    .put(ENDS_IN,"2017-10-04T19:35:12Z")
                    .put(LAT,eventLocation.getLatitude())
                    .put(LON,eventLocation.getLongitude());

            JSONArray jsonMembers = new JSONArray();

            for(UserProfile user:members){
                jsonMembers.put(user.getUserID());
            }

            jobject.put("members",jsonMembers);

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(EVENTS+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Event event = new Event(json_object.getInt(ID_EVENT),
                        json_object.getString(EVENT_NAME),
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(json_object.getString(STARTS_AT)),
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(json_object.getString(ENDS_IN)));
                Log.i("Event",event.toString());

                return event;
            }

            Log.d("createEvent", "Request Failed, response code: " + responseCode);
            return null;

        } catch (JSONException | InterruptedException | ExecutionException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    private boolean createEventConversation(Event event){
        try {

            JSONObject jobject = new JSONObject()
                    .put(ID_EVENT,"/events/"+Integer.toString(event.getEventID())+"/");

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(E_CONVERSATIONS);

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }

            Log.d("createEventConversation", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean deleteEvent(Event event) {
        try {
            JSONObject jobject = new JSONObject().put("status","D");

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(EVENTS).appendPath(Integer.toString(event.getEventID())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("deleteEvent",json_object.toString());

                return true;
            }
            Log.d("deleteEvent", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public UserProfile getCreatorByEvent(Event event) {
        ArrayList<UserProfile> users = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENTS)
                .appendPath(Integer.toString(event.getEventID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                for(JSONObject jsonObject : responsePayload){
                    JSONObject jsonCreator = jsonObject.getJSONObject(ID_CREATOR);

                    users.add(new UserProfile(jsonCreator.getInt(ID_USER),
                            jsonCreator.getString(FIRSTNAME),
                            jsonCreator.getString(LASTNAME)));
                }

                // For debugging purposes
                for(UserProfile user:users){
                    Log.i("getCreatorByEvent",user.toString());
                    return user;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<UserProfile> getAdminListByEvent(Event event) {

        ArrayList<UserProfile> users = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENT_MEMBERSHIPS)
                .appendQueryParameter(ID_EVENT,Integer.toString(event.getEventID()))
                .appendQueryParameter(EVENT_ADMIN,"true");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                //members = responsePayload;
                for(JSONObject jsonObject : responsePayload){
                    JSONObject jsonUser = jsonObject.getJSONObject(ID_USER);
                    //sendGetRequest(jsonObject.getString(ID_USER),"jsonOBJECT");

                    users.add(new UserProfile(jsonUser.getInt(ID_USER),
                            jsonUser.getString(FIRSTNAME),
                            jsonUser.getString(LASTNAME)));
                }

                if(users.isEmpty()){
                    return null;
                }
                else{
                    // For debugging purposes
                    for(UserProfile user:users)
                        Log.i("getAdminListByEvent",user.toString());

                    return users;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public UserProfile getCreatorByLocationPin(LocationPin pin) {

        UserProfile creator = null;

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(LOCATION_PINS).appendPath(Integer.toString(pin.getLocationPinID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                for(JSONObject jsonObject : responsePayload){
                    JSONObject user = jsonObject.getJSONObject(ID_USER);
                    creator = new UserProfile(user.getInt(ID_USER),
                            user.getString(FIRSTNAME),
                            user.getString(LASTNAME));
                }

                Log.i("getCreatorByLocationPin",creator.toString());
                return creator;
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean sendEventInvite(Event event, UserProfile sender, UserProfile receiver) {
        try {
            JSONObject jobject = new JSONObject()
                    .put(ID_EVENT,Integer.toString(event.getEventID()))
                    .put(ID_USER,Integer.toString(receiver.getUserID()))
                    .put("senteventinvite",Integer.toString(sender.getUserID()));

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(EVENT_MEMBERSHIPS+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("sendEventInvite",json_object.toString());

                return true;
            }

            Log.d("sendEventInvite", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean acceptEventInvite(Event event) {
        try {
            JSONObject jobject = new JSONObject().put("status","J");

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(EVENT_MEMBERSHIPS).appendPath(Integer.toString(event.getIdEventMembership())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("acceptFriendRequest", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean rejectEventInvite(Event event) {
        try {
            JSONObject jobject = new JSONObject().put("status","D");

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(EVENT_MEMBERSHIPS)
                    .appendPath(Integer.toString(event.getIdEventMembership())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("rejectEventInvite", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean createConversation(ArrayList<UserProfile> users) {
        try {
            JSONArray arrayusers = new JSONArray();
            for(UserProfile user:users){
                arrayusers.put( user.getUserID() );
            }

            JSONObject jobject = new JSONObject().put("users",arrayusers);

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(P_CONVERSATIONS+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);

                Log.i("createConversation",json_object.toString());

                return true;
            }

            Log.d("createConversation", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public String getConversationName(Conversation convo) {
        ArrayList<String> names = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(CONVERSATIONS)
                .appendQueryParameter(ID_CONVERSATION,Integer.toString(convo.getConversationID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    names.add(jsonObject.getString(EVENT_NAME));
                }

                // For debugging purposes
                for(String name:names){
                    Log.i("getConversationName",name);
                    return name;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<UserProfile> getUsersByConversation(Conversation conversation) {

        ArrayList<UserProfile> users = new ArrayList<>();
        Uri.Builder builder = setSchemeAndAuthority();

        if(conversation.getConversationType().equals("P"))
            builder.appendPath(P_CONVERSATIONS);
        else
            builder.appendPath(E_CONVERSATIONS);

        builder.appendPath(Integer.toString(conversation.getConversationID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                JSONArray jarray;

                // For Private Conversations
                if(conversation.getConversationType().equals("P")){
                    jarray = responsePayload.get(0).getJSONArray(USERS);
                }
                else{
                    jarray = responsePayload.get(0).getJSONObject(ID_EVENT).getJSONArray(MEMBERS);
                }

                for(int i = 0; i < jarray.length(); i++){
                    //sendGetRequest(jarray.getString(i),"jsonOBJECT");
                    JSONObject user = jarray.getJSONObject(i);

                    users.add(new UserProfile(user.getInt(ID_USER),
                            user.getString(FIRSTNAME),
                            user.getString(LASTNAME)));
                }

                // For debugging purposes
                for(UserProfile user:users){
                    Log.i("getUsersByConversation",user.toString());
                }

                return users;

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<Message> getMessagesByConversation(Conversation conversation) {

        ArrayList<Message> messages = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(MESSAGES)
                .appendQueryParameter(ID_CONVERSATION,Integer.toString(conversation.getConversationID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                JSONArray jsonarray = new JSONArray(responsePayload);

                for(JSONObject jsonObject : responsePayload){
                    messages.add(new Message(jsonObject.getInt(ID_MESSAGE),
                            jsonObject.getString(MESSAGE),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'").parse(jsonObject.getString(TIMESTAMP))) );
                }

                if(messages.isEmpty()){
                    return null;
                }
                else{
                    // For debugging purposes
                    for(Message message:messages){
                        Log.i("getMessagesConversation",message.toString());
                    }
                }


                return messages;

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public Event getEventByConversation(Conversation conversation) {
        Event event = null;

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(E_CONVERSATIONS)
                .appendPath(Integer.toString(conversation.getConversationID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                for(JSONObject jsonObject : responsePayload){
                    JSONObject jsonEvent = jsonObject.getJSONObject(ID_EVENT);

                    event = new Event(jsonEvent.getInt(ID_EVENT),
                            jsonEvent.getString(EVENT_NAME),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(jsonEvent.getString(STARTS_AT)),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(jsonEvent.getString(ENDS_IN)));
                }

                Log.i("getEventByConversation",event.toString());
                return event;

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public Message sendMessageToConversation(Message message, Conversation conversation, UserProfile sender) {
        int idsender = Factory.getFactory().getUser().getUserID();
        Message sentMessage;
        try {
            JSONObject jobject = new JSONObject().put(ID_CONVERSATION,conversation.getConversationID())
                    .put(ID_SENDER,sender.getUserID())
                    .put(MESSAGE,message.getMessageBody());

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(MESSAGES+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                sentMessage = new Message(json_object.getInt(ID_MESSAGE),
                        json_object.getString(MESSAGE),
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'").parse(json_object.getString(TIMESTAMP)));

                Log.i("Message: ",sentMessage.toString());

                return sentMessage;
            }

            Log.i("sendMessageToConvo", "Request Failed, response code: " + responseCode);

            return null;

        } catch (JSONException | InterruptedException | ExecutionException | ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public UserProfile getSenderByMessage(Message message) {

        UserProfile sender = null;

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(MESSAGES)
                .appendPath(Integer.toString(message.getMessageID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                for(JSONObject jsonObject : responsePayload){
                    JSONObject user = jsonObject.getJSONObject(ID_SENDER);

                    sender = new UserProfile(user.getInt(ID_USER),
                            user.getString(FIRSTNAME),
                            user.getString(LASTNAME));
                }

                Log.i("getSenderByMessage",sender.toString());
                return sender;

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public Conversation getConversationByMessage(Message message) {

        ArrayList<Conversation> conversations = new ArrayList<>();
        Uri.Builder convoURL;
        int id_convo;

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(MESSAGES)
                .appendPath(Integer.toString(message.getMessageID())+"/");

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "jsonOBJECT");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                id_convo = responsePayload.get(0).getInt(ID_CONVERSATION);
                convoURL = setSchemeAndAuthority()
                        .appendPath(CONVERSATIONS)
                        .appendPath(Integer.toString(id_convo));

                sendGetRequest(convoURL.build().toString(),"jsonOBJECT");

                for(JSONObject jsonObject : responsePayload){
                    conversations.add(new Conversation(jsonObject.getInt(ID_CONVERSATION),
                            jsonObject.getString(TYPE),
                            jsonObject.getString("name")));
                }

                // For debugging purposes
                for(Conversation conversation:conversations){
                    Log.i("getConvoByMessage",conversation.toString());
                    return conversation;
                }

            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public boolean updateUserLocation(UserProfile user, Location location) {
        try {
            JSONObject jobject = new JSONObject()
                    .put(LAT,location.getLatitude())
                    .put(LON,location.getLongitude());

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(USERS).appendPath(Integer.toString(user.getUserID())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("updateUserLocation", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public int authenticateUser(String username, String password) {
        return 0;
    }

    @Override
    public boolean addPinToEvent(Location loc, Event event, UserProfile creator, String message) {
        return false;
    }

     /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<Event> getSharedEvents(UserProfile user1, UserProfile user2) {

        ArrayList<Event> events = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENTS).appendPath("get_events_in_common/")
                .appendQueryParameter("iduser1",Integer.toString(user1.getUserID()))
                .appendQueryParameter("iduser2",Integer.toString(user2.getUserID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){

                for(JSONObject jsonObject : responsePayload){
                    events.add(new Event(jsonObject.getInt(ID_EVENT),
                            jsonObject.getString(EVENT_NAME),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(jsonObject.getString(STARTS_AT)),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(jsonObject.getString(ENDS_IN))));
                }

                Log.i("events.isEmpty(): ", String.valueOf(events.isEmpty()));
                if(events.isEmpty()){
                    return null;
                }
                else{
                    // For debugging purposes
                    for(Event event:events)
                        Log.i("getSharedEvents",event.toString());

                    return events;
                }


            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*--------------------------------------------------------------------------------------------*/

    @Override
    public ArrayList<UserProfile> getFriendInvitesByUser(UserProfile user){
        ArrayList<UserProfile> listFriends = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(FRIENDSHIPS).appendPath("get_pending_invites")
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()));

        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){

                    JSONObject sender = jsonObject.getJSONObject("sentfriendrequest");

                    listFriends.add(new UserProfile(sender.getInt(ID_USER),
                            jsonObject.getInt(ID_FRIENDSHIP),
                            sender.getString(FIRSTNAME),
                            sender.getString(LASTNAME),
                            jsonObject.getString(STATUS)));
                }

                // For debugging purposes
                for(UserProfile friend:listFriends)
                    Log.i("getFriendsByUser",friend.toString());

                return listFriends;
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ArrayList<Event> getEventInvitesByUser(UserProfile user){
        ArrayList<Event> listEvents = new ArrayList<>();

        Uri.Builder builder = setSchemeAndAuthority();
        builder.appendPath(EVENT_MEMBERSHIPS)
                .appendQueryParameter(ID_USER,Integer.toString(user.getUserID()))
                .appendQueryParameter(STATUS,"R");
        try {
            Log.i("Network Handler",builder.build().toString());
            sendGetRequest(builder.build().toString(), "multiple");

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK){
                for(JSONObject jsonObject : responsePayload){
                    JSONObject jsonEvent = jsonObject.getJSONObject("idevent");
                    listEvents.add(new Event(jsonEvent.getInt(ID_EVENT),
                            jsonObject.getInt(ID_EVENT_MEMBERSHIP),
                            jsonEvent.getString(EVENT_NAME),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    .parse(jsonEvent.getString(STARTS_AT)),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    .parse(jsonEvent.getString(ENDS_IN))) );
                }

                if(listEvents.isEmpty()){
                    return null;
                }
                else{
                    for (Event event : listEvents){
                        Log.i("getEventsByUser: ",event.toString());
                    }
                    return listEvents;
                }
            }
            else{
                Log.i("NetworkManager", "Request Failed, response code: " + responseCode);
                return null;
            }
        } catch (ExecutionException | InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public LocationPin createLocationPin(Location location, Event event, UserProfile user, String comment){
        try {
            JSONObject jobject = new JSONObject().put(COMMENT,comment)
                    .put(ID_EVENT,event.getEventID())
                    .put(ID_USER,user.getUserID())
                    .put(LAT,location.getLatitude())
                    .put(LON,location.getLongitude());

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(LOCATION_PINS+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPostRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                LocationPin pin = new LocationPin(json_object.getInt(ID_LOCATION_PIN),
                        json_object.getDouble(LAT),
                        json_object.getDouble(LON),
                        json_object.getString(COMMENT));

                Log.i("createLocationPin",pin.toString());

                return pin;
            }

            Log.d("createLocationPin", "Request Failed, response code: " + responseCode);
            return null;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*--------------------------------------------------------------------------------------------*/
    /*--------------------------------------------------------------------------------------------*/

    private boolean sendPatchRequest(JSONObject jobject,String url) throws ExecutionException, InterruptedException {

        Triplet<JSONObject,String, NetworkHandler> trip;
        trip = new Triplet<>(jobject, url, this);
        Log.i("sendPostRequest",url);

        return new PatchRequest().execute(trip).get();
    }


    /*--------------------------------------------------------------------------------------------*/
     private boolean sendPostRequest(JSONObject jobject,String url) throws ExecutionException, InterruptedException {

     Triplet<JSONObject,String, NetworkHandler> trip;
     trip = new Triplet<>(jobject, url, this);
         Log.i("sendPostRequest",url);

        return new Request().execute(trip).get();
     }

     /*--------------------------------------------------------------------------------------------*/

     private boolean sendGetRequest(String url,String responseType) throws ExecutionException, InterruptedException {
         Pair<NetworkHandler,String> secondArgs;

         if(responseType.equals("multiple")) {
             secondArgs = new Pair(this,"multiple"); }
         else if(responseType.equals("single")){
             secondArgs = new Pair(this,"single"); }
         else{
             secondArgs = new Pair(this,"jsonOBJECT"); }

         Pair paramsGetRequest = new Pair(url,secondArgs);
         return new GetRequest().execute(paramsGetRequest).get();
     }

     /*--------------------------------------------------------------------------------------------*/

     void setPayload(ArrayList<JSONObject> payload){
     responsePayload = payload;
     }

     void setHttpError(int code){
     responseCode = code;
     }

     /*--------------------------------------------------------------------------------------------*/

     private Uri.Builder setSchemeAndAuthority(){
        return new Uri.Builder().scheme(scheme).authority(serverIP);
     }

     /*--------------------------------------------------------------------------------------------*/

     private boolean addCreatorToMembersList(Event event, UserProfile user){
         try {
             JSONObject jobject = new JSONObject()
                     .put(ID_EVENT,"/events/"+Integer.toString(event.getEventID())+"/")
                     .put(ID_USER,"/users/"+Integer.toString(user.getUserID())+"/")
                     .put(SENT_EVENT_INVITE,"/users/"+Integer.toString(user.getUserID())+"/")
                     .put(EVENT_ADMIN,"true")
                     .put(STATUS,"J");

             Uri.Builder builder = setSchemeAndAuthority();
             builder.appendPath(EVENT_MEMBERSHIPS);

             Log.i("Network Handler",builder.build().toString());
             sendPostRequest(jobject,builder.build().toString());

             //Extract and return data from our payload
             if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                     || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                 // Get first and only element from ArrayList
                 json_object = responsePayload.get(0);
                 Log.i("addCreatorToMemeberList",json_object.toString());

                 return true;
             }

             Log.d("addCreatorToMemeberList", "Request Failed, response code: " + responseCode);
             return false;

         } catch (JSONException | InterruptedException | ExecutionException e) {
             e.printStackTrace();
         }

         return false;
     }


     /*--------------------------------------------------------------------------------------------*/

    @Override
    public UserProfile sendIdTokenToServer(String idToken) {
        try {
            JSONObject jobject = new JSONObject()
                    .put("idToken", idToken);

            Uri.Builder url = new Uri.Builder().scheme("http")
                    .authority(serverIP)
                    .appendPath(LOGIN + "/");

            Log.i("Network Handler",url.build().toString());
            sendPostRequest(jobject,url.build().toString());

            Log.i("ResponseCode: ", Integer.toString(responseCode) );
            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_ACCEPTED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("userprofandtoken",json_object.toString());

                UserProfile user = new UserProfile(json_object.getInt(ID_USER),
                                                   json_object.getString(FIRSTNAME),
                                                   json_object.getString(LASTNAME));
                return user;
            }

            Log.d("SendIdToken", "Request Failed, response code: " + responseCode);
            return null;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateUserFirebaseToken(UserProfile user, String firebaseToken) {
        try {
            JSONObject jobject = new JSONObject()
                    .put(FIREBASE_TOKEN,firebaseToken);

            Uri.Builder builder = setSchemeAndAuthority();
            builder.appendPath(USERS).appendPath(Integer.toString(user.getUserID())+"/");

            Log.i("Network Handler",builder.build().toString());
            sendPatchRequest(jobject,builder.build().toString());

            //Extract and return data from our payload
            if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){

                // Get first and only element from ArrayList
                json_object = responsePayload.get(0);
                Log.i("user",json_object.toString());

                return true;
            }
            Log.d("updateUserFirebaseToken", "Request Failed, response code: " + responseCode);
            return false;

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    private boolean sendAuthRequest(JSONObject jobject,String url) throws ExecutionException, InterruptedException {

        Triplet<JSONObject,String, NetworkHandler> trip;
        trip = new Triplet<>(jobject, url, this);
        Log.i("sendAuthRequest",url);

        return new LogInRequest().execute(trip).get();
    }

}
