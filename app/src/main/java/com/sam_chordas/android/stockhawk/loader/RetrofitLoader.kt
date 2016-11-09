package com.sam_chordas.android.stockhawk.loader

import android.content.Context
import android.support.v4.content.Loader
import com.google.android.agera.Result
import com.google.android.agera.Updatable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class RetrofitLoader<Body, D>(context: Context) : Loader<Result<D>>(context), Callback<Body>, Updatable {

    private var lastResult = Result.absent<D>()
    private var lastCall: Call<Body>? = null

    abstract val call: Call<Body>
    abstract fun transformResponse(body: Body): D

    override fun update() {
        lastCall?.cancel()
        lastCall = call
        lastCall!!.enqueue(this)
    }

    override fun onStartLoading() {
        deliverResult(lastResult)
        if (!lastResult.succeeded()) update()
    }

    override fun onForceLoad() {
        deliverResult(lastResult)
        update()
    }

    override fun onReset() {
        lastCall?.cancel()
        lastResult = Result.absent()
    }

    override fun onCancelLoad(): Boolean {
        lastCall?.apply {
            cancel()
            return true
        }
        return false
    }

    override fun onResponse(call: Call<Body>, response: Response<Body>) {
        lastResult = if (response.isSuccessful) Result.success(transformResponse(response.body()))
        else Result.failure()
        deliverResult(lastResult)
    }

    override fun onFailure(call: Call<Body>, t: Throwable) {
        lastResult = Result.failure(t)
        deliverResult(lastResult)
    }
}