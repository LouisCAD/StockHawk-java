package com.sam_chordas.android.stockhawk;

import android.app.Application;

import timber.log.Timber;

public class StockApp extends Application {
    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
    }
}
