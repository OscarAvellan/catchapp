package com.ammonia.catchapp.ViewTypes;

import android.os.Bundle;

import com.ammonia.catchapp.R;
import com.ammonia.catchapp.ui_utilities.BaseActivity;

public class EventSettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("Event Settings");
    }

    public int getLayoutResource(){
        return R.layout.activity_event_settings;
    }

    @Override
    public void refresh(){

    }
}

