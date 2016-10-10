package com.sam_chordas.android.stockhawk.ui

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.os.Bundle

fun getWidgetId(extras: Bundle?): Int {
    return if (extras != null) extras.getInt(EXTRA_APPWIDGET_ID) else INVALID_APPWIDGET_ID
}
