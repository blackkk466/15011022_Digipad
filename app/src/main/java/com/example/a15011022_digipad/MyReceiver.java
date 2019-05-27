package com.example.a15011022_digipad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MyReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "digipad_1";
    private String title,content;
    private long id;


    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        //get the data that comes from NewNote via intent and alarm
        try{
            title = bundle.getString("title");
            content = bundle.getString("content");
            id = bundle.getLong("id");
            Log.d("***id",String.valueOf(id));
        }catch (Exception e){
            e.printStackTrace();
            Log.e("BUNDLE_SEND_ERROR",e.getMessage());
        }


        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notifications";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //get the default notification ringtone
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //create NotificationManager to be able to use the service
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //intent that will hold the data to pass it to next activity when the notification is activated
        Intent i = new Intent(context, NoteDetail.class);
        i.putExtra("NOTID",id);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //pending intent to show notification
        PendingIntent pIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);


        //configure the notification with data and some options
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_h)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pIntent)
                .setSound(sound)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        //show the notification
        nm.notify(1, builder.build());
    }

}
