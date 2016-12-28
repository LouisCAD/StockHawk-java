package com.sam_chordas.android.stockhawk.rest;


import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@Metadata(
        mv = {1, 1, 1},
        bv = {1, 0, 0},
        k = 1,
        d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\'¨\u0006\u0007"},
        d2 = {"Lcom/sam_chordas/android/stockhawk/rest/YqlApi;", "", "getHistoricalQuotesData", "Lretrofit2/Call;", "Lcom/sam_chordas/android/stockhawk/model/HistoricalQuotesDataResponse;", "query", "", "production sources for module app"}
)
public interface YqlApi {
    @GET("yql?format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
    @NotNull
    Call<HistoricalQuotesDataResponse> getHistoricalQuotesData(@Query("q") @NotNull String var1);
}
