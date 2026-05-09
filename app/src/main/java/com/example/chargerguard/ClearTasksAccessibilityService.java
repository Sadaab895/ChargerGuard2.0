package com.example.chargerguard;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class ClearTasksAccessibilityService extends AccessibilityService {

    public static ClearTasksAccessibilityService instance;

    @Override
    public void onServiceConnected() {
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    public static void clearAllApps() {
        if (instance == null) return;

        // Step 1: Recents screen kholo
        instance.performGlobalAction(GLOBAL_ACTION_RECENTS);

        // Step 2: 1.5 second baad Clear All button dhundho
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clickClearAll();
            }
        }, 1500);
    }

    private static void clickClearAll() {
        if (instance == null) return;

        AccessibilityNodeInfo root = instance.getRootInActiveWindow();
        if (root == null) return;

        // Alag alag phones mein alag text hota hai
        String[] clearTexts = {
            "Clear All", "CLEAR ALL", "Clear all",
            "Close All", "CLOSE ALL",
            "सभी साफ़ करें", "Clear", "CLEAR",
            "End All", "Remove All"
        };

        for (String text : clearTexts) {
            List<AccessibilityNodeInfo> nodes =
                root.findAccessibilityNodeInfosByText(text);
            if (nodes != null && !nodes.isEmpty()) {
                nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        }

        // Agar button nahi mila toh swipe karke clear karo
        instance.performGlobalAction(GLOBAL_ACTION_HOME);
    }
}
