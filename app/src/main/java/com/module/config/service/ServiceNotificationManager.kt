package com.module.config.service

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.module.config.R
import com.module.config.rx.RxBusHelper
import com.module.config.utils.utils_controller.Config
import com.module.config.utils.utils_controller.Toolbox

class ServiceNotificationManager (val context: Context) {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    @SuppressLint("RemoteViewLayout")
    fun showMainNotification(service: Service) {
        Log.e("CHECKNOTIFI","showNoti")
        val notificationLayout: RemoteViews =
            if (context.resources.configuration.layoutDirection == 1) {
                RemoteViews(context.packageName, R.layout.layout_notification_main_rtl)
            } else {
                RemoteViews(context.packageName, R.layout.layout_notification_main)
            }
        val intentFilterControl = IntentFilter()
        intentFilterControl.addAction(ACTION_NOTIFICATION_VIEW_CLICK)
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        context.registerReceiver(receiver, intentFilterControl)
        notificationLayout.setTextViewText(
            R.id.tv_record,
            context.resources.getString(R.string.record)
        )
        notificationLayout.setTextViewText(
            R.id.tv_screenshot,
            context.resources.getString(R.string.screenshots)
        )
        notificationLayout.setTextViewText(R.id.tv_home, context.resources.getString(R.string.home))
        notificationLayout.setTextViewText(
            R.id.tv_tools,
            context.resources.getString(R.string.tools)
        )
        notificationLayout.setTextViewText(
            R.id.tv_close,
            context.resources.getString(R.string.close)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_record, onButtonNotificationClick(
                context, R.id.noti_record
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_screen_shot, onButtonNotificationClick(
                context, R.id.noti_screen_shot
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_tools, onButtonNotificationClick(
                context, R.id.noti_tools
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_home,
            onButtonNotificationClick(context, R.id.noti_home)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_exit,
            onButtonNotificationClick(context, R.id.noti_exit)
        )
        createChannel(ID_NOTIFICATION_SERVICE)
        val notification: Notification = NotificationCompat.Builder(
            context, ID_NOTIFICATION_SERVICE.toString()
        )
            .setSmallIcon(R.drawable.ic_home_app)
            .setCustomContentView(notificationLayout)
            .setOngoing(true)
            .build()
        startFogroundNotificationService(service, ID_NOTIFICATION_SERVICE, notification)
    }

    @SuppressLint("RemoteViewLayout")
    fun showRecordingNotification() {
        val notificationLayout: RemoteViews =
            if (context.resources.configuration.layoutDirection == 1) {
                RemoteViews(context.packageName, R.layout.layout_notification_recording_rtl)
            } else {
                RemoteViews(context.packageName, R.layout.layout_notification_recording)
            }
        val intentFilterControl = IntentFilter()
        intentFilterControl.addAction(ACTION_NOTIFICATION_VIEW_CLICK)
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        context.registerReceiver(receiver, intentFilterControl)
        notificationLayout.setTextViewText(
            R.id.tv_pause,
            context.resources.getString(R.string.pause)
        )
        notificationLayout.setTextViewText(
            R.id.tv_resume,
            context.resources.getString(R.string.resume)
        )
        notificationLayout.setTextViewText(R.id.tv_stop, context.resources.getString(R.string.stop))
        notificationLayout.setTextViewText(
            R.id.tv_tools,
            context.resources.getString(R.string.tools)
        )
        notificationLayout.setTextViewText(
            R.id.tv_close,
            context.resources.getString(R.string.close)
        )
        notificationLayout.setViewVisibility(R.id.noti_play, View.GONE)
        notificationLayout.setViewVisibility(R.id.noti_pause, View.VISIBLE)
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_pause, onButtonNotificationClick(
                context, R.id.noti_pause
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_stop,
            onButtonNotificationClick(context, R.id.noti_stop)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_play,
            onButtonNotificationClick(context, R.id.noti_play)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_tools, onButtonNotificationClick(
                context, R.id.noti_tools
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_close, onButtonNotificationClick(
                context, R.id.noti_close
            )
        )
        createChannel(ID_NOTIFICATION_SERVICE)
        val notification: Notification = NotificationCompat.Builder(
            context, ID_NOTIFICATION_SERVICE.toString()
        )
            .setSmallIcon(R.drawable.ic_home_app)
            .setCustomContentView(notificationLayout)
            .setOngoing(true)
            .build()
        updateNotification(ID_NOTIFICATION_SERVICE, notification)
    }

    @SuppressLint("RemoteViewLayout")
    fun showPausedNotification() {
        val notificationLayout: RemoteViews =
            if (context.resources.configuration.layoutDirection == 1) {
                RemoteViews(context.packageName, R.layout.layout_notification_recording_rtl)
            } else {
                RemoteViews(context.packageName, R.layout.layout_notification_recording)
            }
        val intentFilterControl = IntentFilter()
        intentFilterControl.addAction(ACTION_NOTIFICATION_VIEW_CLICK)
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        context.registerReceiver(receiver, intentFilterControl)
        notificationLayout.setTextViewText(
            R.id.tv_pause,
            context.resources.getString(R.string.pause)
        )
        notificationLayout.setTextViewText(
            R.id.tv_resume,
            context.resources.getString(R.string.resume)
        )
        notificationLayout.setTextViewText(R.id.tv_stop, context.resources.getString(R.string.stop))
        notificationLayout.setTextViewText(
            R.id.tv_tools,
            context.resources.getString(R.string.tools)
        )
        notificationLayout.setTextViewText(
            R.id.tv_close,
            context.resources.getString(R.string.close)
        )
        notificationLayout.setViewVisibility(R.id.noti_pause, View.GONE)
        notificationLayout.setViewVisibility(R.id.noti_play, View.VISIBLE)
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_pause, onButtonNotificationClick(
                context, R.id.noti_pause
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_stop,
            onButtonNotificationClick(context, R.id.noti_stop)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_play,
            onButtonNotificationClick(context, R.id.noti_play)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_tools, onButtonNotificationClick(
                context, R.id.noti_tools
            )
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.noti_close, onButtonNotificationClick(
                context, R.id.noti_close
            )
        )
        createChannel(ID_NOTIFICATION_SERVICE)
        val notification: Notification = NotificationCompat.Builder(
            context, ID_NOTIFICATION_SERVICE.toString()
        )
            .setSmallIcon(R.drawable.ic_home_app)
            .setCustomContentView(notificationLayout)
            .setOngoing(true)
            .build()
        updateNotification(ID_NOTIFICATION_SERVICE, notification)
    }

    fun hideNotificationScreenRecording() {
        hidenNotification(ID_NOTIFICATION_SERVICE)
    }

    fun showScreenRecordSuccessNotification(path: String?) {
        val openVideoIntent: Intent = Toolbox.getIntentActionView(context, path)
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            openVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        createChannel(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS, NotificationManager.IMPORTANCE_DEFAULT)
        val notification: Notification = NotificationCompat.Builder(
            context, ID_NOTIFICATION_SCREEN_RECORD_SUCCESS.toString()
        )
            .setContentTitle(context.getString(R.string.screen_recorder))
            .setContentText("Open Video")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_video)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        updateNotification(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS, notification)
    }

    fun hideScreenRecordSuccessNotification() {
        hidenNotification(ID_NOTIFICATION_SCREEN_RECORD_SUCCESS)
    }

    fun showScreenshotSuccessNotification(path: String?) {
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.img_crop)
        val openVideoIntent: Intent = Toolbox.getIntentActionView(context, path)
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            openVideoIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        createChannel(ID_NOTIFICATION_SCREENSHOT_SUCCESS, NotificationManager.IMPORTANCE_MAX)
        val notification: Notification = NotificationCompat.Builder(
            context, ID_NOTIFICATION_SCREENSHOT_SUCCESS.toString()
        )
            .setContentTitle(context.getString(R.string.screen_recorder))
            .setContentText("Open Image")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_camera)
            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        updateNotification(ID_NOTIFICATION_SCREENSHOT_SUCCESS, notification)
    }

    fun hideScreenshotSuccessNotification() {
        hidenNotification(ID_NOTIFICATION_SCREENSHOT_SUCCESS)
    }

    fun showShakeNotification() {
        val destroyShakeRecord = Intent(
            context,
            RecorderService::class.java
        )
        destroyShakeRecord.action = Config.ACTION_STOP_SHAKE
        val pendingIntent = PendingIntent.getService(
            context, 0, destroyShakeRecord, 0
        )
        createChannel(ID_NOTIFICATION_SHAKE, NotificationManager.IMPORTANCE_DEFAULT)
        val notification: Notification = NotificationCompat.Builder(
            context, ID_NOTIFICATION_SHAKE.toString()
        )
            .setContentTitle(context.getString(R.string.screen_recorder))
            .setContentText(context.getString(R.string.content_notification_shake))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_record)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        updateNotification(ID_NOTIFICATION_SHAKE, notification)
    }

    fun hideShakeNotification() {
        hidenNotification(ID_NOTIFICATION_SHAKE)
    }

    fun startFogroundNotificationService(service: Service, id: Int, notification: Notification?) {
        service.startForeground(id, notification)
    }

    @JvmOverloads
    fun createChannel(id: Int, importance: Int = NotificationManager.IMPORTANCE_LOW) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.app_name)
            val channel = NotificationChannel(id.toString(), name, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(id: Int, notification: Notification?) {
        notificationManager.notify(id, notification)
    }

    fun onButtonNotificationClick(context: Context?, @IdRes id: Int): PendingIntent {
        val intent = Intent(ACTION_NOTIFICATION_VIEW_CLICK)
        intent.putExtra(EXTRA_VIEW_CLICKED, id)
        return PendingIntent.getBroadcast(context, id, intent, FLAG_IMMUTABLE)
    }

    fun hidenNotification(id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getIntExtra(EXTRA_VIEW_CLICKED, -1)
            when (id) {
                R.id.noti_record -> RxBusHelper.sendClickNotificationScreenRecord()
                R.id.noti_screen_shot -> RxBusHelper.sendClickNotificationScreenShot()
                R.id.noti_tools -> RxBusHelper.sendClickNotificationTools()
                R.id.noti_home -> RxBusHelper.sendClickNotificationHome()
                R.id.noti_exit, R.id.noti_close -> RxBusHelper.sendClickNotificationExit()
                R.id.noti_pause -> RxBusHelper.sendClickNotificationPause()
                R.id.noti_play -> RxBusHelper.sendClickNotificationResume()
                R.id.noti_stop -> RxBusHelper.sendClickNotificationStop()
            }
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(it)
        }
    }

    companion object {
        const val ID_NOTIFICATION_SERVICE = 1001
        const val ID_NOTIFICATION_SCREEN_RECORD_SUCCESS = 808
        const val ID_NOTIFICATION_SCREENSHOT_SUCCESS = 809
        const val ID_NOTIFICATION_SHAKE = 810
        const val ACTION_NOTIFICATION_VIEW_CLICK = "action_view_clicked_in_notification"
        const val EXTRA_VIEW_CLICKED = "extra_id_view_clicked"
    }
}
