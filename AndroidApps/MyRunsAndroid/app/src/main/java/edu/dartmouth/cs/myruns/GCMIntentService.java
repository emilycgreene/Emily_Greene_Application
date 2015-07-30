package edu.dartmouth.cs.myruns;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {


    public GCMIntentService() {
        super("GcmIntentService");

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("intent", "service");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //handle send error in here
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d("delete","called");
                //handle delete message on server in here
                // TODO: handle deletes
                String message = (String) extras.get("id");
                long id = Long.valueOf(message);
                Intent i = new Intent();
                i.setAction("GCM_NOTIFY");
                i.putExtra("message", id);
                sendBroadcast(i);
                
                //HistoryFragment history = (HistoryFragment)



                // update the database
                // check if the history fragment is in focus and if it is call its oncreate
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // If it's a regular GCM message, do some work.
                String message = (String) extras.get("message");
                Intent i = new Intent();
                i.setAction("GCM_NOTIFY");
                i.putExtra("message", message);
                sendBroadcast(i);
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}