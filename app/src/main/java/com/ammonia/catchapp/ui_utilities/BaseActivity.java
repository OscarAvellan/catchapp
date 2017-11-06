package com.ammonia.catchapp.ui_utilities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ViewTypes.ConversationActivity;
import com.ammonia.catchapp.ViewTypes.EventActivity;
import com.ammonia.catchapp.ViewTypes.EventSettingsActivity;
import com.ammonia.catchapp.ViewTypes.InvitesActivity;
import com.ammonia.catchapp.ViewTypes.MainActivity;
import com.ammonia.catchapp.ViewTypes.SignInActivity;

/**
 *  Most of the other activities inherit this one so that they can abstract the Toolbar code and
 *  other common features.
 * */

public abstract class BaseActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        configureToolbar();
    }

    protected abstract int getLayoutResource();

    public abstract void refresh();

    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.accent), PorterDuff.Mode.SRC_ATOP);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // MainActivity does not get a back button
            if (this instanceof MainActivity){
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /* Allow the children classes to the set the Title in the Toolbar */
    public void setToolbarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        /* Add a Event Settings option if you are in an Event,
         * add a General Settings option if you are in the Main */
        if (this instanceof EventActivity){
            menu.clear();
            menu.add(0, 1, Menu.NONE, "Event Settings");
        } else if (this instanceof MainActivity){
            menu.clear();
            menu.add(0, 2, Menu.NONE, "General Settings");
            menu.add(0, 3, Menu.NONE, "Invites").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else if(this instanceof ConversationActivity){
            menu.clear();
            menu.add(0,4, Menu.NONE, "Refresh").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case 1: //Event Settings
                intent = new Intent(this, EventSettingsActivity.class);
                startActivity(intent);
                return true;

            case 2: //General Settings
                intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                return true;

            case 3: //Invites
                intent = new Intent(this, InvitesActivity.class);
                startActivity(intent);
                return true;

            case 4: //Refresh, this acts as the refresh button in the ConversationActivity
                refresh();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
