package com.ammonia.catchapp.structures;

import org.json.JSONObject;

/**
 * Created by Oscar on 9/09/2017.
 */

public class  Triplet<JSONObject,String,NetworkHandler> {

    private JSONObject jobject;
    private String url;
    private NetworkHandler networkManager;

    Triplet(JSONObject jobject, String url,NetworkHandler networkManager){
        this.jobject = jobject;
        this.url = url;
        this.networkManager = networkManager;
    }

    public JSONObject getjobject(){
        return jobject;
    }

    public String geturl(){
        return url;
    }

    public NetworkHandler getnetworkManager(){
        return networkManager;
    }

    public void setjobject(JSONObject jobject){
        this.jobject = jobject;
    }

    public void seturl(String url){
        this.url = url;
    }

    public void setnetworkManager(NetworkHandler networkManager){
        this.networkManager = networkManager;
    }
}