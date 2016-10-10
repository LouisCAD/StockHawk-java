package com.sam_chordas.android.stockhawk.ui

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
