package com.example.heatmap;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class YourService extends Service {

    private static final int backgroundNotifId = 1;
    private static final String backgroundChannelId = "Channel_Id";

    private long updateInterval = 100000; //milliseconds
    private String denseChannelId = "channel_id";
    private int denseNotifId = 0;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        // do your jobs here

        createNotificationChannel(denseChannelId);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    DataBase.logLocation(latitude, longitude);

                    densePopulationCheck(latitude, longitude);
                }
            }
        };

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(updateInterval);
        startLocationUpdates();

        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        int rgb = 75;
        rgb = (rgb << 8) + 46;
        rgb = (rgb << 8) + 131;
        createNotificationChannel(backgroundChannelId);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(backgroundNotifId, new NotificationCompat.Builder(this,
                backgroundChannelId) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running in background.")
                .setColor(rgb)
                .setContentIntent(pendingIntent)
                .build());
    }

    private void createNotificationChannel(String channelId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "channel0";
            String channelDescription = "channel_description";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("" + channelId, channelName, channelImportance);
            channel.setDescription(channelDescription);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void densePopulationCheck(double latitude, double longitude) {
        DataBase.getUsersInRange(latitude, longitude, 30, numUsers -> {
            if (numUsers > 30) {
                densePopulationNotification();
            }
        });
    }

    public void densePopulationNotification() {
        int rgb = 75;
        rgb = (rgb << 8) + 46;
        rgb = (rgb << 8) + 131;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, denseChannelId)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Densely populated area!")
                .setContentText("Lots of people around you! Please wear your mask.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(rgb)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(denseNotifId, builder.build());
        denseNotifId++;
    }

    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}