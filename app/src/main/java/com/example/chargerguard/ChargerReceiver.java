package com.example.chargerguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

public class ChargerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            handleChargerConnected(context);
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            handleChargerDisconnected(context);
        }
    }

    private void handleChargerConnected(Context context) {
        turnOffWifi(context);
        clearBackgroundApps(context);
        showNotification(context,
            "🔋 Charger Connected",
            "WiFi off & background apps clear ho gaye!");
        Toast.makeText(context,
            "Charger laga! WiFi off & apps clear!",
            Toast.LENGTH_LONG).show();
    }

    private void handleChargerDisconnected(Context context) {
        showNotification(context,
            "🔌 Charger Removed",
            "Phone normal mode mein aa gaya.");
        Toast.makeText(context,
            "Charger hata diya!",
            Toast.LENGTH_SHORT).show();
    }

    private void turnOffWifi(Context context) {
        try {
            WifiManager wifiManager = (WifiManager)
                context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearBackgroundApps(Context context) {
        try {
            if (ClearTasksAccessibilityService.instance != null) {
                ClearTasksAccessibilityService.clearAllApps();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(Context context,
            String title, String message) {
        try {
            NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "charger_guard_channel";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    channelId, "ChargerGuard Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT);
                nm.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            nm.notify(1001, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
