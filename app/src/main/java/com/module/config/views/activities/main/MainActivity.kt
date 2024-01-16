package com.module.config.views.activities.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.module.config.R
import com.module.config.databinding.ActivityMainBinding
import com.module.config.models.MyTab
import com.module.config.rx.RxBusHelper
import com.module.config.rx.RxBusType
import com.module.config.service.RecorderService
import com.module.config.utils.PermissionUtils
import com.module.config.utils.utils_controller.*
import com.module.config.views.activities.GuidePermissionActivity
import com.module.config.views.bases.BaseActivity
import com.module.config.views.dialogs.AskPermissionDialog
import com.module.config.views.dialogs.DialogSingleSelected
import com.module.config.views.fragment.EditVideoFragment
import com.module.config.views.fragment.PictureFragment
import com.module.config.views.fragment.SettingFragment
import com.module.config.views.fragment.VideoFragment

class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {
    private var mainViewPagerAdapter: MainViewPagerAdapter? = null
    private var askPermissionDialog: AskPermissionDialog? = null
    private var mReceiverUpdateData: BroadcastReceiver? = null
    private val videoFragment: VideoFragment = VideoFragment()
    private val pictureFragment: PictureFragment = PictureFragment()
    private val editVideoFragment: EditVideoFragment = EditVideoFragment()
    private var tabArr: Array<MyTab> = arrayOf<MyTab>(
        MyTab(R.string.video, videoFragment, R.drawable.ic_tab_video),
        MyTab(R.string.picture, pictureFragment, R.drawable.ic_tab_picture),
        MyTab(R.string.edit, editVideoFragment, R.drawable.ic_tab_edit),
        MyTab(R.string.setting, SettingFragment(), R.drawable.tab_ic_settings)
    )

    override fun getLayoutActivity() = R.layout.activity_main

    override fun initViews() {
        super.initViews()
        instance = this
        PreferencesHelper.setFirstInstall(this, true)
        ColorUtils.colorStatusBar(this, resources.getColor(R.color.white), true)
        val display = window.windowManager.defaultDisplay
        val outSize = Point()
        display.getRealSize(outSize)
        PreferencesHelper.putString(
            PreferencesHelper.KEY_DEFAULT_RESOLUTION,
            outSize.x.toString() + "x" + outSize.y
        )
        mBinding.navView.itemIconTintList = null
        mainViewPagerAdapter = MainViewPagerAdapter(supportFragmentManager, this, tabArr)
        mBinding.appBarMain.contentMain.viewpager.adapter = mainViewPagerAdapter
        mBinding.appBarMain.contentMain.viewpager.offscreenPageLimit = 5
        mBinding.appBarMain.contentMain.viewpager.addOnPageChangeListener(object :
            OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until mBinding.appBarMain.tabLayout.tabCount) {
                    val tv =
                        ((mBinding.appBarMain.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i) as ViewGroup).getChildAt(
                            1
                        ) as TextView
                    tv.visibility = if (i != position) View.GONE else View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        mBinding.appBarMain.tabLayout.setupWithViewPager(mBinding.appBarMain.contentMain.viewpager)
        for (i in 0 until mBinding.appBarMain.tabLayout.tabCount) {
            mBinding.appBarMain.tabLayout.getTabAt(i)?.setIcon(tabArr[i].getmIcon())
            val tv =
                ((mBinding.appBarMain.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i) as ViewGroup).getChildAt(
                    1
                ) as TextView
            tv.visibility = if (i != 0) View.GONE else View.VISIBLE
        }
        checkActionIntent()
        startService()
        initEvent()
    }

    private fun initEvent() {
        mBinding.appBarMain.imgLanguageMain.setOnClickListener(this)
        mBinding.appBarMain.imgSettingMain.setOnClickListener(this)
    }

    private fun checkActionIntent() {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                Config.ACTION_OPEN_SETTING -> mBinding.appBarMain.contentMain.viewpager.currentItem =
                    3
                Config.ACTION_OPEN_MAIN -> mBinding.appBarMain.contentMain.viewpager.currentItem = 0
            }
        }
    }

    fun startService() {
        val intent = Intent(this, RecorderService::class.java)
        intent.action = Config.ACTION_SHOW_MAIN_FLOATING
        startService(intent)
    }

    private fun onPermissionGranted() {
        PreferencesHelper.putBoolean(PreferencesHelper.KEY_FLOATING_CONTROL, true)
        RxBusHelper.sendPermissionOverlayGranted()
    }

    fun askPermissionStorageMain() {
        if (!PermissionUtils.hasStorage(this)) {
            PermissionUtils.requestStorage(this, "")
        }
    }

    fun askPermissionRecord() {
        if (!PermissionUtils.hasRecordAudio(this)) {
            PermissionUtils.requestRecordAudio(this, "")
        }
    }

    fun askPermissionNotification() {
        if (!PermissionUtils.hasPostNotification(this)) {
            PermissionUtils.requestPostNotification(this, "")
        }
    }

    fun askPermissionOverlay() {
        if (isSystemAlertPermissionGranted(this)) {
            onPermissionGranted()
        } else {
            askPermissionDialog =
                AskPermissionDialog.getInstance(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    object : AskPermissionDialog.SuccessListener {
                        override fun onSuccess() {
                            startActivityForResult(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + applicationContext.packageName)
                                ), REQUEST_SETTING_OVERLAY_PERMISSION
                            )
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    GuidePermissionActivity::class.java
                                )
                            )
                        }

                    })

            askPermissionDialog?.show(
                supportFragmentManager,
                AskPermissionDialog::class.java.name
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SETTING_OVERLAY_PERMISSION) {
            Handler().postDelayed({
                if (isSystemAlertPermissionGranted(this@MainActivity)) {
                    onPermissionGranted()
                }
            }, 200)
        }
    }

    override fun onClickViews() {
        super.onClickViews()
        mBinding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feedback -> Toolbox.feedback(this)
                R.id.nav_rate -> Toolbox.rateApp(this)
                R.id.nav_share -> Toolbox.shareApp(this)
                R.id.nav_language -> {
//                    dialogLanguge = DialogSingleSelected(
//                        this,
//                        getString(R.string.menu_language),
//                        listOf(Config.itemsLanguage),
//                        getCurrentLanguage().getLanguage()
//                    )
//                    dialogLanguge?.callback(object :DialogSingleSelected.CallBackDialog{
//                        setLanguage(selected)
//                    })
//                    dialogLanguge?.show()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.UPDATE_VIDEO_MAIN)
        askPermissionOverlay()
        askPermissionStorageMain()
        askPermissionRecord()
        askPermissionNotification()
        mReceiverUpdateData = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Constants.UPDATE_VIDEO_MAIN) {
                    recreate()
                }
            }
        }
        registerReceiver(mReceiverUpdateData, intentFilter)
    }

    override fun onBackPressed() {
        if (mBinding.appBarMain.contentMain.viewpager.currentItem === 0) {
            finish()
            finishAffinity()
        } else {
            mBinding.appBarMain.contentMain.viewpager.setCurrentItem(0, true)
        }
    }

    override fun onReceivedEvent(type: RxBusType?, data: Any?) {
        when (type) {
            RxBusType.PERMISSION_GRANTED -> startService()
            else -> {}
        }
    }

    override fun onClick(v: View) {
        if (v === mBinding.appBarMain.imgLanguageMain) {
//            dialogLanguge = DialogSingleSelected(
//                this,
//                getString(R.string.menu_language),
//                Arrays.asList(Config.itemsLanguage),
//                getCurrentLanguage().getLanguage()
//            ) { selected -> setLanguage(selected) }
//            dialogLanguge.show()
        }
        if (v === mBinding.appBarMain.imgSettingMain) {
            if (mBinding.appBarMain.contentMain.viewpager.currentItem !== 3) {
                mBinding.appBarMain.contentMain.viewpager.currentItem = 3
            }
        }
        if (v === mBinding.appBarMain.imgRemoveAds) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        RemoveAdsDialog.onDestroyBp()
        instance = null
        if (mReceiverUpdateData != null) {
            unregisterReceiver(mReceiverUpdateData)
        }
//        if (dialogLanguge != null) {
//            dialogLanguge.dismiss()
//        }
        if (askPermissionDialog != null) {
            askPermissionDialog?.dismiss()
        }
    }

    companion object {
        private const val REQUEST_SETTING_OVERLAY_PERMISSION = 290
        var instance: MainActivity? = null
            private set

        fun isSystemAlertPermissionGranted(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT < 23) {
                true
            } else Settings.canDrawOverlays(context)
        }
    }
}