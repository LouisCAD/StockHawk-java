package com.sam_chordas.android.stockhawk.rest

import android.support.annotation.IntRange
import com.github.aurae.retrofit2.LoganSquareConverterFactory
import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

private val YQL_ENDPOINT = "https://query.yahooapis.com/v1/public/"

val yqlApi: YqlApi = Retrofit.Builder()
        .baseUrl(YQL_ENDPOINT)
        .client(loggingClient())
        .addConverterFactory(LoganSquareConverterFactory.create())
        .build()
        .create(YqlApi::class.java)

private fun loggingClient() = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(BODY))
            .build()

interface YqlApi {
    /**
     * @param query Call [buildHistoryQuery] to get the formatted query to pass here.
     */
    @GET("yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
    fun getHistoricalQuotesData(@Query("q") query: String): Call<HistoricalQuotesDataResponse>
}

fun YqlApi.getHistoryForMonths(symbol: String, @IntRange(from = 0) months: Int) =
        getHistoricalQuotesData(buildHistoryQuery(symbol, months))

fun buildHistoryQuery(symbol: String, @IntRange(from = 0) months: Int): String {
    val startDate = Calendar.getInstance().apply {
        add(Calendar.MONTH, -months)
    }
    return buildHistoryQuery(symbol, startDate.time, Date())
}

fun buildHistoryQuery(symbol: String, startDate: Date, endDate: Date): String {
    val startDateStr = dateFormatter.format(startDate)
    val endDateStr = dateFormatter.format(endDate)
    return buildHistoryQuery(symbol, startDateStr, endDateStr)
}

private fun buildHistoryQuery(symbol: String, startDate: String, endDate: String): String {
    val historyTable = "yahoo.finance.historicaldata"
    val symbolCondition = "symbol = \"$symbol\""
    val dateCondition = "startDate = \"$startDate\" and endDate = \"$endDate\""
    return "select * from $historyTable where $symbolCondition and $dateCondition"
}

private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
