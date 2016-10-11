package com.sam_chordas.android.stockhawk.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.sam_chordas.android.stockhawk.service.deleteWidgetStockSymbolPref
import com.sam_chordas.android.stockhawk.service.requestUpdateWidgets

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [StockAppWidgetConfigureActivity]
 */
class StockAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, widgetManager: AppWidgetManager, widgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        requestUpdateWidgets(context, widgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteWidgetStockSymbolPref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
