package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.InputType
import android.view.View
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.databinding.ActivityFapiaoShowBinding
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import com.afollestad.materialdialogs.MaterialDialog

/**
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class FapiaoShowActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var activityFapiaoShowBinding: ActivityFapiaoShowBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFapiaoShowBinding = DataBindingUtil.setContentView<ActivityFapiaoShowBinding>(this,
                R.layout.activity_fapiao_show)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityFapiaoShowBinding!!.toolbar)
        supportActionBar!!.title = "发票详情"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityFapiaoShowBinding!!.tvXiazai.setOnClickListener({
            Logger.e("tv_xiazai")
//            ToastUtil.toastSuccess("pdf发票文件已保存到手机根目录，请查找！")

        })

        activityFapiaoShowBinding!!.tvMail.setOnClickListener({
            Logger.e("tvMail")
            MaterialDialog.Builder(this)
                    .title("邮箱地址")
                    .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .input("请填写要发送至的邮箱", "") { dialog, input ->
                        ToastUtil.toastSuccess("pdf发票文件稍后将发送至邮箱，请查收！")
                    }.show()
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
            R.id.tv_xiazai -> {

            }
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, FapiaoShowActivity::class.java)
            context.startActivity(intent)
        }
    }

}
