package com.sam_chordas.android.stockhawk.service

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.data.QuoteColumns
import com.sam_chordas.android.stockhawk.data.QuoteProvider
import com.sam_chordas.android.stockhawk.model.Quote
import com.sam_chordas.android.stockhawk.ui.QUOTE_LIST_PROJECTION

class StockWidgetUpdateIntentService : IntentService("StockWidgetUpdateIntentService") {
    val TAG = StockWidgetUpdateIntentService::class.java.simpleName

    init {
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) when (intent.action) {
            ACTION_UPDATE_WIDGETS -> {
                for (appWidgetId in intent.getIntArrayExtra(EXTRA_WIDGET_IDS)) {
                    updateWidget(appWidgetId)
                }
            }
            ACTION_UPDATE_WIDGET -> {
                updateWidget(intent.getIntExtra(EXTRA_WIDGET_ID, Int.MIN_VALUE))
            }
            else -> Log.wtf(TAG, "Unexpected action: ${intent.action}")
        }
    }

    private fun updateWidget(widgetId: Int) {
        val quote = loadQuote(this, widgetId) ?: return
        val views = RemoteViews(this.packageName, R.layout.stock_app_widget)
        views.setTextViewText(R.id.stock_symbol, quote.symbol)
        views.setTextViewText(R.id.bid_price, quote.bidPrice)
        views.setTextViewText(R.id.change, quote.change) // TODO: 11/10/2016 Support percentage
        AppWidgetManager.getInstance(this).updateAppWidget(widgetId, views)
    }
}

val ACTION_UPDATE_WIDGETS = "com.sam_chordas.android.stockhawk.service.action.UPDATE_WIDGETS"
val ACTION_UPDATE_WIDGET = "com.sam_chordas.android.stockhawk.service.action.UPDATE_WIDGET"
val EXTRA_WIDGET_IDS = "com.sam_chordas.android.stockhawk.service.extra.WIDGET_IDS"
val EXTRA_WIDGET_ID = "com.sam_chordas.android.stockhawk.service.extra.WIDGET_ID"

fun requestUpdateWidgets(context: Context, widgetIds: IntArray) {
    val intent = Intent(context, StockWidgetUpdateIntentService::class.java)
    intent.action = ACTION_UPDATE_WIDGETS
    intent.putExtra(EXTRA_WIDGET_IDS, widgetIds)
    context.startService(intent)
}

fun requestUpdateWidget(context: Context, widgetId: Int) {
    val intent = Intent(context, StockWidgetUpdateIntentService::class.java)
    intent.action = ACTION_UPDATE_WIDGET
    intent.putExtra(EXTRA_WIDGET_ID, widgetId)
    context.startService(intent)
}

private val PREFS_NAME = "com.sam_chordas.android.stockhawk.ui.StockAppWidget"
private val PREF_PREFIX_KEY = "appwidget_"

/**
 * Write the prefix to the SharedPreferences object for this widget
 */
fun saveStockSymbolPref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

/**
 * Read the prefix from the SharedPreferences object for this widget.
 * If there is no preference saved, get the default from a resource
 */
fun loadStockSymbolPref(context: Context, appWidgetId: Int): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
}

fun loadQuote(context: Context, appWidgetId: Int): Quote? {
    val symbol = loadStockSymbolPref(context, appWidgetId) ?: return null
    val cursor = context.contentResolver.query(QuoteProvider.Quotes.CONTENT_URI,
            QUOTE_LIST_PROJECTION,
            QuoteColumns.ISCURRENT + " = ? AND " + QuoteColumns.SYMBOL + " = ?",
            arrayOf("1", symbol),
            null) ?: return null
    val quote: Quote?
    if (cursor.moveToFirst()) {
        val i = Quote.Indexes();
        i.set(cursor)
        quote = Quote()
        quote.set(cursor, i)
    } else quote = null
    cursor.close()
    return quote
}

fun deleteWidgetStockSymbolPref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}
