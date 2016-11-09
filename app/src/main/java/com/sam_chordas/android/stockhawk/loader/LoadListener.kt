package com.sam_chordas.android.stockhawk.loader

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader

class LoadListener<D, out L : Loader<D>>(private val createLoader: () -> L,
                                         private val onLoadDone: (loader: L, data: D) -> Unit)
    : LoaderManager.LoaderCallbacks<D> {

    override fun onCreateLoader(id: Int, args: Bundle?) = createLoader()
    @Suppress("UNCHECKED_CAST")
    override fun onLoadFinished(loader: Loader<D>?, data: D) = onLoadDone(loader as L, data)

    override fun onLoaderReset(loader: Loader<D>?) = Unit
}