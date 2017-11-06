package com.ammonia.catchapp.ViewTypes;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Event;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.Item;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.services.GPS_Service;
import com.ammonia.catchapp.structures.NetworkHandler;
import com.ammonia.catchapp.structures.UserProfile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ammonia.catchapp.ui_utilities.Item.CONVERSATION;
import static com.ammonia.catchapp.ui_utilities.Item.EVENT;


public class MainActivity extends BaseActivity {
    private static final int NUM_ITEMS  = 3;
    private static final int EVENTS_TAB = 0;
    private static final int CONVERSATIONS_TAB = 1;
    private static final int FRIENDS_TAB = 2;

    SharedPreferences mPreferences;
    MyAdapter mAdapter;
    ViewPager mPager;
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check Location Permissions
        if(!runtime_permissions()){
            start_service();
        }

        //Instantiate network manager
        NetworkManagerInterface networkManager = new NetworkHandler();
        Factory.getFactory().setNetworkManager(networkManager);

        mPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);



        // Check if preferences for sign in are already set
        boolean signInExecuted = mPreferences.getBoolean("activity_executed", false);
        String useremail = mPreferences.getString("activity_useremail", "");

        if(signInExecuted && (!useremail.equals(""))){
            Log.i("Main","Log-in preferences already set");
            NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
            UserProfile user = nm.getUserByEmail(useremail);
            Factory.getFactory().setUser(user);

            Log.i("Main", "Set user in factory");
        }
      
        else if(!mPreferences.getBoolean("activity_executed", false) || useremail.equals("")){
            Log.i("Main", "Log-in preferences not yet set");

            SharedPreferences.Editor ed = mPreferences.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();

            Log.i("Main", "Opening sign in activity");
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(mPager, true);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        // Watch for tab items clicks
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // The floating action button does something different for every tab
                switch (position){
                    case EVENTS_TAB:
                        // Create an event
                        mFab.setImageResource(R.drawable.ic_add_black_24dp);
                        mFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(),
                                        SelectFriendsActivity.class);
                                Bundle bundle = new Bundle();
                                /* Give the SelectFriendsActivity an empty list so it knows that the
                                event to which members are being added is new */
                                bundle.putString("NEW_OR_EXISTING", "NEW");
                                bundle.putSerializable("EVENT_MEMBERS",
                                        new ArrayList<UserProfile>());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        break;
                    case CONVERSATIONS_TAB:
                        // Create a conversation
                        mFab.setImageResource(R.drawable.ic_message_black_24dp);
                        mFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), CreateConversationActivity.class);
                                startActivity(intent);
                            }
                        });

                        break;
                    case FRIENDS_TAB:
                        // Add a friend
                        mFab.setImageResource(R.drawable.ic_add_black_24dp);
                        mFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), SearchFriendActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                }


                mPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getLayoutResource(){
        return R.layout.activity_main_frag_pager;
    }
 
    /* ---------------------------------- FRAGMENTPAGERADAPTER ---------------------------------- */

    public static class MyAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[]{"Events", "Chats", "Friends"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

    }


    /* -------------------------------------- LISTFRAGMENT -------------------------------------- */


    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_main_frag_pager_list, container, false);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new InterfaceAdapter(getActivity(), android.R.layout.simple_list_item_1,
                    getDataSource(Factory.getFactory().getUser(), mNum)));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Intent intent = null;
            Item item = ((Item) (l.getAdapter().getItem(position)));
            String type = ((Item) (l.getAdapter().getItem(position))).getType();

            Log.i("FragmentList", "Item clicked: " +
                    (((Item) (l.getAdapter().getItem(position))).getType()));

            if (type == EVENT) {
                intent = new Intent(getActivity(), EventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("EVENT", (Serializable) item);
                intent.putExtras(bundle);
                startActivity(intent);

            }else if (type == CONVERSATION) {
                intent = new Intent(getActivity(), ConversationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("CONVERSATION", (Serializable) item);
                intent.putExtras(bundle);
                startActivity(intent);
              
            }else{
                intent = new Intent(getActivity(), FriendActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("FRIEND", (Serializable) item);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        }

        /**
         * Set the content in the list depending on which tab we are.
         */
        private ArrayList<Item> getDataSource(UserProfile user, int i){
            // The content of the ListView depends on which tab we are
            switch(i){
                case EVENTS_TAB:
                    ArrayList<Event> userEvents = user.getEvents();
                    if (userEvents != null){
                        return new ArrayList<Item>(user.getEvents());
                    }else {
                        return new ArrayList<Item>();
                    }

                case CONVERSATIONS_TAB:
                    ArrayList<Conversation> userConversations = user.getConversations();
                    if (userConversations != null){
                        return new ArrayList<Item>(user.getConversations());
                    }else {
                        return new ArrayList<Item>();
                    }

                case FRIENDS_TAB:
                    ArrayList<UserProfile> userFriends = user.getFriends();

                    if (userFriends != null){
                        return new ArrayList<Item>(user.getFriends());
                    }else {
                        return new ArrayList<Item>();
                    }
            }

            return null;
        }

    }

    /* -------------------------------------- ITEMADAPTER --------------------------------------- */

    public static class InterfaceAdapter extends ArrayAdapter<Item> {
        Context context;
      
        public InterfaceAdapter (Context context, int resource, List<Item> objects){
            super(context, resource, objects);
            this.context = context;
        }

        public int getCount() {
            return super.getCount();
        }

        public Item getItem(int index) {
            return super.getItem(index);
        }

        /**
         * ListView keep track of view off screen and reuse them for the others
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = LayoutInflater.from(context);

            if (convertView == null) {
                convertView = li.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position).getListString());
            return convertView;
        }
    }

    private boolean runtime_permissions(){
        if((Build.VERSION.SDK_INT >= 23) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED)){
            permissionRequest();
            return true;
        }
        return false;
    }

    @TargetApi(23)
    private void permissionRequest(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA},100);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                start_service();
            }else{
                runtime_permissions();
            }
        }
    }

    private void start_service() {
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
        Log.d("Service", "Started Service");
    }

    @Override
    public void refresh(){

    }

}