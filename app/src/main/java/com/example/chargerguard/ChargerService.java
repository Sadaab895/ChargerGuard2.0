package com.example.chargerguard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class ChargerService extends Service {

    private static final String CHANNEL_ID = "charger_service_channel";
    private ChargerReceiver chargerReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        // BroadcastReceiver register karo
        chargerReceiver = new ChargerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(chargerReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Foreground service start karo (background mein bhi kaam kare)
        startForeground(1000, buildNotification());
        return START_STICKY; // App band hone par bhi restart ho
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "ChargerGuard Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ChargerGuard Chal Raha Hai")
            .setContentText("Charger lagane ka wait kar raha hoon...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentIntent(pendingIntent)
            .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chargerReceiver != null) {
            unregisterReceiver(chargerReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
