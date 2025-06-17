package com.nativegame.animalspop;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;

public class MyApplication extends Application {
    private AppOpenAdManager appOpenAdManager;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {
            // Initialization complete
        });

        appOpenAdManager = new AppOpenAdManager();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public AppOpenAdManager getAppOpenAdManager() {
        return appOpenAdManager;
    }
} 