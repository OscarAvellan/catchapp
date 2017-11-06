package com.ammonia.catchapp.firecloudmessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ammonia.catchapp.ViewTypes.ConversationActivity;
import com.ammonia.catchapp.ViewTypes.EventActivity;
import com.ammonia.catchapp.ViewTypes.FriendActivity;
import com.ammonia.catchapp.R;
import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.structures.UserProfile;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class CatchAppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingLog";
    private BroadcastReceiver locationReceiver;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //handleNow();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        JSONObject data = new JSONObject(remoteMessage.getData());

        // Create notification UI in the device
        try{
            String notificationType = data.getString("type");

            if(notificationType.equals("PRIVATE_MESSAGE")){
                createMessageNotification(data);
            }
            else if(notificationType.equals("EVENT_MESSAGE")){
                createMessageNotification(data);
            }
            else if(notificationType.equals("FRIEND_INVITE")){
                createFriendInviteNotification(data);
            }
            else if(notificationType.equals("EVENT_INVITE")){
                createEventInviteNotification(data);
            }else if(notificationType.equals("LOCATION_REQUEST")){
                //registerLocationRequest();
                Factory currentFactory = Factory.getFactory();
                UserProfile myUser = currentFactory.getUser();
                Log.d("Service", "Location Request");
                if(currentFactory.getLastLocation() != null) {
                    Log.d("Service", "Location Send");
                    Factory.getFactory().getNetworkManager().updateUserLocation(myUser, currentFactory.getLastLocation());
                }
            }
        }
        catch (JSONException e){
            Log.e("jsonexceptionfcmmsg", "jsonexcep");
        }
    }


    /* Create appropriate notifications for the type of message received */

    private void createMessageNotification(JSONObject data) throws JSONException{
        // change SignInActivity.class to appropriate view that should open depending on the message
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        buildNotification("New message from " + data.getString("sender"), "", pendingIntent);
    }

    private void createFriendInviteNotification(JSONObject data) throws JSONException{
        // change SignInActivity.class to appropriate view that should open depending on the message
        Intent intent = new Intent(this, FriendActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        buildNotification("New friend invite from " + data.getString("sender"), "", pendingIntent);
    }

    private void createEventInviteNotification(JSONObject data) throws JSONException{
        // change SignInActivity.class to appropriate view that should open depending on the message
        Intent intent = new Intent(this, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        buildNotification("New event invite from " + data.getString("sender"), "", pendingIntent);
    }

    /* Helper function to create actual notification UI */
    private void buildNotification(String title, String text, PendingIntent pendingIntent){
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_catchapp)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //notify(unique notif ID; if there are the same the prev one gets changed, notifaction)
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void registerLocationRequest(){
        if(locationReceiver == null){
            locationReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("Service", "Received");
                    Log.d("Service", ((LocationResult)intent.getExtras().get("locationResult")).toString());
                    if((boolean)intent.getExtras().get("isEnabled")) {
                        LocationResult locationResult =
                                (LocationResult)intent.getExtras().get("locationResult");
                        Location lastLocation = locationResult.getLastLocation();
                        Factory factory = Factory.getFactory();
                        UserProfile user = factory.getUser();
                        factory.getNetworkManager().updateUserLocation(user, lastLocation);
                        unregisterReceiver(this);
                        locationReceiver = null;
                    }
                }
            };
        }
        registerReceiver(locationReceiver, new IntentFilter("location_update"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationReceiver != null) {
            unregisterReceiver(locationReceiver);
        }
    }
}