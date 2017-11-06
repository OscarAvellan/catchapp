package com.ammonia.catchapp.structures;

import android.location.Location;

import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by morley on 24/08/17.
 */

public class LocationPin implements Serializable {
    private int locationPinID;
    private double latitude;
    private double longitude;
    private String comment;

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    public LocationPin(int locationPinID, double latitude, double longitude,
                       String comment){
        this.locationPinID = locationPinID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;

    }

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    /*--------------------------------------- GETTERS  -------------------------------------------*/

    public int getLocationPinID(){return this.locationPinID;}
    public double getLatitude(){return this.latitude;}
    public double getLongitude(){return this.longitude;}
    public String getComment() { return comment; }

    public Location getLocation(){
        Location newLocation = new Location("manual");
        newLocation.setLatitude(this.latitude);
        newLocation.setLongitude(this.longitude);
        return newLocation;
    }

    public UserProfile getCreator(){
        UserProfile creator;
        creator = Factory.getFactory().getNetworkManager().getCreatorByLocationPin(this);
        return creator;
    }

    /*--------------------------------------- GETTERS  -------------------------------------------*/


    /*--------------------------------------- SETTERS  -------------------------------------------*/
    /*Note: Setters not provided for creationTime and creator as these
    * properties do not change over the life of a LocationPin*/
    protected void setLocationPinID(int newLocationPinID){ this.locationPinID = newLocationPinID; }
    protected void setLatitude(float newLatitude){
        this.latitude = newLatitude;
    }
    protected void setLongitude(float newLongitude){
        this.longitude = newLongitude;
    }
    protected void setComment(String newComment){ this.comment = newComment; }

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    @Override
    public String toString() {
        return "LocationPin{" +
                "locationPinID=" + locationPinID +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationPin that = (LocationPin) o;

        if (locationPinID != that.locationPinID) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        return comment != null ? comment.equals(that.comment) : that.comment == null;

    }

}
