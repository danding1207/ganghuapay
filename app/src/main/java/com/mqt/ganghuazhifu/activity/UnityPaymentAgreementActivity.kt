package com.mqt.ganghuazhifu.activity

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityUnityPaymentAgreementBinding

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

/**
 * 联动优势支付——支付协议

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnityPaymentAgreementActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var activityUnityPaymentAgreementBinding: ActivityUnityPaymentAgreementBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnityPaymentAgreementBinding = DataBindingUtil.setContentView<ActivityUnityPaymentAgreementBinding>(this,
                R.layout.activity_unity_payment_agreement)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUnityPaymentAgreementBinding!!.toolbar)
        supportActionBar!!.title = "支付协议"
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

}
