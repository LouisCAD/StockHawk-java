package com.sam_chordas.android.stockhawk.rest;

import android.support.annotation.IntRange;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;
import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Retrofit;

@Metadata(
        mv = {1, 1, 1},
        bv = {1, 0, 0},
        k = 2,
        d1 = {"\u00006\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b\u001a\u0018\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00012\b\b\u0001\u0010\r\u001a\u00020\u000e\u001a \u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u0001H\u0002\u001a\u0010\u0010\u000f\u001a\n \u0011*\u0004\u0018\u00010\u00100\u0010H\u0002\u001a\"\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013*\u00020\u00052\u0006\u0010\t\u001a\u00020\u00012\b\b\u0001\u0010\r\u001a\u00020\u000e\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082D¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0015"},
        d2 = {"YQL_ENDPOINT", "", "dateFormatter", "Ljava/text/SimpleDateFormat;", "yqlApi", "Lcom/sam_chordas/android/stockhawk/rest/YqlApi;", "getYqlApi", "()Lcom/sam_chordas/android/stockhawk/rest/YqlApi;", "buildHistoryQuery", "symbol", "startDate", "Ljava/util/Date;", "endDate", "months", "", "loggingClient", "Lokhttp3/OkHttpClient;", "kotlin.jvm.PlatformType", "getHistoryForMonths", "Lretrofit2/Call;", "Lcom/sam_chordas/android/stockhawk/model/HistoricalQuotesDataResponse;", "production sources for module app"}
)
public final class YqlApiKt {
    private static final String YQL_ENDPOINT = "https://query.yahooapis.com/v1/public/";
    @NotNull
    private static final YqlApi yqlApi;
    private static final SimpleDateFormat dateFormatter;

    @NotNull
    public static YqlApi getYqlApi() {
        return yqlApi;
    }

    private static OkHttpClient loggingClient() {
        return (new Builder()).addInterceptor((Interceptor) (new HttpLoggingInterceptor()).setLevel(Level.BODY)).build();
    }

    @NotNull
    public static Call<HistoricalQuotesDataResponse> getHistoryForMonths(@NotNull YqlApi $receiver, @NotNull String symbol, @IntRange(from = 0L) int months) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(symbol, "symbol");
        return $receiver.getHistoricalQuotesData(buildHistoryQuery(symbol, months));
    }

    @NotNull
    public static String buildHistoryQuery(@NotNull String symbol, @IntRange(from = 0L) int months) {
        Intrinsics.checkParameterIsNotNull(symbol, "symbol");
        Calendar var3 = Calendar.getInstance();
        Calendar $receiver = (Calendar) var3;
        $receiver.add(Calendar.MONTH, -months);
        Calendar startDate = (Calendar) var3;
        Date var10001 = startDate.getTime();
        Intrinsics.checkExpressionValueIsNotNull(var10001, "startDate.time");
        return buildHistoryQuery(symbol, var10001, new Date());
    }

    @NotNull
    public static String buildHistoryQuery(@NotNull String symbol, @NotNull Date startDate, @NotNull Date endDate) {
        Intrinsics.checkParameterIsNotNull(symbol, "symbol");
        Intrinsics.checkParameterIsNotNull(startDate, "startDate");
        Intrinsics.checkParameterIsNotNull(endDate, "endDate");
        String startDateStr = dateFormatter.format(startDate);
        String endDateStr = dateFormatter.format(endDate);
        Intrinsics.checkExpressionValueIsNotNull(startDateStr, "startDateStr");
        Intrinsics.checkExpressionValueIsNotNull(endDateStr, "endDateStr");
        return buildHistoryQuery(symbol, startDateStr, endDateStr);
    }

    private static String buildHistoryQuery(String symbol, String startDate, String endDate) {
        String historyTable = "yahoo.finance.historicaldata";
        String symbolCondition = "symbol = " + "\"" + symbol + "\"";
        String dateCondition = "startDate = " + "\"" + startDate + "\"" + " and endDate = " + "\"" + endDate + "\"";
        return "select * from " + historyTable + " where " + symbolCondition + " and " + dateCondition;
    }

    static {
        YqlApi api = new Retrofit.Builder()
                .baseUrl(YQL_ENDPOINT)
                .client(loggingClient())
                .addConverterFactory(LoganSquareConverterFactory.create()).build()
                .create(YqlApi.class);
        Intrinsics.checkExpressionValueIsNotNull(api, "Retrofit.Builder()\n     …reate(YqlApi::class.java)");
        yqlApi = api;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }
}