package com.piontech.core.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.lang.Exception

fun safeDelay(delayMillis: Long = 0, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, delayMillis)
}