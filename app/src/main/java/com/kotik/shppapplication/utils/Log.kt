package com.kotik.shppapplication.utils

import android.util.Log

fun log(message: String = "Default message", isError: Boolean = false) {
    if (!isError) Log.d("myLog", message) else Log.e("myLog", message)
}