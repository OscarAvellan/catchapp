package com.ammonia.catchapp.ViewTypes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.EventAdapter;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.MockNetworkManager;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.Item;

import java.util.ArrayList;

public class FriendActivity extends BaseActivity {

    private EventAdapter mEventAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        UserProfile friend = (UserProfile) bundle.getSerializable("FRIEND");

        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();

        setToolbarTitle(friend.getFirstName() + " " + friend.getLastName());

        ArrayList<Event> returnedEvents = nm.getSharedEvents(Factory.getFactory().getUser(), friend);

        if (returnedEvents != null){
            mEventAdapter = new EventAdapter(this, new ArrayList<Item>(returnedEvents));
        }else{
            mEventAdapter = new EventAdapter(this, new ArrayList<Item>());
        }

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_event_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mEventAdapter);
    }

    @Override
    public int getLayoutResource(){
        return R.layout.activity_friend;
    }

    @Override
    public void refresh(){

    }

}
