package com.nativegame.animalspop;

import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.ui.fragment.MenuFragment;
import com.nativegame.animalspop.level.MyLevelManager;
import com.nativegame.animalspop.sound.MySoundManager;
import com.nativegame.animalspop.timer.LivesTimer;
import com.nativegame.nattyengine.ui.GameActivity;

/**
 * Created by Oscar Liang on 2022/09/18
 */

/*
 *    MIT License
 *
 *    Copyright (c) 2022 Oscar Liang
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *    copies of the Software, and to permit persons to whom the Software is
 *    furnished to do so, subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in all
 *    copies or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *    SOFTWARE.
 */

public class MainActivity extends GameActivity {

    private DatabaseHelper mDatabaseHelper;
    private AdManager mAdManager;
    private LivesTimer mLivesTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AnimalsPop);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);
        setContainerId(R.id.container);
        setLevelManager(new MyLevelManager(this));
        setSoundManager(new MySoundManager(this));
        mDatabaseHelper = new DatabaseHelper(this);
        mAdManager = new AdManager(this);
        mLivesTimer = new LivesTimer(this);

        // Init the ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Show the menu fragment
        if (savedInstanceState == null) {
            navigateToFragment(new MenuFragment());
            // Start the menu bgm
            getSoundManager().loadMusic(R.raw.happy_and_joyful_children);
        }

        // Start periodic app open ads
        MyApplication.getInstance().getAppOpenAdManager().startPeriodicAds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Show app open ad when app is resumed
        MyApplication.getInstance().getAppOpenAdManager().showAdIfAvailable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop periodic app open ads
        MyApplication.getInstance().getAppOpenAdManager().stopPeriodicAds();
    }

    public DatabaseHelper getDatabaseHelper() {
        return mDatabaseHelper;
    }

    public AdManager getAdManager() {
        return mAdManager;
    }

    public LivesTimer getLivesTimer() {
        return mLivesTimer;
    }

}
