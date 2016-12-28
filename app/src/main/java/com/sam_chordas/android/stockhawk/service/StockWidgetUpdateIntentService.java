package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.model.Quote;

import org.jetbrains.annotations.Nullable;

import kotlin.Metadata;
import kotlin.jvm.internal.IntCompanionObject;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {1, 1, 1},
   bv = {1, 0, 0},
   k = 1,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014J\u0010\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eH\u0002R\u0019\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u000f"},
   d2 = {"Lcom/sam_chordas/android/stockhawk/service/StockWidgetUpdateIntentService;", "Landroid/app/IntentService;", "()V", "TAG", "", "kotlin.jvm.PlatformType", "getTAG", "()Ljava/lang/String;", "onHandleIntent", "", "intent", "Landroid/content/Intent;", "updateWidget", "widgetId", "", "production sources for module app"}
)
public final class StockWidgetUpdateIntentService extends IntentService {
   private final String TAG = StockWidgetUpdateIntentService.class.getSimpleName();

   public final String getTAG() {
      return this.TAG;
   }

   protected void onHandleIntent(@Nullable Intent intent) {
      if(intent != null) {
         String var2 = intent.getAction();
         if(Intrinsics.areEqual(var2, StockWidgetUpdateIntentServiceKt.getACTION_UPDATE_WIDGETS())) {
            int[] var5 = intent.getIntArrayExtra(StockWidgetUpdateIntentServiceKt.getEXTRA_WIDGET_IDS());

            for(int var4 = 0; var4 < var5.length; ++var4) {
               int appWidgetId = var5[var4];
               this.updateWidget(appWidgetId);
            }
         } else if(Intrinsics.areEqual(var2, StockWidgetUpdateIntentServiceKt.getACTION_UPDATE_WIDGET())) {
            this.updateWidget(intent.getIntExtra(StockWidgetUpdateIntentServiceKt.getEXTRA_WIDGET_ID(), IntCompanionObject.MIN_VALUE));
         } else {
            Log.wtf(this.TAG, "Unexpected action: " + intent.getAction());
         }
      }

   }

   private final void updateWidget(int widgetId) {
      Quote var10000 = StockWidgetUpdateIntentServiceKt.loadQuote((Context)this, widgetId);
      if(var10000 != null) {
         Quote quote = var10000;
         RemoteViews views = new RemoteViews(this.getPackageName(), 2130968655);
         views.setTextViewText(2131624080, (CharSequence)quote.symbol);

         try {
            String bg = quote.change;
            float e = Float.parseFloat(bg);
            int bg1 = e < (float)0?2130837652:2130837651;
            views.setInt(2131624119, "setBackgroundResource", bg1);
         } catch (NumberFormatException var6) {
            Log.wtf(this.TAG, (Throwable)var6);
         }

         views.setTextViewText(2131624081, (CharSequence)quote.bidPrice);
         views.setTextViewText(2131624082, (CharSequence)quote.change);
         AppWidgetManager.getInstance((Context)this).updateAppWidget(widgetId, views);
      }
   }

   public StockWidgetUpdateIntentService() {
      super("StockWidgetUpdateIntentService");
      this.setIntentRedelivery(true);
   }
}
