package com.fyp_android_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;
import java.util.Calendar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlarmReceiver extends BroadcastReceiver {

    GPS_Service gps;
    DatabaseReference mDatabaseLocationDetails;

    public AlarmReceiver(){
    }

    public AlarmReceiver(Context context){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.add(Calendar.SECOND, 10);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), 1000 * 60, alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        gps = new GPS_Service(context);
        context.startService(new Intent(context,GPS_Service.class));

        if(gps.canGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            mDatabaseLocationDetails = FirebaseDatabase.getInstance().getReference().child("Location_Details").push();
            storeInDatabase(latitude,longitude);
            Toast.makeText(context, latitude+" ::: "+ longitude, Toast.LENGTH_SHORT).show();
        }else{
            gps.showSettingsAlert();
        }
    }

    private void storeInDatabase(double latitude, double longitude) {
        mDatabaseLocationDetails.child("longitude").setValue(longitude);
        mDatabaseLocationDetails.child("latitude").setValue(latitude);
    }
}
