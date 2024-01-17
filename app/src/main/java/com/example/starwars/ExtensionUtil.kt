package com.example.calendar

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun <T> ComponentActivity.collectEvent(flow: Flow<T>, collect: suspend (T) -> Unit): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.onEach(collect).collect()
        }
    }
}

fun <T> Fragment.collectEvent(flow: Flow<T>, collect: suspend (T) -> Unit): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.onEach(collect).collect()
        }
    }
}

fun formatDateToDayOfMonth(dateString: String): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val date: Date = inputFormat.parse(dateString)
    val outputFormat = SimpleDateFormat("dd", Locale.ENGLISH)
    return outputFormat.format(date)
}


