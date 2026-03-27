package com.velora.mobile.presentation.tracker



import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatApplicationDate(timestamp: Long): String {
    if (timestamp <= 0L) return "Unknown date"
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}