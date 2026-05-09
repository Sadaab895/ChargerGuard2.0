package com.example.chargerguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.app.ActivityManager;
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
            // ✅ Charger lagaya gaya!
            handleChargerConnected(context);

        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            // ❌ Charger hataya gaya
            handleChargerDisconnected(context);
        }
    }

    private void handleChargerConnected(Context context) {
        // 1. WiFi off karo
        turnOffWifi(context);

        // 2. Background apps clear karo
        clearBackgroundApps(context);

        // 3. Notification dikhao
        showNotification(context,
            "🔋 Charger Connected",
            "WiFi off & background apps clear ho gaye. Battery safe hai!");

        Toast.makeText(context, "Charger laga! WiFi off & apps clear!", Toast.LENGTH_LONG).show();
    }

    private void handleChargerDisconnected(Context context) {
        // WiFi wapas on karo (optional)
        // turnOnWifi(context);

        showNotification(context,
            "🔌 Charger Removed",
            "Phone normal mode mein aa gaya.");

        Toast.makeText(context, "Charger hata diya!", Toast.LENGTH_SHORT).show();
    }

    // WiFi Off karna
    private void turnOffWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
            .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Android 9 aur usse purane phones ke liye
                wifiManager.setWifiEnabled(false);
            } else {
                // Android 10+ ke liye Settings panel kholna padega
                // (System restriction hai, direct off nahi ho sakta)
                Intent panelIntent = new Intent(android.provider.Settings.Panel.ACTION_WIFI);
                panelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(panelIntent);
            }
        }
    }

    // Background Apps Clear karna
    private void clearBackgroundApps(Context context) {
        ActivityManager activityManager = (ActivityManager)
            context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            // Running apps ki list lo
            for (ActivityManager.RunningAppProcessInfo appProcess :
                    activityManager.getRunningAppProcesses()) {

                // Sirf background apps band karo (apna app nahi)
                if (appProcess.importance ==
                        ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {

                    if (!appProcess.processName.equals(context.getPackageName())) {
                        activityManager.killBackgroundProcesses(appProcess.processName);
                    }
                }
            }
        }
    }

    // Notification dikhana
    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "charger_guard_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                channelId,
                "ChargerGuard Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        notificationManager.notify(1001, builder.build());
    }
}
