package com.example.chargerguard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnEnableAccessibility, btnStart;
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility);
        btnStart = findViewById(R.id.btnStart);
        tvStatus = findViewById(R.id.tvStatus);

        updateStatus();

        // Accessibility Service enable button
        btnEnableAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accessibility settings open karo
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                Toast.makeText(MainActivity.this,
                    "ChargerGuard Task Cleaner ko enable karo", Toast.LENGTH_LONG).show();
            }
        });

        // App start button
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccessibilityEnabled()) {
                    Intent serviceIntent = new Intent(MainActivity.this, ChargerService.class);
                    startForegroundService(serviceIntent);
                    tvStatus.setText("✅ ChargerGuard Active hai!\nCharger lagao aur dekho.");
                    Toast.makeText(MainActivity.this, "App chal raha hai!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                        "Pehle Accessibility Service enable karo!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Check karo ki Accessibility Service enable hai ya nahi
    private boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (services != null) {
                return services.contains(getPackageName() + "/" +
                    ClearTasksAccessibilityService.class.getName());
            }
        }
        return false;
    }

    private void updateStatus() {
        if (isAccessibilityEnabled()) {
            tvStatus.setText("✅ Accessibility Ready\nStart button dabao!");
        } else {
            tvStatus.setText("⚠️ Accessibility Service enable karo pehle.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}
