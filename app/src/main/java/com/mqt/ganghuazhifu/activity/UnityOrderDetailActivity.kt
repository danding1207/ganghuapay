package com.mqt.ganghuazhifu.activity

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.LianDong
import com.mqt.ganghuazhifu.databinding.ActivityUnityOrderDetailBinding
import com.mqt.ganghuazhifu.utils.PreciseCompute

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import org.parceler.Parcels

/**
 * 联动优势支付——订单详细

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnityOrderDetailActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var lianDong: LianDong? = null
    private var activityUnityOrderDetailBinding: ActivityUnityOrderDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnityOrderDetailBinding = DataBindingUtil.setContentView<ActivityUnityOrderDetailBinding>(this, R.layout.activity_unity_order_detail)
        lianDong = Parcels.unwrap<LianDong>(intent.getParcelableExtra<Parcelable>("LianDong"))
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUnityOrderDetailBinding!!.toolbar)
        supportActionBar!!.title = "订单信息"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (lianDong != null) {
            activityUnityOrderDetailBinding!!.tvAmount.text = "￥ " + PreciseCompute.div(lianDong!!.amount, 1.0, 2)
            activityUnityOrderDetailBinding!!.tvOrderTime.text = lianDong!!.merdate
            activityUnityOrderDetailBinding!!.tvOrderNum.text = lianDong!!.orderid
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    companion object {
        fun startActivity(context: Context, lianDong: LianDong) {
            val intent = Intent(context, UnityOrderDetailActivity::class.java)
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            context.startActivity(intent)
        }
    }

}
