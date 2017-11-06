package com.ammonia.catchapp.ViewTypes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.UserAdapter;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.Item;

import java.util.ArrayList;

public class SearchFriendActivity extends BaseActivity {
    private UserAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private NetworkManagerInterface nm;
    private TextView searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Search a friend");

        searchKey = (TextView)findViewById(R.id.text_search_key);
        nm = Factory.getFactory().getNetworkManager();

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_message_list);


    }

    public void showResultsByEmail(View view){
        UserProfile returnedUser = nm.getUserByEmail(searchKey.getText().toString());
        ArrayList<UserProfile> returnedList = new ArrayList<UserProfile>();

        if (returnedUser != null) {
            returnedList.add(nm.getUserByEmail(searchKey.getText().toString()));
        }
        mAdapter = new UserAdapter(view.getContext(), new ArrayList<Item>(returnedList));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void showResultsByName(View view){
        ArrayList<UserProfile> returnedUser = nm.searchUser(searchKey.getText().toString());

        if (returnedUser != null) {
            mAdapter = new UserAdapter(view.getContext(), new ArrayList<Item>(returnedUser));
        }else{
            mAdapter = new UserAdapter(view.getContext(), new ArrayList<Item>());
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public int getLayoutResource(){
        return R.layout.activity_search_friend;
    }

    @Override
    public void refresh(){

    }
}
