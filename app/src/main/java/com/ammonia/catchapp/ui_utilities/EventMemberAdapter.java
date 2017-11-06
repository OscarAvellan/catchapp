package com.ammonia.catchapp.ui_utilities;

/**
 * Created by dlolpez on 1/10/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ViewTypes.ARActivity;
import com.ammonia.catchapp.ViewTypes.FriendActivity;
import com.ammonia.catchapp.structures.UserProfile;

import java.util.List;

/**
 * The recycler view needs an Adapter to bind the XML components to actual data
 */

public class EventMemberAdapter extends MyAbstractRecyclerViewAdapter {


    /* ------------------------- Methods to bind XML components to data ------------------------- */


    public EventMemberAdapter(Context context, List<Item> messageList){
        super(context, messageList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_member_layout, parent, false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        final UserProfile member = (UserProfile) mList.get(position);
        ((MemberHolder) holder).bind(member);
    }

    /**
     * ViewHolders to bind data differently depending if the message is sent or received.
     */
    private class MemberHolder extends RecyclerView.ViewHolder{
        TextView nameText;
        ImageView profileImage;
        Button aRButton;
        Button profileButton;
        UserProfile myMember;


        MemberHolder(View itemView){
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.text_member_name);
            aRButton = (Button)itemView.findViewById(R.id.arStartButton);
            profileButton = (Button)itemView.findViewById(R.id.userProfileButton);
            aRButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAR(myMember);
                }
            });

            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openUser(myMember);
                }
            });
        }

        void bind(UserProfile member){
            myMember = member;
            nameText.setText(member.getFirstName() + " " + member.getLastName());
        }
    }


    public void openAR(UserProfile member){
        Intent intent = new Intent(mContext,ARActivity.class);
        intent.putExtra("user", member);
        mContext.startActivity(intent);
    }

    public void openUser(UserProfile member){
        Intent intent =  new Intent(mContext, FriendActivity.class);
        intent.putExtra("FRIEND", member);
        mContext.startActivity(intent);
    }
}