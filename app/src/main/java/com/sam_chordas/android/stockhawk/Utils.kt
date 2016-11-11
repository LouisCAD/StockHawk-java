package com.sam_chordas.android.stockhawk

import android.content.Context
import android.preference.PreferenceManager

inline fun Context.runOnce(uniqueKey: String, f: () -> Unit) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    val isFirstRun = prefs.getBoolean(uniqueKey, true)
    if (isFirstRun) {
        prefs.edit().putBoolean(uniqueKey, false).commit()
        f()
    }
}