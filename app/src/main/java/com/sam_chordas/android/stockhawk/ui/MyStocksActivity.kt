package com.sam_chordas.android.stockhawk.ui

import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.CursorLoader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.agera.Updatable
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.PeriodicTask
import com.google.android.gms.gcm.Task
import com.sam_chordas.android.stockhawk.R
import com.sam_chordas.android.stockhawk.agera.ConnectivityListener
import com.sam_chordas.android.stockhawk.data.QuoteColumns
import com.sam_chordas.android.stockhawk.data.QuoteProvider
import com.sam_chordas.android.stockhawk.loader.LoadListener
import com.sam_chordas.android.stockhawk.model.Quote
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter
import com.sam_chordas.android.stockhawk.service.StockIntentService
import com.sam_chordas.android.stockhawk.service.StockTaskService
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback
import com.sam_chordas.android.stockhawk.ui.StockDetailsActivity.Companion.EXTRA_QUOTE_SYMBOL
import kotlinx.android.synthetic.main.activity_my_stocks.*
import kotlin.LazyThreadSafetyMode.NONE

class MyStocksActivity : AppCompatActivity(), QuoteCursorAdapter.ViewHolder.Host {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in [.restoreActionBar].
     */
    private var mTitle: CharSequence? = null
    private var mServiceIntent: Intent? = null
    private var mCursorAdapter: QuoteCursorAdapter? = null

    private val connectivityListener by lazy(NONE) { ConnectivityListener(this) }

    private val onConnectionChanged = Updatable {
        val isConnected = connectivityListener.isNetworkConnected
        fab.isEnabled = isConnected
        // TODO: 10/11/2016 Show no connection in the appbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_stocks)
        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        mServiceIntent = Intent(this, StockIntentService::class.java)
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent!!.putExtra("tag", "init")
            if (connectivityListener.isNetworkConnected) {
                startService(mServiceIntent)
            }
        }
        val recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportLoaderManager.initLoader(CURSOR_LOADER_ID, null, quotesLoadCallback)

        mCursorAdapter = QuoteCursorAdapter(this, null)
        recyclerView.adapter = mCursorAdapter

        fab.setOnClickListener {
            MaterialDialog.Builder(this).title(R.string.symbol_search)
                    .titleColor(Color.BLACK)
                    .contentColor(Color.BLACK)
                    .content(R.string.content_test)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(R.string.input_hint, R.string.input_prefill, MaterialDialog.InputCallback { dialog, input ->
                        // On FAB click, receive user input. Make sure the stock doesn't already exist
                        // in the DB and proceed accordingly
                        val c = contentResolver.query(QuoteProvider.Quotes.CONTENT_URI,
                                arrayOf(QuoteColumns.SYMBOL), QuoteColumns.SYMBOL + "= ?",
                                arrayOf(input.toString()), null)
                        if (c != null && c.count != 0) {
                            val toast = Toast.makeText(this@MyStocksActivity, R.string.stock_already_saved,
                                    Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0)
                            toast.show()
                            return@InputCallback
                        } else {
                            // Add the stock to DB
                            mServiceIntent!!.putExtra("tag", "add")
                            mServiceIntent!!.putExtra("symbol", input.toString())
                            startService(mServiceIntent)
                        }
                        c?.close()
                    })
                    .show()
        }

        val callback = SimpleItemTouchHelperCallback(mCursorAdapter)
        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)

        mTitle = title
        if (connectivityListener.isNetworkConnected) {
            val period = 3600L
            val flex = 10L
            val periodicTag = "periodic"

            // create a periodic task to pull stocks once every hour after the app has been opened. This
            // is so Widget data stays up to date.
            val periodicTask = PeriodicTask.Builder()
                    .setService(StockTaskService::class.java)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build()
            // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
            // are updated.
            GcmNetworkManager.getInstance(this).schedule(periodicTask)
        }
    }

    override fun onClick(quote: Quote) {
        startActivity(Intent(this, StockDetailsActivity::class.java).putExtra(EXTRA_QUOTE_SYMBOL, quote.symbol))
    }

    public override fun onResume() {
        super.onResume()
        connectivityListener.addUpdatable(onConnectionChanged)
        onConnectionChanged.update()
        supportLoaderManager.restartLoader(CURSOR_LOADER_ID, null, quotesLoadCallback)
    }

    override fun onPause() {
        super.onPause()
        connectivityListener.removeUpdatable(onConnectionChanged)
    }

    fun restoreActionBar() {
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.title = mTitle
    }

    override fun onCreateOptionsMenu(menu: Menu) = consume {
        menuInflater.inflate(R.menu.my_stocks, menu)
        restoreActionBar()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_change_units -> consume { mCursorAdapter!!.changeUnits() }
        else -> super.onOptionsItemSelected(item)
    }

    private val quotesLoadCallback = LoadListener({
        val projection = arrayOf(QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP)
        // This narrows the return to only the stocks that are most current.
        CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                projection,
                QuoteColumns.ISCURRENT + " = ?",
                arrayOf("1"),
                null)
    }, { mCursorAdapter!!.swapCursor(null) }
    ) {
        loader: CursorLoader, data: Cursor ->
        mCursorAdapter!!.swapCursor(data)
    }

    companion object {
        private val CURSOR_LOADER_ID = 0
    }
}
