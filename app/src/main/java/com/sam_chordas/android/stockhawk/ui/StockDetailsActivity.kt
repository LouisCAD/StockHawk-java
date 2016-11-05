package com.sam_chordas.android.stockhawk.ui

import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.db.chart.model.LineSet
import com.db.chart.view.ChartView
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse
import com.sam_chordas.android.stockhawk.rest.buildHistoryQuery
import com.sam_chordas.android.stockhawk.rest.yqlApi
import kotlinx.android.synthetic.main.activity_stock_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class StockDetailsActivity : AppCompatActivity(), Callback<HistoricalQuotesDataResponse> {

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
        yqlApi.getHistoricalQuotesData(buildHistoryQuery(symbol, 1)).enqueue(this)
    }

    override fun onResponse(call: Call<HistoricalQuotesDataResponse>, response: Response<HistoricalQuotesDataResponse>) {
        Timber.i(response.message())
        if (response.isSuccessful) {
            val data = response.body()
            val quotes = data.query.results.quotes
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
            val minVal = min.roundDown()
            val maxVal = max.roundUp()
            val rows = (maxVal - minVal) / 5
            chart.setGrid(ChartView.GridType.HORIZONTAL, rows, 1, gridPaint)
            chart.addData(lines)
            chart.setAxisBorderValues(minVal, maxVal)
            chart.setStep(5)
            chart.show()
        }
    }

    private fun Float.roundUp(): Int {
        return (this.toInt() + 4) / 5 * 5
    }

    private fun Float.roundDown(): Int {
        val n = this.toInt()
        return n - n % 5
    }

    override fun onFailure(call: Call<HistoricalQuotesDataResponse>, t: Throwable) {
        Timber.e(t)
    }

    companion object {
        const val EXTRA_QUOTE_SYMBOL = "quote"
    }
}
