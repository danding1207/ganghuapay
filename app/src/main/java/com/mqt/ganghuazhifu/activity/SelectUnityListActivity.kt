package com.mqt.ganghuazhifu.activity

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.hwangjr.rxbus.RxBus
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.SelectUnitySortAdapter
import com.mqt.ganghuazhifu.bean.RecordChangedEvent
import com.mqt.ganghuazhifu.bean.Unit
import com.mqt.ganghuazhifu.databinding.ActivitySelectUnityBinding
import com.mqt.ganghuazhifu.event.UnitySelectedEvent
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import org.parceler.Parcels
import java.util.*

/**
 * 联系人列表

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SelectUnityListActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var adapter: SelectUnitySortAdapter? = null
    private var unit: ArrayList<Unit>? = null
    private var type: Int = 0
    private var activitySelectUnityBinding: ActivitySelectUnityBinding? = null

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private var pinyinComparator: PinyinComparator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySelectUnityBinding = DataBindingUtil.setContentView<ActivitySelectUnityBinding>(this, R.layout.activity_select_unity)
        initView()
    }

    override fun OnViewClick(v: View) {

    }

    private fun initView() {
        pinyinComparator = PinyinComparator()
        type = intent.getIntExtra("TYPE", 1)
        setSupportActionBar(activitySelectUnityBinding!!.toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (type == 1) {
            supportActionBar!!.title = "注册"
        } else {
            supportActionBar!!.title = "绑定常用缴费单位"
        }

        adapter = SelectUnitySortAdapter(this)

        activitySelectUnityBinding!!.countryLvcountry.adapter = adapter
        activitySelectUnityBinding!!.countryLvcountry.layoutManager = LinearLayoutManager(this)

        val headersDecor = StickyRecyclerHeadersDecoration(adapter)
        activitySelectUnityBinding!!.countryLvcountry.addItemDecoration(headersDecor)

        adapter!!.onRecyclerViewItemClickListener = this

        adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                headersDecor.invalidateHeaders()
            }
        })

        initData()
    }

    /**
     */
    private fun initData() {
        val body = HttpRequestParams.paramsForPayeesQueryAll
        post(HttpURLS.payeesQueryAll, true, "payeesQueryAll", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                // 判断返回是jsonObject还是jsonArray
                when (type) {
                    0 -> ToastUtil.toastInfo("该城市暂未开通服务!")
                    1 -> {
                        val ResponseFields = response.getString("ResponseFields")
                        if (ResponseFields != null) {
                            unit = ArrayList<Unit>()
                            if (ProcessCode == "0000") {
                                unit = JSONObject.parseArray(ResponseFields, Unit::class.java) as ArrayList<Unit>
                            }
                        }
                    }
                    2 -> {
                        val ResponseFields1 = response.getJSONObject("ResponseFields")
                        if (ResponseFields1 != null) {
                            val PayeesDetail = ResponseFields1.getString("PayeesDetail")
                            unit = ArrayList<Unit>()
                            if (ProcessCode == "0000") {
                                unit!!.add(JSONObject.parseObject(PayeesDetail, Unit::class.java))
                            }
                        }
                    }
                }
                Collections.sort(unit!!, pinyinComparator)
                adapter!!.updateList(unit)
            }
        })
    }

    private fun submit(PayeeCode: String, PayeeNm: String) {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForSetNewPayee(user.LoginAccount, PayeeCode)
        post(HttpURLS.userUpdate, true, "SetNewPayee", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    ToastUtil.toastSuccess("绑定成功!")
                    EncryptedPreferencesUtils.setAscriptionFlag("10")
                    EncryptedPreferencesUtils.setPayeeName(PayeeNm)
                    EncryptedPreferencesUtils.setPayeeCode(PayeeCode)
                    finish()
                } else {
                    ToastUtil.toastError(ProcessDes)
                }
            }
        })
    }

    override fun onItemClick(view: View, position: Int) {
        if (type == 1) {
            val intent = Intent()
            intent.putExtra("Unity", Parcels.wrap(unit!![position]))
            RxBus.get().post(UnitySelectedEvent(intent))
            finish()
        } else {
            MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("您选择的常用缴费单位是：" + unit!![position].PayeeNm + "!")
                    .onPositive { dialog, which -> submit(unit!![position].PayeeCode, unit!![position].PayeeNm) }
                    .positiveText("确定")
                    .negativeText("取消")
                    .show()
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    inner class PinyinComparator : Comparator<Unit> {
        override fun compare(o1: Unit, o2: Unit): Int {
            return o1.Capital.compareTo(o2.Capital)
        }
    }

    override fun onBackPressed() {
        if (type == 1) {
            finish()
        } else {
            MaterialDialog.Builder(this)
                    .title("提醒")
                    .content("请您选择常用缴费单位！")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("确定")
                    .show()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

}
