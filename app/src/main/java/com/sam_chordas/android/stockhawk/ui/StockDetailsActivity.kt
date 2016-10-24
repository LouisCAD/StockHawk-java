package com.sam_chordas.android.stockhawk.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.db.chart.model.LineSet
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
            for((i, quote) in quotes.withIndex()) {
                values[i] = quote.closeValue
            }
            val lines = LineSet(labels, values)
            chart.addData(lines)
            chart.show()
        }
    }

    override fun onFailure(call: Call<HistoricalQuotesDataResponse>, t: Throwable) {
        Timber.e(t)
    }

    companion object {
        const val EXTRA_QUOTE_SYMBOL = "quote"
    }
}
