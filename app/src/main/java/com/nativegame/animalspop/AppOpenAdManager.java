package com.nativegame.animalspop;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class AppOpenAdManager {
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"; // Test ad unit ID
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private Handler handler;
    private Runnable showAdRunnable;
    private static final long AD_INTERVAL = 120000; // 2 minutes in milliseconds

    public AppOpenAdManager() {
        handler = new Handler(Looper.getMainLooper());
        showAdRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isShowingAd) {
                    loadAd(null);
                }
                // Schedule next ad
                handler.postDelayed(this, AD_INTERVAL);
            }
        };
    }

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private long loadTime = 0;

    /** Check if ad was loaded more than n hours ago. */
    private boolean wasLoadTimeLessThanNHoursAgo() {
        long dateDifference = System.currentTimeMillis() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * (long) 4));
    }

    /** Check if ad exists and can be shown. */
    private boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo();
    }

    /** Load an ad. */
    public void loadAd(Context context) {
        // Don't load ad if there's an existing ad or already loading one.
        if (isLoadingAd || isAdAvailable()) {
            return;
        }

        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(
                context,
                AD_UNIT_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                    }
                });
    }

    /** Show the ad if one isn't already showing. */
    public void showAdIfAvailable(@NonNull final Activity activity) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            return;
        }

        // If the ad is not available, do not show the ad.
        if (!isAdAvailable()) {
            loadAd(activity);
            return;
        }

        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null;
                        isShowingAd = false;
                        loadAd(activity);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        appOpenAd = null;
                        isShowingAd = false;
                        loadAd(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                });

        appOpenAd.show(activity);
    }

    /** Start showing ads periodically */
    public void startPeriodicAds() {
        handler.postDelayed(showAdRunnable, AD_INTERVAL);
    }

    /** Stop showing ads periodically */
    public void stopPeriodicAds() {
        handler.removeCallbacks(showAdRunnable);
    }
} 