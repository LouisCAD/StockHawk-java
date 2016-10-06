package com.sam_chordas.android.stockhawk.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;

/**
 * Mutable model for code clarity
 * @see com.sam_chordas.android.stockhawk.data.QuoteColumns
 */

public final class Quote {
    public long id;
    public String symbol;
    public String percentChange;
    public String change;
    public String bidPrice;
    public String created;
    public int isUp;
    public int isCurrent;

    public void set(@NonNull Cursor c, @NonNull Indexes i) {
        if (i.id != -1) id = c.getLong(i.id);
        if (i.symbol != -1) symbol = c.getString(i.symbol);
        if (i.percentChange != -1) percentChange = c.getString(i.percentChange);
        if (i.change != -1) change = c.getString(i.change);
        if (i.bidPrice != -1) bidPrice = c.getString(i.bidPrice);
        if (i.created != -1) created = c.getString(i.created);
        if (i.isUp != -1) isUp = c.getInt(i.isUp);
        if (i.isCurrent != -1) isCurrent = c.getInt(i.isCurrent);
    }

    public static class Indexes {
        private int id;
        private int symbol;
        private int percentChange;
        private int change;
        private int bidPrice;
        private int created;
        private int isUp;
        private int isCurrent;

        public void set(@NonNull Cursor c) {
            id = c.getColumnIndex(QuoteColumns._ID);
            symbol = c.getColumnIndex(QuoteColumns.SYMBOL);
            percentChange = c.getColumnIndex(QuoteColumns.PERCENT_CHANGE);
            change = c.getColumnIndex(QuoteColumns.CHANGE);
            bidPrice = c.getColumnIndex(QuoteColumns.BIDPRICE);
            created = c.getColumnIndex(QuoteColumns.CREATED);
            isUp = c.getColumnIndex(QuoteColumns.ISUP);
            isCurrent = c.getColumnIndex(QuoteColumns.ISCURRENT);
        }
    }
}
