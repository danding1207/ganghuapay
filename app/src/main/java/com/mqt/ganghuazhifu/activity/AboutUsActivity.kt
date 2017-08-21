package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityAboutUsBinding

/**
 * 关于我们
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class AboutUsActivity : BaseActivity() {

    private var activityAboutUsBinding: ActivityAboutUsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAboutUsBinding = DataBindingUtil.setContentView<ActivityAboutUsBinding>(this, R.layout.activity_about_us)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityAboutUsBinding!!.toolbar)
        supportActionBar!!.title = "关于"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (App.phoneInfo!!.appVersion != "NULL")
            activityAboutUsBinding!!.tvVersion.text = "港华交易宝V " + App.phoneInfo!!.appVersion
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

}
