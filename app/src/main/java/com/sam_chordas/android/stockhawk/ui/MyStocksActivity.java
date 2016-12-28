package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.agera.Updatable;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.agera.ConnectivityListener;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import static com.sam_chordas.android.stockhawk.UtilsKt.runOnce;
import static com.sam_chordas.android.stockhawk.ui.StockDetailsActivity.EXTRA_QUOTE_SYMBOL;

public final class MyStocksActivity extends AppCompatActivity
        implements QuoteCursorAdapter.ViewHolder.Host, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;

    private QuoteCursorAdapter mCursorAdapter;
    private ConnectivityListener mConnectivityListener;
    private FloatingActionButton mFab;
    private TextView mNoConnectionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stocks);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mCursorAdapter = new QuoteCursorAdapter(this, null);
        rv.setAdapter(mCursorAdapter);
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mNoConnectionTextView = (TextView) findViewById(R.id.no_connection_text);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MyStocksActivity.this)
                        .title(R.string.symbol_search)
                        .titleColor(Color.BLACK)
                        .contentColor(Color.BLACK)
                        .content(R.string.content_test)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                final Cursor c = getContentResolver()
                                        .query(QuoteProvider.Quotes.CONTENT_URI,
                                                new String[]{QuoteColumns.SYMBOL},
                                                QuoteColumns.SYMBOL + "= ?",
                                                new String[]{input.toString()}, null);
                                if (c != null && c.getCount() != 0) {
                                    final Toast toast = Toast.makeText(MyStocksActivity.this,
                                            R.string.stock_already_saved, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                    toast.show();
                                } else {
                                    Intent serviceIntent = new Intent(MyStocksActivity.this,
                                            StockIntentService.class)
                                            .putExtra("tag", "add")
                                            .putExtra("symbol", input.toString());
                                    startService(serviceIntent);
                                }
                                if (c != null) c.close();
                            }
                        })
                        .show();
            }
        });
        new ItemTouchHelper(new SimpleItemTouchHelperCallback(mCursorAdapter))
                .attachToRecyclerView(rv);
        mConnectivityListener = new ConnectivityListener(this);
    }

    @Override
    public void onClick(@NonNull Quote quote) {
        startActivity(new Intent(this, StockDetailsActivity.class).putExtra(EXTRA_QUOTE_SYMBOL, quote.symbol));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectivityListener.addUpdatable(mConnectivityUpdatable);
        mConnectivityUpdatable.update();
        getSupportLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    private final Updatable mConnectivityUpdatable = new Updatable() {
        @Override
        public void update() {
            final boolean isConnected = mConnectivityListener.isNetworkConnected();
            mFab.setEnabled(isConnected);
            mNoConnectionTextView.setVisibility(isConnected ? View.GONE : View.VISIBLE);
            if (isConnected) {
                runOnce(MyStocksActivity.this, "init_with_some_stocks", new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        // Run the initialize task service so that some stocks appear upon an empty database
                        Intent serviceIntent = new Intent(MyStocksActivity.this, StockIntentService.class)
                                .putExtra("tag", "init");
                        startService(serviceIntent);
                        return null;
                    }
                });
                final long period = 3600L;
                final long flex = 10L;
                final String periodicTag = "periodic";

                // create a periodic task to pull stocks once every hour after the app has been opened. This
                // is so Widget data stays up to date.
                final PeriodicTask periodicTask = new PeriodicTask.Builder()
                        .setService(StockTaskService.class)
                        .setPeriod(period)
                        .setFlex(flex)
                        .setTag(periodicTag)
                        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                        .setRequiresCharging(false)
                        .build();
                // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
                // are updated.
                GcmNetworkManager.getInstance(MyStocksActivity.this).schedule(periodicTask);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mConnectivityListener.removeUpdatable(mConnectivityUpdatable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_stocks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_units:
                mCursorAdapter.changeUnits();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP};
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                projection,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
