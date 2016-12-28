package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.google.android.agera.Result;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.loader.QuoteHistoryLoader;
import com.sam_chordas.android.stockhawk.model.QuoteResult;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static com.db.chart.view.ChartView.GridType.HORIZONTAL;

public final class StockDetailsActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener, LoaderManager.LoaderCallbacks<Result<List<QuoteResult>>> {

    public static final String EXTRA_QUOTE_SYMBOL = "quote";
    private static final int LOADER_HISTORY = 0;
    private static final String KEY_SELECTED_TAB = "selectedTab";

    private String mSymbol;
    private Snackbar mSnackbar;

    private CoordinatorLayout mCl;
    private TabLayout mTabLayout;
    private LineChartView mChart;

    private List<QuoteResult> last6MonthsQuotes;
    private List<QuoteResult> last3MonthsQuotes;
    private List<QuoteResult> lastMonthQuotes;
    private List<QuoteResult> last5BusinessDaysQuotes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        mSymbol = getIntent().getStringExtra(EXTRA_QUOTE_SYMBOL);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mChart = (LineChartView) findViewById(R.id.chart);
        mCl = (CoordinatorLayout) findViewById(R.id.cl);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mSymbol);
        addTabs(mTabLayout,
                R.string.fiveDays,
                R.string.oneMonth,
                R.string.threeMonths,
                R.string.sixMonths);
        if (savedInstanceState != null) {
            mTabLayout.getTabAt(savedInstanceState.getInt(KEY_SELECTED_TAB)).select();
        }
        getSupportLoaderManager().initLoader(LOADER_HISTORY, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_TAB, mTabLayout.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Result<List<QuoteResult>>> onCreateLoader(int id, Bundle args) {
        return new QuoteHistoryLoader(this, mSymbol);
    }

    @Override
    public void onLoadFinished(Loader<Result<List<QuoteResult>>> loader, Result<List<QuoteResult>> data) {
        if (data.succeeded()) {
            attemptDismissSnackbar();
            mTabLayout.addOnTabSelectedListener(this);
            updateQuotes(data.get());
            updateChart(mTabLayout.getSelectedTabPosition());
        } else if (data.isAbsent()) {
            attemptDismissSnackbar();
            mSnackbar = Snackbar.make(mCl, R.string.loading, LENGTH_INDEFINITE);
        } else {
            attemptDismissSnackbar();
            mSnackbar = Snackbar.make(mCl, R.string.request_failed, LENGTH_INDEFINITE);
            mSnackbar.show();
            Throwable failReason = data.failureOrNull();
            if (failReason != null) Timber.wtf(failReason);
        }
    }

    private void updateChart(int tabPosition) {
        updateChart(quotesForTabPosition(tabPosition));
    }

    private void updateChart(List<QuoteResult> quotes) {
        final int step = quotes.size() > 99 ? 10 : 5;
        final String[] labels = new String[quotes.size()];
        final float[] values = new float[quotes.size()];
        float max = quotes.get(0).closeValue;
        float min = max;
        for (int i = 0; i < quotes.size(); i++) {
            final QuoteResult quote = quotes.get(i);
            float value = quote.closeValue;
            labels[i] = "";
            values[i] = value;
            if (value > max) max = value;
            if (value < min) min = value;
        }
        min -= 1;
        max += 1;
        final LineSet lines = new LineSet(labels, values);
        for (int i = 0; i < lines.size(); i++) {
            lines.setColor(ContextCompat.getColor(this, R.color.primary));
        }
        final Paint gridPaint = new Paint();
        gridPaint.setColor(0x2fffffff);
        final int minVal = roundDown(min, step);
        final int maxVal = roundUp(max, step);
        final int rows = (maxVal - minVal) / step;
        mChart.setGrid(HORIZONTAL, rows, 1, gridPaint);
        final ArrayList<ChartSet> linesData = new ArrayList<>(1);
        linesData.add(lines);
        mChart.addData(linesData);
        mChart.setStep(step);
        mChart.setAxisBorderValues(minVal, maxVal);
        mChart.show();
    }

    private int roundDown(float value, int step) {
        int n = (int) value;
        return n - n % step;
    }

    private int roundUp(float value, int step) {
        return roundDown(value, step) + step;
    }

    private List<QuoteResult> quotesForTabPosition(int tabPosition) {
        switch (tabPosition) {
            case 0:
                return last5BusinessDaysQuotes;
            case 1:
                return lastMonthQuotes;
            case 2:
                return last3MonthsQuotes;
            case 3:
            default:
                return last6MonthsQuotes;
        }
    }

    private void updateQuotes(List<QuoteResult> quotes) {
        final int end = quotes.size();
        last6MonthsQuotes = quotes;
        last3MonthsQuotes = quotes.subList(end - quotes.size() / 2, end);
        lastMonthQuotes = quotes.subList(end - quotes.size() / 6, end);
        last5BusinessDaysQuotes = quotes.subList(end - 5, end);
    }

    private void attemptDismissSnackbar() {
        if (mSnackbar != null) mSnackbar.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Result<List<QuoteResult>>> loader) {

    }

    private static void addTabs(TabLayout tabLayout, int... titleResIds) {
        for (int titleRedId : titleResIds) tabLayout.addTab(tabLayout.newTab().setText(titleRedId));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateChart(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
