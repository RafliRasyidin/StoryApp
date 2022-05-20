package com.rasyidin.storyapp.presentation.component

import android.util.Log
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

fun String.toShortNumber(): String? {
    return try {
        val doubleNumber = this.toDouble()
        val formatter = DecimalFormat("0.#")
        val isNumberLessThanThousand = this.toInt() < 1000.0
        formatter.roundingMode = RoundingMode.DOWN

        if (isNumberLessThanThousand) {
            formatter.format(doubleNumber)
        } else {
            val exp = (ln(doubleNumber) / ln(1000.0)).toInt()
            formatter.format(this.toInt() / 1000.0.pow(exp.toDouble())) + "KMBTPE"[exp - 1]
        }
    } catch (e: Exception) {
        "0"
    }
}

fun String.withDateFormat(): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("EEE, d MMM h:mm a", Locale.US)
        val date = format.parse(this) as Date
        outputFormat.format(date)
    } catch (e: Exception) {
        Log.e("DateFormatter", e.printStackTrace().toString())
        this
    }
}