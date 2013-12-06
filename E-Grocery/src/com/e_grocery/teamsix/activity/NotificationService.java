package com.e_grocery.teamsix.activity;

import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.e_grocery.teamsix.models.FoodItem;

public class NotificationService {
    private Context context;
    private PendingIntent mAlarmSender;
    public NotificationService(Context context) {
        this.context = context;
    }

    public void scheduleExpiryWarning(Date date, FoodItem foodItem){
    	
    	Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("foodItemID", foodItem.getId());
        mAlarmSender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Date now = new Date();
        
        date.setHours(now.getHours());
        date.setMinutes(now.getMinutes());
        date.setSeconds(now.getSeconds());
        
        long notificationTime = date.getTime();    
        
        
        // Schedule the alarm!
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, notificationTime, mAlarmSender);
    }
}