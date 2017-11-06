package com.ammonia.catchapp.ViewTypes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ui_utilities.BaseActivity;
import com.ammonia.catchapp.ui_utilities.ConversationAdapter;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.Conversation;
import com.ammonia.catchapp.structures.Message;
import com.ammonia.catchapp.ui_utilities.Item;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by morley on 24/08/17. Edited by David on 29/09/17.
 */

public class ConversationActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private ConversationAdapter mConversationAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        Conversation conversation = (Conversation) bundle.getSerializable("CONVERSATION");

        Log.i("this", conversation.toString());

        NetworkManagerInterface networkManager = Factory.getFactory().getNetworkManager();

        setToolbarTitle(conversation.getListString());

        ArrayList<Message> messages = networkManager.getMessagesByConversation(conversation);

        if(messages != null){
            Collections.sort(messages);
            Collections.reverse(messages);
            mConversationAdapter = new ConversationAdapter(this, new ArrayList<Item>(messages), conversation);
        }else{
            mConversationAdapter = new ConversationAdapter(this, new ArrayList<Item>(), conversation);
        }

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_message_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mConversationAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView r, int newState){
                if(mLayoutManager.findLastVisibleItemPosition() == (mConversationAdapter.getItemCount() - 1)){
                    mConversationAdapter.loadPreviousMessages();
                }
            }
        });
    }

    @Override
    public int getLayoutResource(){
        return R.layout.activity_conversation;
    }

    public void sendMessage (View view){

        EditText message = (EditText)findViewById(R.id.edit_text_chatbox);
        mConversationAdapter.sendMessage(message.getText().toString());
        message.setText("");
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }


    @Override
    public void refresh(){
        mConversationAdapter.refresh();
    }

}
