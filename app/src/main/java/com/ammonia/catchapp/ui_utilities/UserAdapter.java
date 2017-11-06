package com.ammonia.catchapp.ui_utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ViewTypes.CreateConversationActivity;
import com.ammonia.catchapp.ViewTypes.SearchFriendActivity;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dlolpez on 10/10/17.
 */

/**
 * The recycler view needs an Adapter to bind the XML components to actual data
 */

public class UserAdapter extends MyAbstractRecyclerViewAdapter {

    /* ------------------------- Methods to bind XML components to data ------------------------- */


    public UserAdapter(Context context, List<Item> userList){
        super(context, userList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_layout, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        final UserProfile user = (UserProfile) mList.get(position);
        Log.i("membeer", user.getFirstName());
        ((UserHolder) holder).bind(user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof SearchFriendActivity) {
                    sendFriendRequest(user);
                    ((SearchFriendActivity) mContext).finish();
                }else{
                    createConversation(user);
                    ((CreateConversationActivity)mContext).finish();
                }
            }
        });
    }

    private void createConversation(UserProfile other){
        NetworkManagerInterface nh = Factory.getFactory().getNetworkManager();
        UserProfile currentUser = Factory.getFactory().getUser();
        ArrayList<UserProfile> members = new ArrayList<UserProfile>();
        members.add(currentUser);
        members.add(other);
        nh.createConversation(members);
    }

    private void sendFriendRequest(UserProfile to){
        NetworkManagerInterface nh = Factory.getFactory().getNetworkManager();
        UserProfile from = Factory.getFactory().getUser();
        nh.sendFriendRequest(from, to);
    }



    /**
     * ViewHolders to bind data differently depending if the message is sent or received.
     */
    private class UserHolder extends RecyclerView.ViewHolder{
        TextView nameText;

        UserHolder(View itemView){
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.text_event_name);
        }

        void bind(UserProfile member){
            nameText.setText(member.getFirstName() + " " + member.getLastName());
        }
    }

}