package com.ammonia.catchapp.ui_utilities;

/**
 * Created by dlolpez on 18/9/17.
 */

/**
 * To make it easier to abstract, we refer to everything that can be displayed in a recycler view as
 * an Item.
 */

public interface Item {
    public static final String EVENT = "Event";
    public static final String CONVERSATION = "Conversation";
    public static final String USER = "User";
    public static final String MESSAGE = "Message";

    public String getListString();
    public String getType();
}

