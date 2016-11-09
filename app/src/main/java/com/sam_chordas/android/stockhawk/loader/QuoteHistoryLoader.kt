package com.sam_chordas.android.stockhawk.loader

import android.content.Context
import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse
import com.sam_chordas.android.stockhawk.model.QuoteResult
import com.sam_chordas.android.stockhawk.rest.getHistoryForMonths
import com.sam_chordas.android.stockhawk.rest.yqlApi
import retrofit2.Call

class QuoteHistoryLoader(context: Context, private val symbol: String)
    : RetrofitLoader<HistoricalQuotesDataResponse, MutableList<QuoteResult>>(context) {

    override val call: Call<HistoricalQuotesDataResponse>
        get() = yqlApi.getHistoryForMonths(symbol, 6)

    override fun transformResponse(body: HistoricalQuotesDataResponse): MutableList<QuoteResult> {
        return body.query.results.quotes
    }
}