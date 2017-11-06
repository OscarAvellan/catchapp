package com.ammonia.catchapp.ViewTypes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.structures.NetworkHandler;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.FriendCheckboxAdapter;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.MockNetworkManager;
import com.ammonia.catchapp.structures.UserProfile;
import com.ammonia.catchapp.ui_utilities.Item;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The recycler view needs an Adapter to bind the XML components to actual data
 */
public class SelectFriendsActivity extends BaseActivity {
    FriendCheckboxAdapter mFriendCheckboxAdapter;
    LinearLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    private ArrayList<UserProfile> currentSelectedItems = new ArrayList<UserProfile>();
    String status;
    ArrayList<UserProfile> eventMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        status = getIntent().getStringExtra("NEW_OR_EXISTING");

        eventMembers = (ArrayList<UserProfile>) bundle.getSerializable("EVENT_MEMBERS");

        for(UserProfile e : eventMembers){
            Log.i("members:", e.toString());
        }

        setToolbarTitle("Select members");

        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();

        ArrayList<UserProfile> friends = nm.getFriendsByUser(Factory.getFactory().getUser());

        for(UserProfile f : friends){
            Log.i("friends:", f.toString());
        }

        ArrayList<UserProfile> union;

        if(eventMembers.size() > 0) {
            union = new ArrayList<UserProfile>(eventMembers);
            union.addAll(friends);
            ArrayList<UserProfile> intersection = new ArrayList<UserProfile>(eventMembers);
            intersection.retainAll(friends);
            union.removeAll(intersection);
            union.remove(union.indexOf(Factory.getFactory().getUser()));
        } else{
            union = new ArrayList<UserProfile>(friends);
        }


        mFriendCheckboxAdapter = new FriendCheckboxAdapter(SelectFriendsActivity.this,
                new ArrayList<Item>(union),
                new FriendCheckboxAdapter.OnItemCheckListener() {

            @Override
            public void onItemCheck(UserProfile user) {
                currentSelectedItems.add(user);
            }

            @Override
            public void onItemUncheck(UserProfile user) {
                currentSelectedItems.remove(user);
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_friend_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mFriendCheckboxAdapter);
    }

    public int getLayoutResource(){
        return R.layout.activity_select_friends;
    }

    public void getList(View v) {
        Intent intent;

        if (status.equals("EXISTING")){
            Event curr_event = (Event) getIntent().getSerializableExtra("EVENT");
            NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
            UserProfile user = Factory.getFactory().getUser();
            for (UserProfile item : currentSelectedItems){
                Log.i("aya", "got here");
                nm.sendEventInvite(curr_event, user, item);
            }
            setResult(RESULT_OK);
            finish();


        }else{
            intent = new Intent(this, CreateEventActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("NEW_EVENT_MEMBERS", (Serializable) currentSelectedItems);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void refresh(){

    }

}
