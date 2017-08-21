package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.InvoiceAdapter
import com.mqt.ganghuazhifu.bean.FeedBack
import com.mqt.ganghuazhifu.bean.Invoice
import com.mqt.ganghuazhifu.databinding.ActivityFapiaoListBinding
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import java.util.*

/**
 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class FapiaoListActivity : BaseActivity(), OnRecyclerViewItemClickListener {



    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var activityFapiaoListBinding: ActivityFapiaoListBinding? = null

    private var adapter: InvoiceAdapter? = null
    private var list: ArrayList<Invoice>? = ArrayList()
    private var page = 0
    private var pageLast = page.toString() + ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFapiaoListBinding = DataBindingUtil.setContentView<ActivityFapiaoListBinding>(this,
                R.layout.activity_fapiao_list)
        initView()
        initdata()
    }

    private fun initView() {
        setSupportActionBar(activityFapiaoListBinding!!.toolbar)
        supportActionBar!!.title = "发票管理"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activityFapiaoListBinding!!.listFapiao.removeHeader()
        activityFapiaoListBinding!!.listFapiao.isSwipeEnable = true
        activityFapiaoListBinding!!.listFapiao.setLoadmoreString("正在加载...")
        activityFapiaoListBinding!!.listFapiao.onFinishLoading(true, false)
        activityFapiaoListBinding!!.listFapiao.layoutManager = LinearLayoutManager(this)
        adapter = InvoiceAdapter(this)
        activityFapiaoListBinding!!.listFapiao.setAdapter(adapter)
        adapter!!.onRecyclerViewItemClickListener = this

        activityFapiaoListBinding!!.listFapiao.setPagingableListener {
            page++
            if (Integer.parseInt(pageLast) <= page) {
                ToastUtil.toastInfo("最后一页了!")
                activityFapiaoListBinding!!.listFapiao.onFinishLoading(false, false)
                pageLast = page.toString() + ""
            } else {
                initdata()
            }
        }

        activityFapiaoListBinding!!.listFapiao.setOnRefreshListener {
            page = 0
            initdata()
            if (Integer.parseInt(pageLast) == 1) {
                ToastUtil.toastInfo("已到达第一页!")
                pageLast = page.toString() + ""
            }
        }
    }

    private fun initdata() {
        list = ArrayList()
        list!!.add(Invoice("1", "泰州港华有限责任公司", "323143245", "464.60", "雷杨", "2017-05-10"))
        list!!.add(Invoice("1", "泰州港华有限责任公司", "323143245", "132.00", "北京驰波名气通数据服务责任有限公司", "2017-04-30"))
        list!!.add(Invoice("1", "泰州港华有限责任公司", "323143245", "71.40", "天才", "2016-10-08"))
        adapter!!.updateList(list)
        activityFapiaoListBinding!!.listFapiao.setOnRefreshComplete()
        activityFapiaoListBinding!!.listFapiao.onFinishLoading(true, false)
    }

    override fun onItemClick(view: View?, position: Int) {
        FapiaoShowActivity.startActivity(this)
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
            val intent = Intent(context, FapiaoListActivity::class.java)
            context.startActivity(intent)
        }
    }

}
