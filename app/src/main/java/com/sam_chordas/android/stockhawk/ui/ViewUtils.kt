@file:Suppress("NOTHING_TO_INLINE")

package com.sam_chordas.android.stockhawk.ui

import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import android.view.View
import org.jetbrains.anko.onLongClick
import org.jetbrains.anko.toast

/**
 * Shows a toast with [View.getContentDescription]
 */
fun View.bindLongClickToContentDesc() {
    consumeLongClick { context.toast(contentDescription) }
}

fun View.consumeLongClick(f: () -> Any) {
    onLongClick { consume { f() } }
}

inline fun consume(f: () -> Any): Boolean {
    f()
    return true;
}

inline fun View.snack(@IntegerRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG): Snackbar {
    val snack = Snackbar.make(this, resources.getString(messageRes), length)
    snack.show()
    return snack
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG): Snackbar {
    val snack = Snackbar.make(this, message, length)
    snack.show()
    return snack
}

inline fun View.snack(@IntegerRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@IntegerRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    action(view.resources.getString(actionRes), color, listener)
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}
