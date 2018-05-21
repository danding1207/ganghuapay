package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityAboutUsBinding
import com.mqt.ganghuazhifu.databinding.ActivityNfcSelectBinding

/**
 * 选择易通表充值方式
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class  NFCSelectActivity : BaseActivity() {

    private var activityNfcSelectBinding: ActivityNfcSelectBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNfcSelectBinding = DataBindingUtil.setContentView<ActivityNfcSelectBinding>(this,
                R.layout.activity_nfc_select)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityNfcSelectBinding!!.toolbar)
        supportActionBar!!.title = "易通表充值方式"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
