package com.module.config.views.activities.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.module.config.models.MyTab

class MainViewPagerAdapter(
    fm: FragmentManager?,
    private val context: Context,
    tabArr: Array<MyTab>
) :
    FragmentPagerAdapter(fm!!) {
    private val tabList: List<MyTab>

    init {
        tabList = listOf(*tabArr) as List<MyTab>
    }

    override fun getItem(position: Int): Fragment {
        return tabList[position].getmFragment()
    }

    override fun getCount(): Int {
        return tabList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(tabList[position].getmTitle())
    }
}