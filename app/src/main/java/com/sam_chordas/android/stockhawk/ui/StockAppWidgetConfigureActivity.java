package com.sam_chordas.android.stockhawk.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static com.sam_chordas.android.stockhawk.service.StockWidgetUpdateIntentServiceKt.requestUpdateWidget;
import static com.sam_chordas.android.stockhawk.service.StockWidgetUpdateIntentServiceKt.saveStockSymbolPref;

public final class StockAppWidgetConfigureActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, QuoteCursorAdapter.ViewHolder.Host {

    private int mAppWidgetId;

    private AlertDialog mDialog;
    private QuoteCursorAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        mAppWidgetId = getWidgetId(getIntent().getExtras());
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        mAdapter = new QuoteCursorAdapter(this, null);
        getSupportLoaderManager().initLoader(0, null, this);
        mDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_widget)
                .setView(R.layout.recyclerview)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                .create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                RecyclerView rv = (RecyclerView) mDialog.findViewById(R.id.recycler_view);
                assert rv != null;
                rv.setLayoutManager(new LinearLayoutManager(StockAppWidgetConfigureActivity.this));
                rv.setAdapter(mAdapter);
            }
        });
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDialog.dismiss();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                QUOTE_LIST_PROJECTION,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(@NonNull Quote quote) {
        createWidget(quote);
    }

    private void createWidget(Quote quote) {
        saveStockSymbolPref(this, mAppWidgetId, quote.symbol);
        requestUpdateWidget(this, mAppWidgetId);
        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private static int getWidgetId(@Nullable Bundle extras) {
        return extras != null ? extras.getInt(EXTRA_APPWIDGET_ID) : INVALID_APPWIDGET_ID;
    }

    public static final String[] QUOTE_LIST_PROJECTION = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.ISUP};
}
