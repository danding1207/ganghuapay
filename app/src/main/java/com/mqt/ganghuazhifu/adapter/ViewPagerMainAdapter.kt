package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

class ViewPagerMainAdapter(fm: FragmentManager,
                           private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {
    private val context: Context? = null

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}
