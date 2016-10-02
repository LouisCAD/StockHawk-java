package com.sam_chordas.android.stockhawk.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.sam_chordas.android.stockhawk.R
import kotlinx.android.synthetic.main.stock_app_widget_configure.*

/**
 * The configuration screen for the [StockAppWidget] AppWidget.
 */
class StockAppWidgetConfigureActivity : Activity(), View.OnClickListener {
    private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)
        setContentView(R.layout.stock_app_widget_configure)
        add_button.setOnClickListener(this)

        // Find the widget id from the intent.
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        appwidget_text.setText(loadTitlePref(this, mAppWidgetId))
    }

    override fun onClick(v: View) {
        // When the button is clicked, store the string locally
        val widgetText = appwidget_text.text.toString()
        saveTitlePref(this, mAppWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(this)
        updateAppWidget(this, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}

private val PREFS_NAME = "com.sam_chordas.android.stockhawk.ui.StockAppWidget"
private val PREF_PREFIX_KEY = "appwidget_"

/**
 * Write the prefix to the SharedPreferences object for this widget
 */
fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

/**
 * Read the prefix from the SharedPreferences object for this widget.
 * If there is no preference saved, get the default from a resource
 */
fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    if (titleValue != null) {
        return titleValue
    } else {
        return context.getString(R.string.appwidget_text)
    }
}

fun deleteWidgetTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}


