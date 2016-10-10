package com.sam_chordas.android.stockhawk.ui

import android.app.LoaderManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.data.QuoteColumns
import com.sam_chordas.android.stockhawk.data.QuoteProvider
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter
import kotlinx.android.synthetic.main.stock_app_widget_configure.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.include

/**
 * The configuration screen for the [StockAppWidget] AppWidget.
 */
class StockAppWidgetConfigureActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener {

    private val quotesAdapter by lazy { QuoteCursorAdapter(this, null) }

    private val mAppWidgetId by lazy { getWidgetId(intent.extras) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        loaderManager.initLoader(0, null, this)
        alert {
            title(R.string.add_widget)
            customView {
                include<RecyclerView>(R.layout.recyclerview) {
                    layoutManager = LinearLayoutManager(this@StockAppWidgetConfigureActivity)
                    adapter = quotesAdapter
                }
            }
            onCancel { finish() }
            cancelButton({ finish() })
            positiveButton(R.string.create_widget, { createWidget() })
        }.show()
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        quotesAdapter.swapCursor(null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        quotesAdapter.swapCursor(data)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val projection = arrayOf(
                QuoteColumns._ID,
                QuoteColumns.SYMBOL,
                QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE,
                QuoteColumns.CHANGE,
                QuoteColumns.ISUP)
        return CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                projection,
                QuoteColumns.ISCURRENT + " = ?",
                arrayOf("1"),
                null)
    }

    fun createWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        // It is the responsibility of the configuration activity to update the app widget
        updateAppWidget(this, appWidgetManager, mAppWidgetId)
        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    override fun onClick(v: View) {
        // When the button is clicked, store the string locally
        val widgetText = appwidget_text.text.toString()
        saveTitlePref(this, mAppWidgetId, widgetText)

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
