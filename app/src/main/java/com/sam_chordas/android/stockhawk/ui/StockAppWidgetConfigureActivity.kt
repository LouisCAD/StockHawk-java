package com.sam_chordas.android.stockhawk.ui

import android.app.LoaderManager
import android.appwidget.AppWidgetManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.data.QuoteColumns
import com.sam_chordas.android.stockhawk.data.QuoteProvider
import com.sam_chordas.android.stockhawk.model.Quote
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter
import com.sam_chordas.android.stockhawk.service.requestUpdateWidget
import com.sam_chordas.android.stockhawk.service.saveStockSymbolPref
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.alert
import org.jetbrains.anko.include

/**
 * The configuration screen for the [StockAppWidget] AppWidget.
 */
class StockAppWidgetConfigureActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
        QuoteCursorAdapter.ViewHolder.Host {

    private val quotesAdapter by lazy { QuoteCursorAdapter(this, null) }

    private val mAppWidgetId by lazy { getWidgetId(intent.extras) }

    private var dialog: AlertDialogBuilder? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        loaderManager.initLoader(0, null, this)
        dialog = alert {
            title(R.string.add_widget)
            customView {
                include<RecyclerView>(R.layout.recyclerview) {
                    layoutManager = LinearLayoutManager(this@StockAppWidgetConfigureActivity)
                    adapter = quotesAdapter
                }
            }
            onCancel { finish() }
            cancelButton({ finish() })
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    override fun onClick(quote: Quote) {
        createWidget(quote)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                QUOTE_LIST_PROJECTION,
                QuoteColumns.ISCURRENT + " = ?",
                arrayOf("1"),
                null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        quotesAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        quotesAdapter.swapCursor(null)
    }

    fun createWidget(quote: Quote) {
        saveStockSymbolPref(this, mAppWidgetId, quote.symbol)
        requestUpdateWidget(this, mAppWidgetId)
        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}

val QUOTE_LIST_PROJECTION = arrayOf(
        QuoteColumns._ID,
        QuoteColumns.SYMBOL,
        QuoteColumns.BIDPRICE,
        QuoteColumns.PERCENT_CHANGE,
        QuoteColumns.CHANGE,
        QuoteColumns.ISUP)
