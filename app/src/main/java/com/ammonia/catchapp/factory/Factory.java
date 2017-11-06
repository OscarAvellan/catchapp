package com.ammonia.catchapp.factory;

import android.location.Location;

import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.FakeDB;
import com.ammonia.catchapp.structures.UserProfile;

import java.util.ArrayList;

/**
 * Created by Oscar on 23/09/2017.
 */

public class Factory {

    private static UserProfile user;
    private Location lastLocation;

    private FakeDB fakeDatabase = new FakeDB();
    private NetworkManagerInterface networkManager;
    public FakeDB getDatabase(){return this.fakeDatabase;}

    public NetworkManagerInterface getNetworkManager(){return this.networkManager;}
    public void setNetworkManager(NetworkManagerInterface networkManager){this.networkManager = networkManager;}

    private static final Factory factory = new Factory();

    private Factory(){ };

    public static Factory getFactory(){
        return factory;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public UserProfile getUser(){
        return user;
    }

    public void setLastLocation(Location loc){ lastLocation = loc;}
    public Location getLastLocation(){return lastLocation;}

    @Override
    public String toString() {
        return "Factory{" +
                "user=" + user +
                '}';
    }

}
