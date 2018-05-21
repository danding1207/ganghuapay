package com.mqt.ganghuazhifu.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.GeneralContactListAdapter
import com.mqt.ganghuazhifu.adapter.GeneralContactListAdapter.OnItemClickListener
import com.mqt.ganghuazhifu.bean.GeneralContact
import com.mqt.ganghuazhifu.databinding.ActivityChangYongUserBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.util.*

/**
 * 常用联系人
 * @author yang.lei
 * @date 2016-7-13
 */
class ChangYongUserActivity : BaseActivity(), OnItemClickListener {

    private var list: ArrayList<GeneralContact>? = null
    private var adapter: GeneralContactListAdapter? = null

    private var activityChangYongUserBinding: ActivityChangYongUserBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChangYongUserBinding = DataBindingUtil.setContentView<ActivityChangYongUserBinding>(this,
                R.layout.activity_chang_yong_user)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityChangYongUserBinding!!.toolbar)
        supportActionBar!!.title = "常用联系人管理"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityChangYongUserBinding!!.ibPicRight.setOnClickListener(this)
        activityChangYongUserBinding!!.listNum.removeHeader()
        activityChangYongUserBinding!!.listNum.isSwipeEnable = true
        activityChangYongUserBinding!!.listNum.layoutManager = LinearLayoutManager(this)
        activityChangYongUserBinding!!.listNum.setOnRefreshListener { initData() }
        activityChangYongUserBinding!!.listNum.setLoadmoreString("loading")
        activityChangYongUserBinding!!.listNum.onFinishLoading(false, false)
        adapter = GeneralContactListAdapter(this)
        adapter!!.setOnItemClickListener(this)
        activityChangYongUserBinding!!.listNum.setAdapter(adapter)
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
            R.id.ib_pic_right//
            -> if (list != null && list!!.size > 0) {
                val temp: GeneralContact? = list!!.firstOrNull { "1" == it.isdefault }
                if (temp != null)
                    VerifyUserActivity.startActivity(this@ChangYongUserActivity, temp)
            } else {
                VerifyUserActivity.startActivity(this@ChangYongUserActivity, null)
            }
        }

    }

    fun initData() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForGeneralContact(user.LoginAccount, user.Uid)
        post(HttpURLS.processQuery, true, "GeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
            activityChangYongUserBinding!!.listNum.setOnRefreshComplete()
            activityChangYongUserBinding!!.listNum.onFinishLoading(true, false)
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
                                list = JSONObject.parseArray(ResponseFields1,
                                        GeneralContact::class.java) as ArrayList<GeneralContact>
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
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    private fun delete(position: Int) {
        val body = HttpRequestParams.getParamsForDeleteGeneralContact(list!![position].id)
        post(HttpURLS.deleteGeneralContact, true, "DeleteGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
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

    private fun update(position: Int) {
        val body = HttpRequestParams.getParamsForUpdateGeneralContact(list!![position].id,
                list!![position].remark, "1")
        post(HttpURLS.updateGeneralContact, true, "UpdateGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
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

    private fun update(position: Int, remark: String) {
        val body = HttpRequestParams.getParamsForUpdateGeneralContact(list!![position].id,
                remark, list!![position].isdefault)
        post(HttpURLS.updateGeneralContact, true, "UpdateGeneralContact", body, OnHttpRequestListener { isError, response, type, error ->
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

    override fun onItemClick(type: Int, position: Int) {
        when (type) {
            0 -> {
            }
            1 -> update(position)
            2 -> MaterialDialog.Builder(this)
                    .title("修改备注")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("请填写备注", list!![position].remark) { dialog, input ->
                        if (input.toString().length > 8) {
                            App.manager!!.hideSoftInputFromWindow(dialog.inputEditText!!.windowToken, 0)
                            ToastUtil.toastWarning("备注名长度不能大于8")
                        } else {
                            update(position, input.toString())
                        }
                    }.show()
            3 -> MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("确定要删除该常用联系人吗？")
                    .positiveText("確定")
                    .onPositive { dialog, which -> delete(position) }
                    .negativeText("取消")
                    .show()
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }
}
