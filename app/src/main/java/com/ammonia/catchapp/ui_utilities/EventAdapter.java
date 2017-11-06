package com.ammonia.catchapp.ui_utilities;

/**
 * Created by dlolpez on 1/10/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.structures.Event;

import java.util.List;

/**
 * The recycler view needs an Adapter to bind the XML components to actual data
 */

public class EventAdapter extends MyAbstractRecyclerViewAdapter {


    /* ------------------------- Methods to bind XML components to data ------------------------- */


    public EventAdapter(Context context, List<Item> eventList){
        super(context, eventList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_layout, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        Event event = (Event) mList.get(position);
        ((EventHolder) holder).bind(event);
    }

    /**
     * ViewHolders to bind data to the layout provided.
     */
    private class EventHolder extends RecyclerView.ViewHolder{
        TextView nameText;

        EventHolder(View itemView){
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.text_event_name);
        }

        void bind(Event event){
            nameText.setText(event.getEventName());
        }
    }

}