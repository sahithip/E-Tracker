package com.e_grocery.teamsix.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.e_gro.teamsix.activity.R;
import com.e_grocery.teamsix.models.FoodItem;
import com.e_grocery.teamsix.sql.FoodItemDataSource;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	long foodItemID = intent.getLongExtra("foodItemID", -1);
    	

		FoodItemDataSource datasource = new FoodItemDataSource(context);
        datasource.open();
        
        FoodItem foodItem = datasource.getFoodItem(foodItemID);
       
        NotificationManager mNM;
        mNM = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        // Set the icon, scrolling text and timestamp
       
        Notification notification = new Notification(R.drawable.groceries, foodItem.getName() + " will expire soon",
        System.currentTimeMillis());
        
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, ShowListActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, foodItem.getName() + " will expire soon", context.getText(R.string.app_name), contentIntent);
        
        notification.defaults |= Notification.DEFAULT_SOUND;
        // Send the notification.
        // We use a layout id because it is a unique number. We use it later to cancel.
        mNM.notify(R.string.app_name, notification);
    }
}