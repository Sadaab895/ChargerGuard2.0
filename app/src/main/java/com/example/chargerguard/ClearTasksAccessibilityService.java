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

        instance.performGlobalAction(GLOBAL_ACTION_RECENTS);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clickClearAll();
            }
        }, 1500);
    }

    private static void clickClearAll() {
        if (instance == null) return;

        AccessibilityNodeInfo root =
            instance.getRootInActiveWindow();
        if (root == null) {
            instance.performGlobalAction(GLOBAL_ACTION_HOME);
            return;
        }

        String[] clearTexts = {
            "Clear All", "CLEAR ALL", "Clear all",
            "Close All", "CLOSE ALL", "Clear",
            "End All", "Remove All", "साफ़ करें",
            "सभी बंद करें", "Kapat", "Temizle"
        };

        for (String text : clearTexts) {
            List<AccessibilityNodeInfo> nodes =
                root.findAccessibilityNodeInfosByText(text);
            if (nodes != null && !nodes.isEmpty()) {
                nodes.get(0).performAction(
                    AccessibilityNodeInfo.ACTION_CLICK);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        instance.performGlobalAction(
                            GLOBAL_ACTION_HOME);
                    }
                }, 500);
                return;
            }
        }

        instance.performGlobalAction(GLOBAL_ACTION_HOME);
    }
}
