package com.module.config.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import com.module.config.BuildConfig
import com.module.config.R
import com.module.config.app.AppConstants
import com.module.config.views.bases.ext.getCurrentSdkVersion
import com.module.config.views.bases.ext.showToastByString

object AppUtils {
    fun sendFeedbackToMail(context: Context) {
        try {
            // String subject
            val subject = context.getString(
                R.string.email_feedback_subject,
                context.getString(R.string.app_name), context.packageName
            )
            val deviceName = Build.MODEL
            val androidName = context.getCurrentSdkVersion()

            val body = context.getString(
                R.string.email_feedback_body,
                context.getString(R.string.app_name)
            )
            val sentFrom = context.getString(
                R.string.email_feedback_sent_from, deviceName,
                BuildConfig.VERSION_NAME, androidName.toString()
            )

            val builder = StringBuilder()
            builder.append("\n\n\n\n")
            builder.append("-------------------------------")
            builder.append("\n")
            builder.append(body)
            builder.append("\n")
            builder.append("-------------------------------")
            builder.append("\n")
            builder.append(sentFrom)

            val msg = builder.toString()
            sendEmailMore(context, arrayOf(AppConstants.EMAIL_FEEDBACK), subject, msg)
        } catch (e: Exception) {
            context.showToastByString("Install application email")
            e.printStackTrace()
        }
    }

    fun sendEmailMore(
        context: Context,
        addresses: Array<String?>?,
        subject: String,
        body: String
    ) {
        disableExposure(context)
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            context.showToastByString("Install application email")
        }
    }

    private fun disableExposure(context: Context) {
        if (context.getCurrentSdkVersion() >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openMarket(context: Context, packageName: String) {
        val i = Intent(Intent.ACTION_VIEW)
        try {
            i.data = Uri.parse("market://details?id=$packageName")
            context.startActivity(i)
        } catch (ex: ActivityNotFoundException) {
            openBrowser(context, AppConstants.BASE_GOOGLE_PLAY + BuildConfig.APPLICATION_ID)
        }
    }

    fun openGoogleStore(mContext: Context, nameDeveloper: String) {
        try {
            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=$nameDeveloper")
            )
            mContext.startActivity(marketIntent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun openBrowser(context: Context, url: String) {
        var mUrl = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            mUrl = "https://$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))
        try {
            context.startActivity(browserIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun isPackageExisted(context: Context, targetPackage: String): Boolean {
        val pm = context.packageManager
        try {
            val info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA)
            if (info != null) {
                return true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
        return true
    }
}