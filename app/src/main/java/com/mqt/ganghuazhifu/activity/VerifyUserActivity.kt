package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.GeneralContact
import com.mqt.ganghuazhifu.bean.Unit
import com.mqt.ganghuazhifu.databinding.ActivityVerifyUserBinding
import com.mqt.ganghuazhifu.event.UnitySelectedEvent
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels

/**
 * 验证用户

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class VerifyUserActivity : BaseActivity() {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var unit: Unit? = null
    private var generalContact: GeneralContact? = null
    private var activityVerifyUserBinding: ActivityVerifyUserBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVerifyUserBinding = DataBindingUtil.setContentView<ActivityVerifyUserBinding>(this, R.layout.activity_verify_user)
        generalContact = Parcels.unwrap<GeneralContact>(intent.getParcelableExtra<Parcelable>("GeneralContact"))
        initView()
    }

    private fun initView() {
        unit = Unit()
        activityVerifyUserBinding!!.scrollViewAll.setOnTouchListener { v, event -> this@VerifyUserActivity.currentFocus != null && this@VerifyUserActivity.currentFocus!!.windowToken != null && App.manager!!.hideSoftInputFromWindow(this@VerifyUserActivity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS) }
        setSupportActionBar(activityVerifyUserBinding!!.toolbar)
        supportActionBar!!.title = "绑定联系人"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (generalContact != null) {
            unit = Unit()
            unit!!.PayeeCode = generalContact!!.payeecode
            unit!!.PayeeNm = generalContact!!.payeename
        }
        activityVerifyUserBinding!!.linearLayoutUnit.setOnClickListener(this)
        activityVerifyUserBinding!!.cardViewNext.setOnClickListener(this)
        activityVerifyUserBinding!!.tvNext.text = "下一步"
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.linearLayout_unit -> startActivityForResult(Intent(this, SelectUnityListActivity::class.java), 8)
            R.id.cardView_next ->
                if (checkEmpty()) {
                    addGeneralContact()
                }
        }
    }

    private fun addGeneralContact() {
        val body: CusFormBody
        val user = EncryptedPreferencesUtils.getUser()
        if (user.GeneralContactCount == 0)
            body = HttpRequestParams.getParamsForAddGeneralContact(user.Uid, unit!!.PayeeCode, activityVerifyUserBinding!!.etNum.text.toString().trim { it <= ' ' }, activityVerifyUserBinding!!.etName.text.toString().trim { it <= ' ' }, "1", "")
        else
            body = HttpRequestParams.getParamsForAddGeneralContact(user.Uid, unit!!.PayeeCode, activityVerifyUserBinding!!.etNum.text.toString().trim { it <= ' ' }, activityVerifyUserBinding!!.etName.text.toString().trim { it <= ' ' }, "0", "")
        post(HttpURLS.addGeneralContact, true, "AddGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    ToastUtil.Companion.toastSuccess("绑定成功!")
                    ScreenManager.getScreenManager().popAllActivityExceptOne(ChangYongUserActivity::class.java)
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    @Subscribe(thread = EventThread.MAIN_THREAD)
    fun onUnitySelectedEvent(event: UnitySelectedEvent) {
        Logger.e("UnitySelectedEvent")
        if (event != null) {
            if (event.data != null) {
                if (event.data.getParcelableExtra<Parcelable>("Unity") != null) {
                    unit = Parcels.unwrap<Unit>(event.data.getParcelableExtra<Parcelable>("Unity"))
                    if (unit != null && activityVerifyUserBinding != null) {
                        activityVerifyUserBinding!!.tvUnit.text = unit!!.PayeeNm
                    }
                }
            }
        }
    }

    private fun checkEmpty(): Boolean {
        if (unit == null) {
            ToastUtil.Companion.toastWarning("请选择缴费单位!")
            return false
        }
        if (TextUtils.isEmpty(activityVerifyUserBinding!!.etNum.text.toString())) {
            ToastUtil.Companion.toastWarning("请填写户号!")
            return false
        }
        if (TextUtils.isEmpty(activityVerifyUserBinding!!.etName.text.toString())) {
            ToastUtil.Companion.toastWarning("请填写用户名!")
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {

        fun startActivity(context: Context, generalContact: GeneralContact?) {
            val intent = Intent(context, VerifyUserActivity::class.java)
            if (generalContact != null)
                intent.putExtra("GeneralContact", Parcels.wrap(generalContact))
            context.startActivity(intent)
        }
    }

}
