package com.example.stopwatch.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.stopwatch.AppConstants.ACTION_PAUSE_SERVICE
import com.example.stopwatch.AppConstants.ACTION_START_OR_RESUME_SERVICE
import com.example.stopwatch.AppConstants.ACTION_STOP_SERVICE
import com.example.stopwatch.AppConstants.NOTIFICATION_CHANNEL_SW_ID
import com.example.stopwatch.AppConstants.NOTIFICATION_CHANNEL_SW_NAME
import com.example.stopwatch.AppConstants.NOTIFICATION_SW_ID
import com.example.stopwatch.AppConstants.TIMER_UPDATE_INTERVAL
import com.example.stopwatch.R
import com.example.stopwatch.ui.mvp.view.MainActivity
import com.example.stopwatch.utils.Converter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchService : LifecycleService() {
    private var isFirstRun = true
    var serviceKilled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val isRunning = MutableLiveData<Boolean>()
    }

    private fun postInitialValues() {
        isRunning.postValue(false)
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startTiming()
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startTiming()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isRunning.postValue(false)
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        CoroutineScope(Dispatchers.Main).launch {
            postInitialValues()
        }
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService() {
        isRunning.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_SW_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.play_arrow)
                .setContentTitle("Running App")
                .setContentText("00:00:00:00")
                .setContentIntent(getMainActivityPendingIntent())


        startForeground(NOTIFICATION_SW_ID, this.notificationBuilder.build())
        timeRunInSeconds.observe(this, {
            if (!serviceKilled) {
                val notification = this.notificationBuilder
                        .setContentText(Converter.getFormattedTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_SW_ID, notification.build())
            }
        })
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(this, 0,
                Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun startTiming() {
        isRunning.postValue(true)
        timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isRunning.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_SW_ID,
                NOTIFICATION_CHANNEL_SW_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

}