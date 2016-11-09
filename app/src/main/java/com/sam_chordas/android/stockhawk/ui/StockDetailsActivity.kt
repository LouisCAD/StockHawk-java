package com.sam_chordas.android.stockhawk.ui

import android.graphics.Paint
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.db.chart.model.ChartSet
import com.db.chart.model.LineSet
import com.db.chart.view.ChartView.GridType.HORIZONTAL
import com.google.android.agera.Result
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.loader.LoadListener
import com.sam_chordas.android.stockhawk.loader.QuoteHistoryLoader
import com.sam_chordas.android.stockhawk.model.QuoteResult
import kotlinx.android.synthetic.main.activity_stock_details.*
import timber.log.Timber

class StockDetailsActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private val symbol by lazy { intent.getStringExtra(EXTRA_QUOTE_SYMBOL)!! }

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_details)
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = symbol
        }
        tabLayout.apply {
            addNewTab(R.string.fiveDays)
            addNewTab(R.string.oneMonth)
            addNewTab(R.string.threeMonths)
            addNewTab(R.string.sixMonths)
        }
        savedInstanceState?.apply {
            tabLayout.getTabAt(getInt(KEY_SELECTED_TAB))!!.select()
        }
        supportLoaderManager.initLoader(LOADER_HISTORY, null, historyLoadCallback)
    }

    private val historyLoadCallback by lazy {
        LoadListener({ QuoteHistoryLoader(this, symbol) }) {
            loader: QuoteHistoryLoader, data: Result<MutableList<QuoteResult>> ->
            if (data.succeeded()) {
                snackbar?.dismiss()
                tabLayout.addOnTabSelectedListener(this)
                updateQuotes(data.get())
                updateChart(tabLayout.selectedTabPosition)
                tabLayout.visibility = View.VISIBLE
            } else if (data.isAbsent) {
                snackbar?.dismiss()
                snackbar = cl.snack(R.string.loading, LENGTH_INDEFINITE)
            } else {
                snackbar?.dismiss()
                snackbar = cl.snack(R.string.request_failed, LENGTH_INDEFINITE)
                val failReason = data.failureOrNull()
                if (failReason != null) Timber.wtf(data.failure)
            }
        }
    }

    private val KEY_SELECTED_TAB = "selectedTab"

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_TAB, tabLayout.selectedTabPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onTabReselected(tab: TabLayout.Tab) = Unit
    override fun onTabUnselected(tab: TabLayout.Tab) = Unit
    override fun onTabSelected(tab: TabLayout.Tab) = updateChart(tab.position)

    private fun quotesForTabPosition(position: Int) = when (position) {
        0 -> last5BusinessDaysQuotes
        1 -> lastMonthQuotes
        2 -> last3MonthsQuotes
        else -> last6MonthsQuotes
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun updateChart(tabPosition: Int) = updateChart(quotesForTabPosition(tabPosition))

    private fun updateChart(quotes: MutableList<QuoteResult>) {
        val step = if (quotes.size > 99) 10 else 5
        val labels = Array(quotes.size) { "" }
        val values = FloatArray(quotes.size)
        var max = quotes[0].closeValue
        var min = max
        for ((i, quote) in quotes.withIndex()) {
            val value = quote.closeValue
            values[i] = value
            if (value > max) max = value
            if (value < min) min = value
        }
        min -= 1; max += 1
        values.max()
        val lines = LineSet(labels, values).apply { color = colorRes(R.color.primary) }
        val gridPaint = Paint().apply { color = 0x2fffffff }
        val minVal = min.roundDown(step)
        val maxVal = max.roundUp(step)
        val rows = (maxVal - minVal) / step
        chart.apply {
            setGrid(HORIZONTAL, rows, 1, gridPaint)
            addData(arrayListOf<ChartSet>(lines))
            setStep(step)
            setAxisBorderValues(minVal, maxVal)
            show()
        }
    }

    private lateinit var last6MonthsQuotes: MutableList<QuoteResult>
    private lateinit var last3MonthsQuotes: MutableList<QuoteResult>
    private lateinit var lastMonthQuotes: MutableList<QuoteResult>
    private lateinit var last5BusinessDaysQuotes: MutableList<QuoteResult>

    private fun updateQuotes(quotes: MutableList<QuoteResult>) {
        last6MonthsQuotes = quotes
        last3MonthsQuotes = quotes.subList(0, quotes.size / 2)
        lastMonthQuotes = quotes.subList(0, quotes.size / 6)
        last5BusinessDaysQuotes = quotes.subList(0, 5)
    }

    private fun Float.roundUp(step: Int): Int = this.roundDown(step) + step
    private fun Float.roundDown(step: Int): Int {
        val n = this.toInt()
        return n - n % step
    }

    companion object {
        const val EXTRA_QUOTE_SYMBOL = "quote"
        private const val LOADER_HISTORY = 0
    }
}

fun TabLayout.addNewTab(@StringRes resId: Int) = addTab(newTab().setText(resId))
