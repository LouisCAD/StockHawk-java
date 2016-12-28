package com.sam_chordas.android.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import com.sam_chordas.android.stockhawk.service.StockWidgetUpdateIntentServiceKt;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {1, 1, 1},
   bv = {1, 0, 0},
   k = 1,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J \u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\bH\u0016¨\u0006\u000f"},
   d2 = {"Lcom/sam_chordas/android/stockhawk/ui/StockAppWidget;", "Landroid/appwidget/AppWidgetProvider;", "()V", "onDeleted", "", "context", "Landroid/content/Context;", "appWidgetIds", "", "onDisabled", "onEnabled", "onUpdate", "widgetManager", "Landroid/appwidget/AppWidgetManager;", "widgetIds", "production sources for module app"}
)
public final class StockAppWidget extends AppWidgetProvider {
   public void onUpdate(@NotNull Context context, @NotNull AppWidgetManager widgetManager, @NotNull int[] widgetIds) {
      Intrinsics.checkParameterIsNotNull(context, "context");
      Intrinsics.checkParameterIsNotNull(widgetManager, "widgetManager");
      Intrinsics.checkParameterIsNotNull(widgetIds, "widgetIds");
      StockWidgetUpdateIntentServiceKt.requestUpdateWidgets(context, widgetIds);
   }

   public void onDeleted(@NotNull Context context, @NotNull int[] appWidgetIds) {
      Intrinsics.checkParameterIsNotNull(context, "context");
      Intrinsics.checkParameterIsNotNull(appWidgetIds, "appWidgetIds");

      for(int var4 = 0; var4 < appWidgetIds.length; ++var4) {
         int appWidgetId = appWidgetIds[var4];
         StockWidgetUpdateIntentServiceKt.deleteWidgetStockSymbolPref(context, appWidgetId);
      }

   }

   public void onEnabled(@NotNull Context context) {
      Intrinsics.checkParameterIsNotNull(context, "context");
   }

   public void onDisabled(@NotNull Context context) {
      Intrinsics.checkParameterIsNotNull(context, "context");
   }
}
