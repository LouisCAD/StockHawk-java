package com.sam_chordas.android.stockhawk.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;

import com.sam_chordas.android.stockhawk.data.QuoteProvider.Quotes;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.Quote.Indexes;
import com.sam_chordas.android.stockhawk.ui.StockAppWidget;
import com.sam_chordas.android.stockhawk.ui.StockAppWidgetConfigureActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
        mv = {1, 1, 1},
        bv = {1, 0, 0},
        k = 2,
        d1 = {"\u0000,\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0015\n\u0002\b\u0003\u001a\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011\u001a\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011\u001a\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011\u001a\u000e\u0010\u0015\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f\u001a\u0016\u0010\u0016\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0011\u001a\u0016\u0010\u0018\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001a\u001a\u001e\u0010\u001b\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u0001\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u0014\u0010\u0004\u001a\u00020\u0001X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0003\"\u0014\u0010\u0006\u001a\u00020\u0001X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0003\"\u0014\u0010\b\u001a\u00020\u0001X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0003\"\u000e\u0010\n\u001a\u00020\u0001X\u0082D¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\u0001X\u0082D¢\u0006\u0002\n\u0000¨\u0006\u001d"},
        d2 = {"ACTION_UPDATE_WIDGET", "", "getACTION_UPDATE_WIDGET", "()Ljava/lang/String;", "ACTION_UPDATE_WIDGETS", "getACTION_UPDATE_WIDGETS", "EXTRA_WIDGET_ID", "getEXTRA_WIDGET_ID", "EXTRA_WIDGET_IDS", "getEXTRA_WIDGET_IDS", "PREFS_NAME", "PREF_PREFIX_KEY", "deleteWidgetStockSymbolPref", "", "context", "Landroid/content/Context;", "appWidgetId", "", "loadQuote", "Lcom/sam_chordas/android/stockhawk/model/Quote;", "loadStockSymbolPref", "requestUpdateAllWidgets", "requestUpdateWidget", "widgetId", "requestUpdateWidgets", "widgetIds", "", "saveStockSymbolPref", "text", "production sources for module app"}
)
public final class StockWidgetUpdateIntentServiceKt {
    @NotNull
    private static final String ACTION_UPDATE_WIDGETS = "com.sam_chordas.android.stockhawk.service.action.UPDATE_WIDGETS";
    @NotNull
    private static final String ACTION_UPDATE_WIDGET = "com.sam_chordas.android.stockhawk.service.action.UPDATE_WIDGET";
    @NotNull
    private static final String EXTRA_WIDGET_IDS = "com.sam_chordas.android.stockhawk.service.extra.WIDGET_IDS";
    @NotNull
    private static final String EXTRA_WIDGET_ID = "com.sam_chordas.android.stockhawk.service.extra.WIDGET_ID";
    private static final String PREFS_NAME = "com.sam_chordas.android.stockhawk.ui.StockAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    @NotNull
    public static final String getACTION_UPDATE_WIDGETS() {
        return ACTION_UPDATE_WIDGETS;
    }

    @NotNull
    public static final String getACTION_UPDATE_WIDGET() {
        return ACTION_UPDATE_WIDGET;
    }

    @NotNull
    public static final String getEXTRA_WIDGET_IDS() {
        return EXTRA_WIDGET_IDS;
    }

    @NotNull
    public static final String getEXTRA_WIDGET_ID() {
        return EXTRA_WIDGET_ID;
    }

    public static final void requestUpdateWidgets(@NotNull Context context, @NotNull int[] widgetIds) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(widgetIds, "widgetIds");
        Intent intent = new Intent(context, StockWidgetUpdateIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        intent.putExtra(EXTRA_WIDGET_IDS, widgetIds);
        context.startService(intent);
    }

    public static final void requestUpdateAllWidgets(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, StockAppWidget.class));
        Intrinsics.checkExpressionValueIsNotNull(ids, "ids");
        requestUpdateWidgets(context, ids);
    }

    public static final void requestUpdateWidget(@NotNull Context context, int widgetId) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intent intent = new Intent(context, StockWidgetUpdateIntentService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(intent);
    }

    public static final void saveStockSymbolPref(@NotNull Context context, int appWidgetId, @NotNull String text) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    @Nullable
    public static final String loadStockSymbolPref(@NotNull Context context, int appWidgetId) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId, (String) null);
    }

    @Nullable
    public static final Quote loadQuote(@NotNull Context context, int appWidgetId) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        String var10000 = loadStockSymbolPref(context, appWidgetId);
        if (var10000 != null) {
            String symbol = var10000;
            ContentResolver var12 = context.getContentResolver();
            Uri var10001 = Quotes.CONTENT_URI;
            String[] var10002 = StockAppWidgetConfigureActivity.QUOTE_LIST_PROJECTION;
            String[] quote = new String[]{"1", symbol};
            String var9 = "is_current = ? AND symbol = ?";
            String[] var8 = var10002;
            Uri var7 = var10001;
            ContentResolver var6 = var12;
            Object[] var10 = (Object[]) quote;
            Cursor var13 = var6.query(var7, var8, var9, (String[]) var10, (String) null);
            if (var13 != null) {
                Cursor cursor = var13;
                Quote quote1;
                if (cursor.moveToFirst()) {
                    Indexes i = new Indexes();
                    i.set(cursor);
                    quote1 = new Quote();
                    quote1.set(cursor, i);
                } else {
                    quote1 = (Quote) null;
                }

                cursor.close();
                return quote1;
            } else {
                return (Quote) null;
            }
        } else {
            return (Quote) null;
        }
    }

    public static final void deleteWidgetStockSymbolPref(@NotNull Context context, int appWidgetId) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }
}