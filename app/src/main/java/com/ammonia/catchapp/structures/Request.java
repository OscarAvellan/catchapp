package com.ammonia.catchapp.structures;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ammonia.catchapp.ViewTypes.EventActivity;
import com.ammonia.catchapp.ViewTypes.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Oscar on 23/09/2017.
 */

public class Request extends AsyncTask<Triplet, String, Boolean> {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    HttpURLConnection conn = null;
    BufferedReader reader = null;

    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected Boolean doInBackground(Triplet... params){

        Triplet ttp1 = params[0];
        NetworkHandler networkManager= (NetworkHandler) ttp1.getnetworkManager();
        ArrayList<JSONObject> response = new ArrayList<>();

        try{

            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = setConnParameters((String) ttp1.geturl());

            JSONObject q = (JSONObject) ttp1.getjobject();
            String query = q.toString();
            Log.i("Query",query);

            // Open connection for sending data
            writeToStream(conn,query);

            conn.connect();

            int response_code = conn.getResponseCode();
            Log.i("ResponseCode", Integer.toString(response_code));

            if(response_code == HttpURLConnection.HTTP_OK  || response_code == HttpURLConnection.HTTP_CREATED
                    || response_code == HttpURLConnection.HTTP_ACCEPTED){
                // Get data from connection
                InputStream inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                response.add(new JSONObject(streamToString(reader)));

                networkManager.setHttpError(response_code);
                networkManager.setPayload(response);
                return true;

            }
            else{
                networkManager.setPayload(null);
                networkManager.setHttpError(response_code);
                return true;
            }

        }
        catch (JSONException | IOException e) { e.printStackTrace(); }

        finally{
            if (conn != null) { conn.disconnect(); }
            try{
                if(reader != null) { reader.close(); }
            }
            catch(IOException e2){ e2.printStackTrace(); }
        }

        return false;
    }

    /*--------------------------------------------------------------------------------------------*/

    private HttpURLConnection setConnParameters(String s_url){

        HttpURLConnection conn = null;

        try {

            URL url = new URL( s_url );

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestProperty( "Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return conn;
    }

    /*--------------------------------------------------------------------------------------------*/

    private void writeToStream(HttpURLConnection conn, String query) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();
    }

    /*--------------------------------------------------------------------------------------------*/

    private String streamToString(BufferedReader reader){
        String srv_response;

        try{
            StringBuffer buffer = new StringBuffer();

            while((srv_response = reader.readLine()) != null){
                buffer.append(srv_response+'\n');
            }

            Log.i("Testing",buffer.toString());
            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "FAIL";
    }

}


