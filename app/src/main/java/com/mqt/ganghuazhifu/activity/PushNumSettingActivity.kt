package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.NumListAdapter
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.bean.QueryHistory
import com.mqt.ganghuazhifu.bean.User
import com.mqt.ganghuazhifu.databinding.ActivityPushNumSettingBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

import java.util.ArrayList

/**
 * 推送户号设置

 * @author yang.lei
 * *
 * @date 2016-7-13
 */
class PushNumSettingActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var queryHistoryList: ArrayList<QueryHistory>? = null
    private var adapter: NumListAdapter? = null
    private var activityPushNumSettingBinding: ActivityPushNumSettingBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPushNumSettingBinding = DataBindingUtil.setContentView<ActivityPushNumSettingBinding>(this, R.layout.activity_push_num_setting)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityPushNumSettingBinding!!.toolbar)
        supportActionBar!!.title = "推送户号设置"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityPushNumSettingBinding!!.ibPicRight.setOnClickListener(this)
        activityPushNumSettingBinding!!.listNum.removeHeader()
        activityPushNumSettingBinding!!.listNum.isSwipeEnable = true
        activityPushNumSettingBinding!!.listNum.layoutManager = LinearLayoutManager(this)
        activityPushNumSettingBinding!!.listNum.setOnRefreshListener { initData() }
        activityPushNumSettingBinding!!.listNum.setLoadmoreString("loading")
        activityPushNumSettingBinding!!.listNum.onFinishLoading(false, false)
        adapter = NumListAdapter(this)
        adapter!!.onRecyclerViewItemClickListener = this
        activityPushNumSettingBinding!!.listNum.setAdapter(adapter)
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.ib_pic_right -> startActivity(Intent(this@PushNumSettingActivity, AddPushNumSettingActivity::class.java))
        }
    }

    fun initData() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForQueryPushUser(user.LoginAccount)
        post(HttpURLS.queryPushUser, true, "queryPushUser", body, OnHttpRequestListener { isError, response, type, error ->
            activityPushNumSettingBinding!!.listNum.setOnRefreshComplete()
            activityPushNumSettingBinding!!.listNum.onFinishLoading(true, false)
            if (isError) {
                Logger.e(error.toString())
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    when (type) {
                        0 -> queryHistoryList = null
                        1 -> {
                            val ResponseFields = response.getString("ResponseFields")
                            if (ResponseFields != null) {
                                queryHistoryList = JSONObject.parseArray(ResponseFields,
                                        QueryHistory::class.java) as ArrayList<QueryHistory>
                            }
                        }
                        2 -> {
                            val ResponseFields1 = response.getJSONObject("ResponseFields")
                            if (ResponseFields1 != null) {
                                val UserDetail = ResponseFields1.getString("UserDetail")
                                queryHistoryList = ArrayList<QueryHistory>()
                                if (UserDetail.startsWith("[")) {
                                    queryHistoryList = JSONObject.parseArray(UserDetail,
                                            QueryHistory::class.java) as ArrayList<QueryHistory>
                                } else if (UserDetail.startsWith("{")) {
                                    queryHistoryList!!.add(JSONObject.parseObject(UserDetail, QueryHistory::class.java))
                                }
                            }
                        }
                    }
                    adapter!!.updateList(queryHistoryList)
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    private fun historyCancle(position: Int) {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForPushUserNb(user.LoginAccount,
                queryHistoryList!![position].UserNb, queryHistoryList!![position].PayeeCode,
                "11")
        post(HttpURLS.setPushUserNb, true, "PushUserNb", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    initData()
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    override fun onItemClick(view: View, position: Int) {
        MaterialDialog.Builder(this)
                .title("提醒")
                .content("确定删除推送户号绑定，删除后无法收到该户号的账单推送？")
                .onPositive { dialog, which -> historyCancle(position) }
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText("确定")
                .negativeText("取消")
                .show()
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
