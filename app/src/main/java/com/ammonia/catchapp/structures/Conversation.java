package com.ammonia.catchapp.structures;


import java.io.Serializable;
import com.ammonia.catchapp.ui_utilities.Item;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;

import java.util.ArrayList;
/**
 * Data type that stores the information related to a specific conversation
 */

public class Conversation implements Item, Serializable {

    private int conversationID;
    private boolean hasAssociatedEvent;
    private String type, name;

    /*Note: Data is directly used, not copied. This means that changes
    * made to the initialization data within the creator of the Conversation
    * will be reflected within the Conversation itself*/
    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    /*This constructor would be called when creating a conversation with
    * an associated event*/
    public Conversation(int conversationID, boolean hasAssociatedEvent) {
        this.conversationID = conversationID;
        this.hasAssociatedEvent = hasAssociatedEvent;
    }

    public Conversation(int conversationID, String type, String name) {
        this.conversationID = conversationID;
        this.type = type;
        this.name = name;
    }

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    /*--------------------------------------- GETTERS  -------------------------------------------*/
    public int getConversationID(){ return this.conversationID; }
    public boolean hasAssociatedEvent(){ return this.hasAssociatedEvent; }
    public String getConversationType(){ return this.type; }


    public ArrayList<UserProfile> getConversationMembers(){
        ArrayList<UserProfile> convMembers;
        convMembers = Factory.getFactory().getNetworkManager().getUsersByConversation(this);
        return convMembers;
    }

    public ArrayList<Message> getMessageHistory(){
        ArrayList<Message> convMessages;
        convMessages = Factory.getFactory().getNetworkManager().getMessagesByConversation(this);
        return convMessages;
    }

    public Event getAssociatedEvent(){
        if(!this.hasAssociatedEvent){
            return null;
        }else {
            Event convEvent;
            convEvent = Factory.getFactory().getNetworkManager().getEventByConversation(this);
            return convEvent;
        }
    }

    /*--------------------------------------- GETTERS  -------------------------------------------*/

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    protected void setConversationID(int newConversationID){
        this.conversationID = newConversationID;
    }

    protected void setHasAssociatedEvent(boolean newHasAssociatedEvent){
        this.hasAssociatedEvent = newHasAssociatedEvent;
    }

    /*--------------------------------------- SETTERS  -------------------------------------------*/


    public void sendMessage(Message message, UserProfile sender){
        Factory.getFactory().getNetworkManager().sendMessageToConversation(message, this, sender);
    }

    @Override
    public String getListString(){
        return this.name;
    }

    @Override
    public String getType(){
        return CONVERSATION;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationID=" + conversationID +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conversation that = (Conversation) o;

        if (conversationID != that.conversationID) return false;
        if (!type.equals(that.type)) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = conversationID;
        result = 31 * result + type.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
