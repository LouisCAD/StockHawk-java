package com.sam_chordas.android.stockhawk.ui

import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.db.chart.model.ChartSet
import com.db.chart.model.LineSet
import com.db.chart.view.ChartView.GridType.HORIZONTAL
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse
import com.sam_chordas.android.stockhawk.model.QuoteResult
import com.sam_chordas.android.stockhawk.rest.getHistoryForMonths
import com.sam_chordas.android.stockhawk.rest.yqlApi
import kotlinx.android.synthetic.main.activity_stock_details.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class StockDetailsActivity : AppCompatActivity(), Callback<HistoricalQuotesDataResponse>, TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val symbol = intent.getStringExtra(EXTRA_QUOTE_SYMBOL)
        if (symbol == null) {
            finish()
            return
        }
        setContentView(R.layout.activity_stock_details)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        tabLayout.apply {
            addTab(newTab().setText(R.string.fiveDays))
            addTab(newTab().setText(R.string.oneMonth))
            addTab(newTab().setText(R.string.threeMonths))
            addTab(newTab().setText(R.string.sixMonths))
            addOnTabSelectedListener(this@StockDetailsActivity)
        }
        yqlApi.getHistoryForMonths(symbol, 6).enqueue(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab) = Unit
    override fun onTabUnselected(tab: TabLayout.Tab) = Unit
    override fun onTabSelected(tab: TabLayout.Tab) = updateChart(when (tab.position) {
        0 -> last5BusinessDaysQuotes
        1 -> lastMonthQuotes
        2 -> last3MonthsQuotes
        else -> last6MonthsQuotes
    })

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

    override fun onResponse(call: Call<HistoricalQuotesDataResponse>, response: Response<HistoricalQuotesDataResponse>) {
        if (response.isSuccessful) {
            updateQuotes(response.body().query.results.quotes)
            updateChart(last5BusinessDaysQuotes)
            tabLayout.visibility = View.VISIBLE
        } else toast("request failed…")
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

    override fun onFailure(call: Call<HistoricalQuotesDataResponse>, t: Throwable) {
        Timber.e(t)
    }

    companion object {
        const val EXTRA_QUOTE_SYMBOL = "quote"
    }
}
