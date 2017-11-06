package com.ammonia.catchapp.ViewTypes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.EventMemberAdapter;

import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventActivity extends BaseActivity {

    private EventMemberAdapter mMemberAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private Event event;
    private ArrayList<UserProfile> eventMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get related Event from the our Intent
        Bundle bundle = getIntent().getExtras();
        this.event = (Event) bundle.getSerializable("EVENT");

        this.eventMembers = event.getMemberList();

        setToolbarTitle(event.getEventName());

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_member_list);

        if(eventMembers!=null) {
            mMemberAdapter = new EventMemberAdapter(this, new ArrayList<Item>(eventMembers));
        }else{
            mMemberAdapter = new EventMemberAdapter(this, new ArrayList<Item>());
        }

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMemberAdapter);

        TextView startTime = (TextView)findViewById(R.id.startsAtText);
        TextView endTime = (TextView)findViewById(R.id.endsAtText);
        TextView creatorText = (TextView)findViewById(R.id.creatorText);

        DateFormat df = new SimpleDateFormat("hh':'mm' on 'EEE', 'MMM' 'd" );

        startTime.append(df.format(event.getStartTime()));
        endTime.append(df.format(event.getEndTime()));
        UserProfile creator = event.getCreator();
        creatorText.append(creator.getFirstName() + " " + creator.getLastName());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myConstraintLayout),
                        "Friends invited!", Snackbar.LENGTH_SHORT);
                mySnackbar.show();


            } else if (resultCode == RESULT_CANCELED) {
                // do your task
            }
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_event;
    }

    public void openMapView (View view){
        Intent intent = new Intent(this,MapView.class);
        intent.putExtra("event", this.event);
        startActivity(intent);

    }

    public void inviteFriends(View view){
        Intent intent = new Intent(this, SelectFriendsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("NEW_OR_EXISTING", "EXISTING");
        bundle.putSerializable("EVENT_MEMBERS", eventMembers);
        bundle.putSerializable("EVENT", event);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);

    }

    @Override
    public void refresh(){

    }
}

