package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.AddNumListAdapter
import com.mqt.ganghuazhifu.bean.GeneralContact
import com.mqt.ganghuazhifu.databinding.ActivityAddPushNumBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels
import java.util.*

/**
 * 绑定推送户号
 * @author yang.lei
 * @date 2014-12-24
 */
class AddPushNumSettingActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var list: ArrayList<GeneralContact>? = null
    private var adapter: AddNumListAdapter? = null
    private var activityAddPushNumBinding: ActivityAddPushNumBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddPushNumBinding = DataBindingUtil.setContentView<ActivityAddPushNumBinding>(this, R.layout.activity_add_push_num)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityAddPushNumBinding!!.toolbar)
        supportActionBar!!.title = "绑定推送户号"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        adapter = AddNumListAdapter()
        activityAddPushNumBinding!!.listAddNum.layoutManager = LinearLayoutManager(this)
        activityAddPushNumBinding!!.listAddNum.setHasFixedSize(true)
        adapter!!.onRecyclerViewItemClickListener = this
        activityAddPushNumBinding!!.listAddNum.adapter = adapter
        initData()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    private fun setPushUserNb(position: Int) {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForPushUserNb(user.LoginAccount,
                list!![position].usernb, list!![position].payeecode,
                "10")
        Logger.i(body.toString())
        post(HttpURLS.setPushUserNb, true, "PushUserNb", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    finish()
                } else {
                    MaterialDialog.Builder(this@AddPushNumSettingActivity).
                            title("提醒").canceledOnTouchOutside(false)
                            .content(ProcessDes)
                            .onPositive { dialog, which -> finish() }
                            .positiveText("确定").show()
                }
            }
        })
    }


    fun initData() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForGeneralContact(user.LoginAccount, user.Uid)
        Logger.i(body.toString())
        post(HttpURLS.processQuery, true, "GeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.i(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    when (type) {
                        0 -> list = ArrayList<GeneralContact>()
                        1 -> {
                            val ResponseFields1 = response.getString("ResponseFields")
                            if (ResponseFields1 != null) {
                                list = JSONObject.parseArray(ResponseFields1, GeneralContact::class.java) as ArrayList<GeneralContact>
                            }
                        }
                        2 -> {
                            val ResponseFields = response.getJSONObject("ResponseFields")
                            val QryResults = ResponseFields.getString("QryResults")
                            if (QryResults != null) {
                                list = ArrayList<GeneralContact>()
                                list!!.add(JSONObject.parseObject(QryResults, GeneralContact::class.java))
                            }
                        }
                    }
                    EncryptedPreferencesUtils.setGeneralContactCount(if (list == null) 0 else list!!.size)
                    adapter!!.updateList(list)
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    override fun onItemClick(view: View, position: Int) {
        setPushUserNb(position)
    }

    val GENERALCONTACTLIST = "GeneralContactList"

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
        list = Parcels.unwrap(savedInstanceState.getParcelable(GENERALCONTACTLIST))
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(GENERALCONTACTLIST, Parcels.wrap(list))
    }

}
