package com.ammonia.catchapp.firecloudmessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ammonia.catchapp.factory.Factory;
import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.structures.UserProfile;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class CatchAppFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIID";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences mPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = mPreferences.edit();
        ed.putString("activity_firebasetoken", refreshedToken);
        ed.commit();

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        NetworkManagerInterface nm = Factory.getFactory().getNetworkManager();
        UserProfile user =  Factory.getFactory().getUser();
        if(user == null)
            return;
        nm.updateUserFirebaseToken(user,token);
        Log.d("HERE MY TOKEN: ", "MY TOKEN: " + token);
    }
}