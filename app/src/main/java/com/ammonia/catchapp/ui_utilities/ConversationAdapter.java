package com.ammonia.catchapp.ui_utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.MockNetworkManager;
import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Message;
import com.ammonia.catchapp.structures.NetworkHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by dlolpez on 29/9/17.
 */

/**
 * The recycler view needs an Adapter to bind the XML components to actual data
 */

public class ConversationAdapter extends MyAbstractRecyclerViewAdapter {
    private Conversation mConversation;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    /* ------------------------- Methods to bind XML components to data ------------------------- */


    public ConversationAdapter(Context mContext, List<Item> messageList, Conversation conversation){
        super(mContext, messageList);
        mConversation = conversation;
    }

    @Override
    public int getItemViewType(int position){
        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
        Message message = (Message) mList.get(position);
        if(nm.getSenderByMessage(message).getUserID() == Factory.getFactory().getUser().getUserID()){
            return VIEW_TYPE_MESSAGE_SENT;
        } else{
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Message message = (Message) mList.get(position);

        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    /**
     * ViewHolders to bind data differently depending if the message is sent or received.
     */
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView){
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);

        }

        void bind(Message message){
            messageText.setText(message.getMessageBody());
            timeText.setText(DateUtils.formatDateTime(mContext, message.getTimeStamp().getTime(),
                    DateUtils.FORMAT_SHOW_TIME));
            nameText.setText(Factory.getFactory().getNetworkManager().
                    getSenderByMessage(message).getFirstName());
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView messageText, timeText;

        SentMessageHolder(View itemView){
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message){
            messageText.setText(message.getMessageBody());
            timeText.setText(DateUtils.formatDateTime(mContext, message.getTimeStamp().getTime(),
                    DateUtils.FORMAT_SHOW_TIME));
        }
    }

    /* ------------------ Methods to fetch messages and store them in ArrayList ----------------- */

    /**
     * Sends a new message and appends the sent message to the beginning of the message list
     */
    public void sendMessage(final String body){
        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
        Message m = new Message(body, new Date());
        Message new_message = nm.sendMessageToConversation(m, mConversation, Factory.getFactory().getUser());
        if(new_message != null){
            mList.add(0, (Item) new_message);
        }
        notifyDataSetChanged();
    }

    public void refresh() {
        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
        ArrayList<Message> messages = nm.getMessagesByConversation(mConversation);
        if(messages != null){
            Log.i("heey", "hi");
            if(messages.size() > mList.size()) {
                Collections.sort(messages);
                Collections.reverse(messages);
                mList = new ArrayList<Item>(messages);
                notifyDataSetChanged();
            }
        }
    }

    public void loadPreviousMessages(){

    }

    public void appendMessage(){

    }
}
