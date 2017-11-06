package com.ammonia.catchapp.structures;


import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.ammonia.catchapp.ui_utilities.Item;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;


/**
 * Created by morley on 23/08/17.
 */


public class UserProfile implements Item, Serializable {
    private int userID;

    private int idFriendship;
    private String firstName;
    private String lastName;
    private Date locationTimestamp;
    private double lastLat;
    private double lastLong;
    private String status;

    private final int LOCATION_UPDATE_WAIT = 5000;

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    public UserProfile(int userIDInit, String firstNameInit, String lastNameInit,
                       NetworkManagerInterface networkManagerInit){
        this.userID = userIDInit;
        this.firstName = firstNameInit;
        this.lastName = lastNameInit;
        locationTimestamp = new Date(new Date().getTime() - LOCATION_UPDATE_WAIT);
    }

    // Used for creating friends or people that has received a Friendship invitation
    public UserProfile(int userID,int idFriendship, String firstName, String lastName) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idFriendship = idFriendship;
    }

    // Used for creating users that are not related to the app user
    public UserProfile(int userID, String firstName, String lastName) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserProfile(int userID, String firstName, String lastName, Date locationTimestamp, double lastLat, double lastLong) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.locationTimestamp = locationTimestamp;
        this.lastLat = lastLat;
        this.lastLong = lastLong;
    }

    public UserProfile(int userID, int idFriendship, String firstName, String lastName, String status) {
        this.userID = userID;
        this.idFriendship = idFriendship;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    /*--------------------------------------- GETTERS  -------------------------------------------*/
    public int getUserID(){return this.userID;}
    public String getFirstName(){return this.firstName;}
    public String getLastName(){return this.lastName;}
    public int getIdFriendship() {
        return idFriendship;
    }

    public String getEmailAddress(){
        String myEmailAddress;
        myEmailAddress = Factory.getFactory().getNetworkManager().getEmailAddressByUser(this);
        return myEmailAddress;
    }

    public String getAccountBiography(){
        String myAccountBio;
        myAccountBio = Factory.getFactory().getNetworkManager().getAccountBioByUser(this);
        return myAccountBio;
    }

    public ArrayList<UserProfile> getFriends(){
        ArrayList<UserProfile> myFriends;
        myFriends = Factory.getFactory().getNetworkManager().getFriendsByUser(this);
        return myFriends;
    }

    public ArrayList<Conversation> getConversations(){
        ArrayList<Conversation> myConversations;
        myConversations = Factory.getFactory().getNetworkManager().getConversationByUser(this);
        return myConversations;
    }

    public ArrayList<Event> getEvents(){
        ArrayList<Event> myEvents;
        myEvents = Factory.getFactory().getNetworkManager().getEventsByUser(this);
        return myEvents;
    }

    public Location getLocation(){
        Location myLocation = new Location("manual");
        if(locationTimestamp == null) {
            Location updatedLocation = Factory.getFactory().getNetworkManager().getUserLocation(this);
            this.lastLat = updatedLocation.getLatitude();
            this.lastLong = updatedLocation.getLongitude();
            this.locationTimestamp = new Date();
        }else {
            Date checkTimestamp = new Date(locationTimestamp.getTime() + LOCATION_UPDATE_WAIT);
            Date currentTimestamp = new Date();
            if (currentTimestamp.after(checkTimestamp)) {
                Location updatedLocation = Factory.getFactory().getNetworkManager().getUserLocation(this);
                this.lastLat = updatedLocation.getLatitude();
                this.lastLong = updatedLocation.getLongitude();
                this.locationTimestamp = new Date();
            }
        }
        myLocation.setLatitude(lastLat);
        myLocation.setLongitude(lastLong);
        return myLocation;
    }

    @Override
    public String getListString(){
        return getFirstName() + getLastName();
    }

    @Override
    public String getType(){
        return null;
    }
    /*--------------------------------------- GETTERS  -------------------------------------------*/

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    protected void setUserID(int newUserID){ this.userID = newUserID;}
    protected void setFirstName(String newFirstName){
        this.firstName = newFirstName;
    }
    protected void setLastName(String newLastName){
        this.lastName = newLastName;
    }
    public void setIdFriendship(int idFriendship) {
        this.idFriendship = idFriendship;
    }

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    /*This method still needs some kind of user verification, whether that be via
    * firebase token or some other method is still uncertain*/
    public void sendLocation(float latitude, float longitude, Date timestamp){
        Location newLocation = new Location("manual");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        Factory.getFactory().getNetworkManager().updateUserLocation(this,newLocation);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userID=" + userID +
                ", idFriendship=" + idFriendship +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", locationTimestamp=" + locationTimestamp +
                ", lastLat=" + lastLat +
                ", lastLong=" + lastLong +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        UserProfile that = (UserProfile) o;

        if (userID != that.userID){
            return false;
        }else{
            return true;
        }
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        UserProfile that = (UserProfile) o;
//
//        if (userID != that.userID) return false;
//        if (idFriendship != that.idFriendship) return false;
//        if (Double.compare(that.lastLat, lastLat) != 0) return false;
//        if (Double.compare(that.lastLong, lastLong) != 0) return false;
//        if (!firstName.equals(that.firstName)) return false;
//        if (!lastName.equals(that.lastName)) return false;
//        return locationTimestamp != null ? locationTimestamp.equals(that.locationTimestamp) : that.locationTimestamp == null;

    }

}
