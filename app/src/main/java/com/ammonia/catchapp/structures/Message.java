package com.ammonia.catchapp.structures;

import java.io.Serializable;
import java.util.Date;

import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.ui_utilities.Item;

/**
 * Created by morley on 23/08/17.
 */

public class Message implements Serializable, Comparable<Message>, Item{
    private int messageID;
    private String messageBody;
    private Date timeStamp;

    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    public Message(int messageID, String messageBody, Date timeStamp){
        this.messageID = messageID;
        this.messageBody = messageBody;
        this.timeStamp = timeStamp;
    }

    public Message(String messageBody, Date timeStamp){
        this.messageBody = messageBody;
        this.timeStamp = timeStamp;
    }


    /*------------------------------------- CONSTRUCTORS -----------------------------------------*/

    /*--------------------------------------- GETTERS  -------------------------------------------*/

    public int getMessageID(){ return messageID; }
    public String getMessageBody(){ return this.messageBody; }
    public Date getTimeStamp(){ return this.timeStamp; }

    public UserProfile getSender(){
        UserProfile sender;
        sender = Factory.getFactory().getNetworkManager().getSenderByMessage(this);
        return sender;
    }

    /*--------------------------------------- GETTERS  -------------------------------------------*/

    /*--------------------------------------- SETTERS  -------------------------------------------*/
    /*Note: setters not provided for messagetype, sender, timestamp, messagebody
    * as these properties do not change over the life of a message*/

    /*--------------------------------------- SETTERS  -------------------------------------------*/

    @Override
    public String toString() {
        return "Message{" +
                "messageID=" + messageID +
                ", messageBody='" + messageBody + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
    @Override
    public int compareTo(Message m) {
        return getTimeStamp().compareTo(m.getTimeStamp());
    }

    public String getListString(){
        return "";
    }

    public String getType(){
        return MESSAGE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (messageID != message.messageID) return false;
        if (!messageBody.equals(message.messageBody)) return false;
        return timeStamp.equals(message.timeStamp);
    }

}
