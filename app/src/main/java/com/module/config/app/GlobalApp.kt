package com.module.config.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.ga.controller.utils.AppFlyerAnalytics
import com.google.firebase.FirebaseApp
import com.module.config.models.AppsModel
import com.module.config.rx.RxBusHelper
import com.module.config.utils.SharePrefUtils
import com.module.config.utils.utils_controller.PreferencesHelper
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

@HiltAndroidApp
class GlobalApp : MultiDexApplication() {
    companion object {
        var listTargetApp: List<AppsModel>? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: GlobalApp

        @SuppressLint("StaticFieldLeak")
        var mCurrentActivity: Activity? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    init {
        instance = this
    }

    private fun initTargetApp() {
        getAllApp()
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(Consumer{ datas: List<AppsModel> ->
                listTargetApp = datas
                RxBusHelper.sendLoadTargetAppFinished()
            }, Consumer{ _: Throwable? -> })
    }

    fun getAllApp(): Single<List<AppsModel>> {
        return Single.create<List<AppsModel>> { sub: SingleEmitter<List<AppsModel>> ->
            try {
                val pm = packageManager
                val apps: MutableList<AppsModel> = ArrayList()
                val packages =
                    pm.getInstalledPackages(0)
                for (packageInfo in packages) {
                    if (packageName != packageInfo.packageName
                        && pm.getLaunchIntentForPackage(packageInfo.packageName) != null
                    ) {
                        val app = AppsModel(
                            packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                            packageInfo.packageName,
                            packageInfo.applicationInfo.loadIcon(packageManager)
                        )
                        app.isSelectedApp =
                            PreferencesHelper.getString(
                                PreferencesHelper.KEY_APP_SELECTED,
                                ""
                            ).equals(packageInfo.packageName)

                        apps.add(app)
                    }
                }
                sub.onSuccess(apps)
            } catch (e: Exception) {
                sub.onError(e.cause)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        SharePrefUtils.init(this)
        PreferencesHelper.init(this)
        Hawk.init(this).build()
        initTargetApp()
        FirebaseApp.initializeApp(this)
        AppFlyerAnalytics.initAppsflyer(this)
    }
}
