package com.automata.betsure;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FireMsgService extends FirebaseMessagingService {


    private static final String TAG = "VOLLEY ERROR";


    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Msg", "Message received [" + remoteMessage + "]");

        // Create Notification
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1410, notificationBuilder.build());
    }
}
