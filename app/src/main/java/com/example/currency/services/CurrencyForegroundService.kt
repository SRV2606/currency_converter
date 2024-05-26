package com.example.currency.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.currency.R
import com.example.domain.domain.usecases.GetLatestCurrencyExchangesDataUseCase
import com.example.domain.models.ClientResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyForegroundService : Service() {

    @Inject
    lateinit var useCase: GetLatestCurrencyExchangesDataUseCase

    private val serviceJob = CoroutineScope(Dispatchers.IO)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Current_TAG", "onStartCommand: ")
        startForeground(NOTIFICATION_ID, createNotification())
        executeApiCall()
        return START_STICKY
    }

    private fun executeApiCall() {
        // Launch a coroutine within the service's coroutine scope
        serviceJob.launch {
            Log.d("Current_TAG", "executeApiCall: ")

            useCase.getLatestCurrencyExchanges().let { result ->
                // Handle the result, update notification accordingly
                when (result) {
                    is ClientResult.Success -> {
                        updateNotification("Data fetched successfully")
                        //for testing purposes , else wont be able to see the notif
//                        stopSelf()
                    }

                    is ClientResult.Error -> {
                        updateNotification("Failed to fetch data")
//                        stopSelf()
                    }

                    else -> {
                        updateNotification("Failed to fetch data")
                        //for testing purposes , else wont be able to see the notif
//                        stopSelf()
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun updateNotification(message: String) {
        // Update the notification with the provided message
        val notification = createNotification(message)
    }

    private fun createNotification(message: String? = ""): Notification {
        Log.d("Current_TAG", "createNotification: ")

        val channelId = "currency_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Currency Worker")
            .setContentText(message ?: "Fetching currency data...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Currency Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = notificationBuilder.build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        return notification
    }

    companion object {
        private const val NOTIFICATION_ID = 12345
        fun createStartIntent(context: Context): Intent {
            return Intent(context, CurrencyForegroundService::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // Cancel the service's job when the service is destroyed
    }
}