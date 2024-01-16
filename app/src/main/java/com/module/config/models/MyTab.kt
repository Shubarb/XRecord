package com.module.config.models

import androidx.fragment.app.Fragment

class MyTab(private var mTitle: Int, private var mFragment: Fragment, private var mIcon: Int) {
    fun getmTitle(): Int {
        return mTitle
    }

    fun setmTitle(mTitle: Int) {
        this.mTitle = mTitle
    }

    fun getmFragment(): Fragment {
        return mFragment
    }

    fun setmFragment(mFragment: Fragment) {
        this.mFragment = mFragment
    }

    fun getmIcon(): Int {
        return mIcon
    }

    fun setmIcon(mIcon: Int) {
        this.mIcon = mIcon
    }
}