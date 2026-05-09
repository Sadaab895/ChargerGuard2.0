package com.example.chargerguard;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class ClearTasksAccessibilityService extends AccessibilityService {

    // Yeh method tab call hogi jab koi screen event ho
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Abhi koi action nahi chahiye yahan
    }

    @Override
    public void onInterrupt() {
        // Service interrupt hone par
    }

    // Recents screen kholke sab apps clear karna
    public static void clearAllRecentApps(AccessibilityService service) {
        // Recents button press karo
        service.performGlobalAction(GLOBAL_ACTION_RECENTS);

        // Thoda wait karo (1 second)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // "Clear All" button dhundho aur press karo
        // Note: Yeh different phones par alag alag hota hai
        // Isliye global action use karte hain
    }
}
