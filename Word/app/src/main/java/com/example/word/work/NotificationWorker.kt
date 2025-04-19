package com.example.word.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.word.MainActivity
import com.example.word.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
//ajil uusgeh class
class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        private const val CHANNEL_ID = "MyChannelId"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_NAME = "My Channel"
    }
    //gol hiih ajilaa dahin tdrhoilno
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        showNotification(applicationContext)
        Result.success()
    }

    private fun showNotification(context: Context) {
        createNotificationChannel(context)
        //notif deer darahad app ruu oroh
        //Main Activity ruu orohiig hatuu zaaj ugsun
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        //pending buyu dam ehluuleh
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE) //Flag_IMMUTABLE- dahin uurchlugduhguig zaana
        //notif builder
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Word Memorize App")
            .setContentText("It is time to check your memory?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        //notif manager uusgene
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    //notif channel ee uusgene
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            //channel bolgon der notif oo zohion baiguulna
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
//schedule hiih
fun scheduleNotificationWorker(context: Context) {
    //nuhtsul
    val constraints = androidx.work.Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
    // hugatsaagaa todorhoilno

    // davtamj aa uusgene
    val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(8, TimeUnit.HOURS)
            .addTag("Reminder")
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

    // daraalluulna
    WorkManager.getInstance(context).enqueueUniquePeriodicWork("UniqueReminder",ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, periodicWorkRequest)
}
