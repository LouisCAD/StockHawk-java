package com.sam_chordas.android.stockhawk.ui

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

@ColorInt
fun Context.colorRes(@ColorRes colorResId: Int): Int = ContextCompat.getColor(this, colorResId)