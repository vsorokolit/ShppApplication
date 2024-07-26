package com.kotik.shppapplication.utils

import android.util.DisplayMetrics
import android.util.TypedValue

object MyHelp {

    fun toPx(displayMetrics: DisplayMetrics, dp: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        displayMetrics
    ).toInt()

}