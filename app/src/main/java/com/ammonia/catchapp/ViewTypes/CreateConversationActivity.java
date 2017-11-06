package com.ammonia.catchapp.ViewTypes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.Item;
import com.ammonia.catchapp.ui_utilities.UserAdapter;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;

import java.util.ArrayList;

public class CreateConversationActivity extends BaseActivity {
    private UserAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private NetworkManagerInterface nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Search a friend");

        nm = Factory.getFactory().getNetworkManager();

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_friend_list);

        ArrayList<UserProfile> returnedFriends = nm.getFriendsByUser(Factory.getFactory().getUser());

        if (returnedFriends != null){
            mAdapter = new UserAdapter(this, new ArrayList<Item>(returnedFriends));
        }else{
            mAdapter = new UserAdapter(this, new ArrayList<Item>());
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    public void refresh(){

    }

    @Override
    public int getLayoutResource(){
        return R.layout.activity_create_conversation;
    }
}
