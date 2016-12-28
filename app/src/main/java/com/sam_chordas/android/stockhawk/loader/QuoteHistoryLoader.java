package com.sam_chordas.android.stockhawk.loader;

import android.content.Context;

import com.sam_chordas.android.stockhawk.model.HistoricalQuotesDataResponse;
import com.sam_chordas.android.stockhawk.model.QuoteResult;
import com.sam_chordas.android.stockhawk.rest.YqlApiKt;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;

@Metadata(
   mv = {1, 1, 1},
   bv = {1, 0, 0},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0001B\u0015\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u000f\u001a\u00020\u0002H\u0016R\u001a\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u000b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lcom/sam_chordas/android/stockhawk/loader/QuoteHistoryLoader;", "Lcom/sam_chordas/android/stockhawk/loader/RetrofitLoader;", "Lcom/sam_chordas/android/stockhawk/model/HistoricalQuotesDataResponse;", "", "Lcom/sam_chordas/android/stockhawk/model/QuoteResult;", "context", "Landroid/content/Context;", "symbol", "", "(Landroid/content/Context;Ljava/lang/String;)V", "call", "Lretrofit2/Call;", "getCall", "()Lretrofit2/Call;", "transformResponse", "body", "production sources for module app"}
)
public final class QuoteHistoryLoader extends RetrofitLoader<HistoricalQuotesDataResponse, List<QuoteResult>> {
   private final String symbol;

   @NotNull
   public Call<HistoricalQuotesDataResponse> getCall() {
      return YqlApiKt.getHistoryForMonths(YqlApiKt.getYqlApi(), this.symbol, 6);
   }

   @NotNull
   public List<QuoteResult> transformResponse(@NotNull HistoricalQuotesDataResponse body) {
      Intrinsics.checkParameterIsNotNull(body, "body");
      List<QuoteResult> var2 = body.query.results.quotes;
      List<QuoteResult> $receiver = var2;
      CollectionsKt.reverse($receiver);
      List<QuoteResult> var10000 = var2;
      Intrinsics.checkExpressionValueIsNotNull((List)var2, "body.query.results.quotes.apply { reverse() }");
      return var10000;
   }

   public QuoteHistoryLoader(@NotNull Context context, @NotNull String symbol) {
      super(context);
      Intrinsics.checkParameterIsNotNull(context, "context");
      Intrinsics.checkParameterIsNotNull(symbol, "symbol");
      this.symbol = symbol;
   }
}
