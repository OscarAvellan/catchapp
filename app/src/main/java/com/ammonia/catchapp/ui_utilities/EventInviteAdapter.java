package com.ammonia.catchapp.ui_utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.Event;

import java.util.List;

/**
 * Created by dlolpez on 12/10/17.
 */

public class EventInviteAdapter extends MyAbstractRecyclerViewAdapter {
    NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();

    /* ------------------------- Methods to bind XML components to data ------------------------- */


    public EventInviteAdapter(Context context, List<Item> eventList){
        super(context, eventList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_layout, parent, false);
        return new EventInviteAdapter.EventInviteHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Event event = (Event) mList.get(position);
        ((EventInviteAdapter.EventInviteHolder) holder).bind(event);
    }

    /**
     * ViewHolders to bind data to the layout provided.
     */
    private class EventInviteHolder extends RecyclerView.ViewHolder{
        TextView nameText;
        Button acceptButton;
        Button rejectButton;

        EventInviteHolder(View itemView){
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.text_invite_name);
            acceptButton = (Button) itemView.findViewById(R.id.button_accept_invite);
            rejectButton = (Button) itemView.findViewById(R.id.button_reject_invite);
        }

        void bind(final Event event){
            nameText.setText(event.getEventName());
            acceptButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Inform the user the button has been clicked
                    nm.acceptEventInvite(event);
                    Toast.makeText(mContext, "Invite accepted!",
                            Toast.LENGTH_LONG).show();
                    mList.remove(mList.indexOf(event));
                    notifyDataSetChanged();
                }
            });
            rejectButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Inform the user the button has been clicked
                    nm.rejectEventInvite(event);
                    Toast.makeText(mContext, "Invite rejected!",
                            Toast.LENGTH_LONG).show();
                    mList.remove(mList.indexOf(event));
                    notifyDataSetChanged();
                }
            });
        }
    }


}
