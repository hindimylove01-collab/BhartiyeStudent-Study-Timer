package com.bhartiyestudent.studytimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class StudyTimerService : Service() {

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    private var endTime: Long = 0L
    private var subject: String = ""
    private var totalMinutes: Int = 0

    companion object {
        const val CHANNEL_ID = "study_timer_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        subject = intent?.getStringExtra("subject") ?: "Study"
        totalMinutes = intent?.getIntExtra("minutes", 60) ?: 60

        endTime = SystemClock.elapsedRealtime() +
                (totalMinutes * 60 * 1000L)

        startForeground(
            NOTIFICATION_ID,
            createNotification(totalMinutes * 60)
        )

        startCountdown()

        return START_STICKY
    }

    private fun startCountdown() {

        serviceScope.coroutineContext.cancelChildren()

        serviceScope.launch {

            while (isActive) {

                val remaining =
                    ((endTime - SystemClock.elapsedRealtime()) / 1000)
                        .toInt()

                if (remaining <= 0) {

                    updateNotification(0)

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()

                    break
                }

                updateNotification(remaining)

                delay(1000)
            }
        }
    }

    private fun createNotification(seconds: Int): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("📚 $subject")
            .setContentText(
                "Time remaining: ${formatTime(seconds)}"
            )
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(seconds: Int) {

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.notify(
            NOTIFICATION_ID,
            createNotification(seconds)
        )
    }

    private fun formatTime(seconds: Int): String {

        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                secs
            )
        } else {
            String.format(
                "%02d:%02d",
                minutes,
                secs
            )
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Study Timer",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description =
                "Shows the study timer while running."

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {

        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
