package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityFapiaoTaitouBinding
import com.orhanobut.logger.Logger

/**
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class FapiaoTaitouActivity : BaseActivity() {

    var Taitou: String = ""

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var activityFapiaoTaitouBinding: ActivityFapiaoTaitouBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFapiaoTaitouBinding = DataBindingUtil.setContentView<ActivityFapiaoTaitouBinding>(this, R.layout.activity_fapiao_taitou)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityFapiaoTaitouBinding!!.toolbar)
        supportActionBar!!.title = "发票开具"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityFapiaoTaitouBinding!!.tvQueding.setOnClickListener({
            Logger.e("tv_queding")
            Taitou = activityFapiaoTaitouBinding!!.etName.text.toString()
            MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("确定以 "+Taitou+" 为抬头开具电子发票，同一订单不能重复开具！")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive({ materialDialog, dialogAction ->
                        FapiaoShowActivity.startActivity(this@FapiaoTaitouActivity)
                    })
                    .show()
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        Logger.e("OnViewClick")
        when (v.id) {
            R.id.tv_queding -> {

            }
        }
    }

}
