package com.module.config.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.module.config.R
import com.module.config.views.activities.splash.SplashActivity

internal const val REQUEST_CODE_FOREGROUND = 1
internal const val ID_FOREGROUND = 27
private const val CHANNEL_NAME = "TURN OFF NOTIFICATION"

object NotificationUtils {

    fun startForeground(service: Service) {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelForeground(service)
        } else {
            CHANNEL_NAME
        }
        val intent = Intent(service, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                service,
                REQUEST_CODE_FOREGROUND,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                service,
                REQUEST_CODE_FOREGROUND,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notificationBuilder = NotificationCompat.Builder(service, channelId)
        val notification = notificationBuilder
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(service.resources.getString(R.string.app_name))
            .setContentText(service.resources.getString(R.string.app_name))
            .setContentIntent(pendingIntent)
            .build()
        service.startForeground(ID_FOREGROUND, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannelForeground(
        context: Context,
        channelId: String = "CHANNEL_ID_FOREGROUND",
    ): String {
        val chan = NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    fun stopForeground(service: Service) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                service.stopForeground(Service.STOP_FOREGROUND_DETACH)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}