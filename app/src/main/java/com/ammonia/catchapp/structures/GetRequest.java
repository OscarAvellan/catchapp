package com.ammonia.catchapp.structures;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Oscar on 3/10/2017.
 */

public class GetRequest extends AsyncTask<Pair, String, Boolean> {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    HttpURLConnection conn = null;
    BufferedReader reader = null;

    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected Boolean doInBackground(Pair... pairs) {

        Pair pair = (Pair) pairs[0];
        NetworkHandler networkManager = (NetworkHandler) ((Pair)pair.getSecond()).getFirst();
        String responseType = (String) ((Pair)pair.getSecond()).getSecond();

        try {
            conn = setConnParameters((String) pair.getFirst());
            conn.connect();

            int response_code = conn.getResponseCode();
            if(response_code == HttpURLConnection.HTTP_OK){
                InputStream inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                switch(responseType){
                    case "single": networkManager.setPayload(getsingleElementJSONarray());
                        break;
                    case "multiple": networkManager.setPayload(getMultipleObjects());
                        break;
                    case "jsonOBJECT": networkManager.setPayload(getsingleJsonObject());
                        break;
                }

                networkManager.setHttpError(response_code);

                return true;

            }
            else{

                networkManager.setPayload(null);
                networkManager.setHttpError(response_code);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    private HttpURLConnection setConnParameters(String s_url){

        HttpURLConnection conn = null;

        try {

            URL url = new URL( s_url );

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("GET");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return conn;
    }

    /*--------------------------------------------------------------------------------------------*/

    private String streamToString(BufferedReader reader){
        String srv_response;

        try{

            StringBuffer buffer = new StringBuffer();
            while((srv_response = reader.readLine()) != null){
                buffer.append(srv_response+'\n');
            }

            Log.i("Response Server: ",buffer.toString());
            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "FAIL";
    }

     /*--------------------------------------------------------------------------------------------*/

    /**
     * Used when the response of the server is a single element in a JSON array
     * Used for methods: getUserByEmail(),
     */
    private ArrayList<JSONObject> getsingleElementJSONarray(){
        ArrayList<JSONObject> listJobect = new ArrayList<>();
        try {
            JSONArray jarray = new JSONArray(streamToString(reader));
            JSONObject jobject;

            if(jarray.isNull(0)){
                jobject = new JSONObject().put("Error","Not Found");
            }
            else{
                jobject = jarray.getJSONObject(0);
                jobject.put("Error","None");
            }
            Log.i("jobject: ",jobject.toString());
            listJobect.add(jobject);

            Log.i("ArrayList",listJobect.get(0).toString());

            return listJobect;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

     /*--------------------------------------------------------------------------------------------*/

    /**
     * Used when the server's response is a JSON array containing multiple objects
     * Used for methods: getFriendsByUser()
     */
    private ArrayList<JSONObject> getMultipleObjects(){
        ArrayList<JSONObject> listJSON = new ArrayList<>();
        try {
            JSONArray jarray = new JSONArray(streamToString(reader));

            // Adding elements to ArrayList<JSONObject> list
            for(int i = 0; i < jarray.length(); i++){
                Log.i("array[i]",jarray.getJSONObject(i).toString());
                listJSON.add(jarray.getJSONObject(i));
            }
            return listJSON;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private ArrayList<JSONObject> getsingleJsonObject(){
        ArrayList<JSONObject> listJobect = new ArrayList<>();
        try {
            JSONObject jobject = new JSONObject(streamToString(reader));

            Log.i("jobject: ",jobject.toString());
            listJobect.add(jobject);

            Log.i("ArrayList",listJobect.get(0).toString());

            return listJobect;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
