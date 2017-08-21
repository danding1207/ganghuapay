package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact
import com.mqt.ganghuazhifu.databinding.ActivityUpdataGeneralcontactBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels

/**
 * 修改常用联系人

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UpdataGeneralContactActivity : BaseActivity() {

    private var generalContact: GeneralContact? = null
    private var activityUpdataGeneralcontactBinding: ActivityUpdataGeneralcontactBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUpdataGeneralcontactBinding = DataBindingUtil.setContentView<ActivityUpdataGeneralcontactBinding>(this, R.layout.activity_updata_generalcontact)
        generalContact = Parcels.unwrap<GeneralContact>(intent.getParcelableExtra<Parcelable>("GeneralContact"))
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUpdataGeneralcontactBinding!!.toolbar)
        supportActionBar!!.title = "修改联系人"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityUpdataGeneralcontactBinding!!.tvTitleRight.visibility = View.VISIBLE
        activityUpdataGeneralcontactBinding!!.tvTitleRight.setOnClickListener(this)
        activityUpdataGeneralcontactBinding!!.tvUsername.text = generalContact!!.usernm
        activityUpdataGeneralcontactBinding!!.tvUsernum.text = generalContact!!.usernb
        activityUpdataGeneralcontactBinding!!.tvDanwei.text = generalContact!!.cdtrnm
        val s = if (generalContact!!.remark != null && generalContact!!.remark == "[]") "" else generalContact!!.remark
        activityUpdataGeneralcontactBinding!!.etMark.setText(s)
        if (generalContact!!.isdefault == "0") {
            activityUpdataGeneralcontactBinding!!.mCheckSwithcButton.isSelected = false
            activityUpdataGeneralcontactBinding!!.llDelete.visibility = View.VISIBLE
            activityUpdataGeneralcontactBinding!!.llMoren.visibility = View.VISIBLE
            activityUpdataGeneralcontactBinding!!.llDelete.setOnClickListener { v ->
                MaterialDialog.Builder(this@UpdataGeneralContactActivity)
                        .title("提醒")
                        .content("确定要删除该常用联系人吗？")
                        .onPositive { dialog, which -> delete() }
                        .positiveText("确定")
                        .negativeText("取消")
                        .show()
            }
        } else {
            activityUpdataGeneralcontactBinding!!.mCheckSwithcButton.isSelected = true
            activityUpdataGeneralcontactBinding!!.llDelete.visibility = View.GONE
            activityUpdataGeneralcontactBinding!!.llMoren.visibility = View.GONE
            activityUpdataGeneralcontactBinding!!.llDelete.setOnClickListener(null)
        }
    }

    private fun delete() {
        val body = HttpRequestParams.getParamsForDeleteGeneralContact(generalContact!!.id)
        post(HttpURLS.deleteGeneralContact, true, "DeleteGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    finish()
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    private fun update() {
        val remark = activityUpdataGeneralcontactBinding!!.etMark.text.toString()
        val isDefault = if (activityUpdataGeneralcontactBinding!!.mCheckSwithcButton.isSelected) "1" else "0"
        val body = HttpRequestParams.getParamsForUpdateGeneralContact(generalContact!!.id, remark, isDefault)
        post(HttpURLS.updateGeneralContact, true, "UpdateGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    finish()
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_title_right -> update()
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    companion object {

        fun startActivity(context: Context) {

        }
    }
}
