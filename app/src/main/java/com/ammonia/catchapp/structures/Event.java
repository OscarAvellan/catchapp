package com.ammonia.catchapp.structures;

import android.location.Location;

import java.io.Serializable;
import com.ammonia.catchapp.ui_utilities.Item;
import com.ammonia.catchapp.factory.Factory;

import java.util.ArrayList;
import java.util.Date;

import static com.ammonia.catchapp.structures.Event.MembershipStatus.DECLINED;

/**
 * Created by morley on 24/08/17.
 */


public class Event implements Item, Serializable {

    public enum Status{PLANNED, CURRENT, PAST};
    public enum MembershipStatus{DECLINED, REQUESTED, JOINED};
    private int idEvent, idEventMembership;
    private String membershipStatus;
    private String name;
    private Date startTime;
    private Date endTime;

    /*Note: Data is directly used, not copied. This means that changes
    * made to the initialization data within the creator of the Event
    * will be reflected within the Event itself*/
    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    public Event(int eventID,
                 String name,
                 Date startTime,
                 Date endTime
    ){
        this.idEvent = eventID;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime; }

    public Event(int idEvent, int idEventMembership, String name, Date startTime, Date endTime) {
        this.idEvent = idEvent;
        this.idEventMembership = idEventMembership;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public Event(int idEvent, int idEventMembership, String status, String name, Date startTime, Date endTime) {
        this.idEvent = idEvent;
        this.idEventMembership = idEventMembership;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.membershipStatus = status;
    }

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    /*--------------------------------------- GETTERS  -------------------------------------------*/
    public int getEventID(){ return this.idEvent; }
    public String getEventName(){ return this.name; }
    public Date getStartTime(){ return this.startTime; }
    public Date getEndTime(){ return this.endTime; }
    public int getIdEventMembership() { return idEventMembership; }

    public UserProfile getCreator(){
        UserProfile creator;
        creator = Factory.getFactory().getNetworkManager().getCreatorByEvent(this);
        return creator;
    }
    public ArrayList<UserProfile> getAdminList(){
        ArrayList<UserProfile> adminList;
        adminList = Factory.getFactory().getNetworkManager().getAdminListByEvent(this);
        return adminList;
    }
    public ArrayList<UserProfile> getMemberList(){
        ArrayList<UserProfile> memberList;
        memberList = Factory.getFactory().getNetworkManager().getMemberListByEvent(this);
        return memberList;
    }
    public Location getLocation(){
        Location eventLocation;
        eventLocation = Factory.getFactory().getNetworkManager().getLocationByEvent(this);
        return eventLocation;
    }
    public ArrayList<LocationPin> getLocationPins(){
        ArrayList<LocationPin> locationPins;
        locationPins = Factory.getFactory().getNetworkManager().getPinsByEvent(this);
        return locationPins;
    }

    public Status getEventStatus(){
        Date currentTime = new Date();
        if(currentTime.before(this.startTime)){
            return Status.PLANNED;
        }else if (currentTime.before(this.endTime)){
            return Status.CURRENT;
        }else{
            return Status.PAST;
        }
    }

    public MembershipStatus getMembershipStatus(){
        if(membershipStatus.equals("D")){
            return MembershipStatus.DECLINED;
        }else if(membershipStatus.equals("R")){
            return MembershipStatus.REQUESTED;
        }else if(membershipStatus.equals("J")){
            return MembershipStatus.JOINED;
        }
        return null;
    }

    @Override
    public String getListString(){
        return getEventName();
    }

    @Override
    public String getType(){
        return EVENT;
    }

    /*--------------------------------------- GETTERS  -------------------------------------------*/

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    protected void setEventID(int newEventID){ this.idEvent = newEventID; }
    protected void setEventName(String newName){
        this.name = newName;
    }
    protected void setStartTime(Date newStartTime){ this.startTime = newStartTime; }
    protected void setEndTime(Date newEndTime){ this.endTime = newEndTime; }

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    @Override
    public String toString() {
        return "Event{" +
                "idEvent=" + idEvent +
                ", idEventMembership=" + idEventMembership +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (idEvent != event.idEvent) return false;
        if (idEventMembership != event.idEventMembership) return false;
        if (!name.equals(event.name)) return false;
        if (!startTime.equals(event.startTime)) return false;
        return endTime.equals(event.endTime);

    }

}
