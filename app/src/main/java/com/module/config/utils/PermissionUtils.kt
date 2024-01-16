package com.module.config.utils

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.vmadalin.easypermissions.EasyPermissions

internal const val REQUEST_CODE_LOCATION = 1
internal const val REQUEST_CODE_STORAGE_PERMISSION = 2
internal const val REQUEST_CODE_POST_NOTIFICATION = 3
internal const val REQUEST_CODE_RECORD_AUDIO = 4

object PermissionUtils {

    fun hasDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun hasUsageAccess(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val applicationInfo =
                    context.packageManager.getApplicationInfo(context.packageName, 0)
                val appOpsManager: AppOpsManager =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode: Int = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
                return (mode == AppOpsManager.MODE_ALLOWED)

            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        } else {
            true
        }
    }

    fun requestUsageAccess(context: Context) {
        if (!hasUsageAccess(context)) {
            var intent: Intent
            try {
                intent = Intent(
                    Settings.ACTION_USAGE_ACCESS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            } catch (e: Exception) {
                intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
                e.printStackTrace()
            }
        }
    }

    fun requestDrawOverlays(context: Context) {
        if (!hasDrawOverlays(context)) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openPermissionBatterySave(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName: String = context.packageName
            val pm: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val intent = Intent()
            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            else {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun openPermissionAutoStart(context: Context) {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            when (manufacturer.lowercase()) {
                "xiaomi" -> {
                    intent.component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }

                "oppo" -> {
                    intent.component = ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }

                "vivo" -> {
                    intent.component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }

                "letv" -> {
                    intent.component = ComponentName(
                        "com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity"
                    )
                }

                "honor" -> {
                    intent.component = ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                    )
                }
            }
            val listResolveInfo = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (listResolveInfo.size > 0) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onCheckDeviceAutoStart(): Boolean {
        val manufacturer = Build.MANUFACTURER
        return manufacturer.lowercase() == "xiaomi" ||
                manufacturer.lowercase() == "oppo" ||
                manufacturer.lowercase() == "vivo" ||
                manufacturer.lowercase() == "letv" ||
                manufacturer.lowercase() == "honor"
    }

    fun hasWriteSetting(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            true
        }
    }

    fun hasStorage(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.hasPermissions(
                    context,
                    perms = arrayOf(
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            } else {
                EasyPermissions.hasPermissions(
                    context,
                    perms = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }

        } else {
            true
        }
    }

    fun hasReadImage(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                EasyPermissions.hasPermissions(
                    context,
                    perms = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        } else {
            return true
        }
    }

    fun hasReadVideo(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else {
                EasyPermissions.hasPermissions(
                    context,
                    perms = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        } else {
            return true
        }
    }

    fun hasReadAudio(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            } else {
                EasyPermissions.hasPermissions(
                    context,
                    perms = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        } else {
            return true
        }
    }

    fun requestAllStorage(activity: Activity, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.requestPermissions(
                    activity,
                    content,
                    REQUEST_CODE_STORAGE_PERMISSION,
                    perms = arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                )
            } else {
                EasyPermissions.requestPermissions(
                    activity,
                    content,
                    REQUEST_CODE_STORAGE_PERMISSION,
                    perms = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    fun requestStorage(activity: Activity, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                activity,
                content,
                REQUEST_CODE_STORAGE_PERMISSION,
                perms = arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            EasyPermissions.requestPermissions(
                activity,
                content,
                REQUEST_CODE_STORAGE_PERMISSION,
                perms = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    fun requestReadAudio(activity: Activity, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                activity,
                content,
                REQUEST_CODE_STORAGE_PERMISSION,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            EasyPermissions.requestPermissions(
                activity,
                content,
                REQUEST_CODE_STORAGE_PERMISSION,
                perms = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    fun hasPostNotification(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.hasPermissions(
                    context,
                    perms = arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            } else {
                true
            }
        } else {
            true
        }
    }

    fun requestPostNotification(activity: Activity, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                activity,
                content,
                REQUEST_CODE_POST_NOTIFICATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    fun hasRecordAudio(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EasyPermissions.hasPermissions(
                context,
                perms = arrayOf(Manifest.permission.RECORD_AUDIO)
            )
        } else {
            true
        }
    }

    fun requestRecordAudio(activity: Activity, content: String) {
        EasyPermissions.requestPermissions(
            activity,
            content,
            REQUEST_CODE_RECORD_AUDIO,
            Manifest.permission.RECORD_AUDIO
        )
    }
}