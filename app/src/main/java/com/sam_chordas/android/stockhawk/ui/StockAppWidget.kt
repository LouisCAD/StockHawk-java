package com.sam_chordas.android.stockhawk.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

import com.sam_chordas.android.stockhawk.R

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [StockAppWidgetConfigureActivity]
 */
class StockAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, widgetManager: AppWidgetManager, widgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in widgetIds) {
            updateAppWidget(context, widgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteWidgetTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                    appWidgetId: Int) {
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.stock_app_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
    return
    TODO("remove code above and update the widget with real data")
}

