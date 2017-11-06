package com.ammonia.catchapp.ViewTypes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.DummyData;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.EventInviteAdapter;
import com.ammonia.catchapp.ui_utilities.FriendInviteAdapter;
import com.ammonia.catchapp.ui_utilities.Item;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class InvitesActivity extends BaseActivity {
    private EventInviteAdapter mEventInviteAdapter;
    private FriendInviteAdapter mFriendInviteAdapter;

    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager2;
    private RecyclerView mEventRecyclerView;
    private RecyclerView mFriendRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
        UserProfile current_user = Factory.getFactory().getUser();

        ArrayList<Event> eventInvites = nm.getEventInvitesByUser(current_user);
        ArrayList<UserProfile> friendInvites = nm.getFriendInvitesByUser(current_user);

        if (eventInvites != null){
            mEventInviteAdapter = new EventInviteAdapter(this, new ArrayList<Item>(nm.getEventInvitesByUser(current_user)));
        }else{
            mEventInviteAdapter = new EventInviteAdapter(this, new ArrayList<Item>());
        }

        if (friendInvites != null){
            mFriendInviteAdapter = new FriendInviteAdapter(this, new ArrayList<Item>(nm.getFriendInvitesByUser(current_user)));
        }else{
            mFriendInviteAdapter = new FriendInviteAdapter(this, new ArrayList<Item>());
        }

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager2 = new LinearLayoutManager(this);

        mLayoutManager.setReverseLayout(true);
        mLayoutManager2.setReverseLayout(true);

        mLayoutManager.setStackFromEnd(true);
        mLayoutManager2.setStackFromEnd(true);

        mEventRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_event_invite_list);
        mFriendRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_friend_invite_list);

        mEventRecyclerView.setLayoutManager(mLayoutManager);
        mFriendRecyclerView.setLayoutManager(mLayoutManager2);

        mEventRecyclerView.setAdapter(mEventInviteAdapter);
        mFriendRecyclerView.setAdapter(mFriendInviteAdapter);
    }

    public int getLayoutResource(){
        return R.layout.activity_invites;
    }

    @Override
    public void refresh(){

    }
}
