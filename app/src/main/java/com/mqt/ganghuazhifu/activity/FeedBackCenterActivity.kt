package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.FeedBackAdapter
import com.mqt.ganghuazhifu.bean.FeedBack
import com.mqt.ganghuazhifu.databinding.ActivityFeedBackCenterBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.util.*

/**
 * 意见反馈
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class FeedBackCenterActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var activityFeedBackCenterBinding: ActivityFeedBackCenterBinding? = null
    private var adapter: FeedBackAdapter? = null
    private var list: ArrayList<FeedBack>? = ArrayList()
    private var page = 0
    private var pageLast = page.toString() + ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFeedBackCenterBinding = DataBindingUtil.setContentView<ActivityFeedBackCenterBinding>(this, R.layout.activity_feed_back_center)
        initView()
    }

    override fun onResume() {
        super.onResume()
        initdata()
    }

    private fun initView() {
        setSupportActionBar(activityFeedBackCenterBinding!!.toolbar)
        supportActionBar!!.title = "意见反馈"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityFeedBackCenterBinding!!.tvTitleRight.setOnClickListener(this)
        activityFeedBackCenterBinding!!.listFeedBack.removeHeader()
        activityFeedBackCenterBinding!!.listFeedBack.isSwipeEnable = true
        activityFeedBackCenterBinding!!.listFeedBack.setLoadmoreString("正在加载...")
        activityFeedBackCenterBinding!!.listFeedBack.onFinishLoading(true, false)
        activityFeedBackCenterBinding!!.listFeedBack.layoutManager = LinearLayoutManager(this)
        adapter = FeedBackAdapter(this)
        activityFeedBackCenterBinding!!.listFeedBack.setAdapter(adapter)
        adapter!!.onRecyclerViewItemClickListener = this

        activityFeedBackCenterBinding!!.listFeedBack.setPagingableListener {
            page++
            if (Integer.parseInt(pageLast) <= page) {
                ToastUtil.toastInfo("最后一页了!")
                activityFeedBackCenterBinding!!.listFeedBack.onFinishLoading(false, false)
                pageLast = page.toString() + ""
            } else {
                initdata()
            }
        }

        activityFeedBackCenterBinding!!.listFeedBack.setOnRefreshListener {
            page = 0
            initdata()
            if (Integer.parseInt(pageLast) == 1) {
                ToastUtil.toastInfo("已到达第一页!")
                pageLast = page.toString() + ""
            }
        }

    }

    private fun initdata() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForFeedbacks(user.LoginAccount!!, null)
        post(HttpURLS.processQuery, true, "FeedBack", body, OnHttpRequestListener { isError, response, type, error ->
            activityFeedBackCenterBinding!!.listFeedBack.setOnRefreshComplete()
            activityFeedBackCenterBinding!!.listFeedBack.onFinishLoading(true, false)
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                val CurrentCount = ResponseHead.getString("CurrentCount")
                val OrgnSerialNumber = ResponseHead.getString("OrgnSerialNumber")
                val PageCount = ResponseHead.getString("PageCount")
                pageLast = PageCount
                if (PageCount == null || Integer.parseInt(PageCount) == 0) {
                    ToastUtil.toastInfo("当前条件没有查询到数据!")
                }
                if (ProcessCode == "0000") {
                    when (type) {
                        0 -> list = null
                        1 -> {
                            val ResponseFields = response.getString("ResponseFields")
                            if (ResponseFields != null) {
                                list = JSONObject.parseArray(ResponseFields,
                                        FeedBack::class.java) as ArrayList<FeedBack>
                            }
                        }
                        2 -> {
                            val ResponseFields1 = response.getJSONObject("ResponseFields")
                            if (ResponseFields1 != null) {
                                val UserDetail = ResponseFields1.getString("QryResults")
                                list = ArrayList<FeedBack>()
                                if (UserDetail.startsWith("[")) {
                                    list = JSONObject.parseArray(UserDetail,
                                            FeedBack::class.java) as ArrayList<FeedBack>
                                } else if (UserDetail.startsWith("{")) {
                                    list!!.add(JSONObject.parseObject(UserDetail, FeedBack::class.java))
                                }
                            }
                        }
                    }
                    adapter!!.updateList(list)
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
            R.id.tv_title_right -> startActivity(Intent(this, FeedBackActivity::class.java))
        }
    }

    override fun onItemClick(view: View, position: Int) {}

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
