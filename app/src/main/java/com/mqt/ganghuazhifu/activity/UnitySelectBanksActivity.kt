package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.UnitySelectBanksAdapter
import com.mqt.ganghuazhifu.bean.KuaiQian
import com.mqt.ganghuazhifu.bean.LianDong
import com.mqt.ganghuazhifu.databinding.ActivityUnitySelectBanksBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.BanksUtils
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import org.parceler.Parcels

/**
 * 联动优势支付——选择银行

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnitySelectBanksActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var adapter: UnitySelectBanksAdapter? = null
    private var lianDong: LianDong? = null
    private var kuaiQian: KuaiQian? = null
    private var activityUnitySelectBanksBinding: ActivityUnitySelectBanksBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnitySelectBanksBinding = DataBindingUtil.setContentView<ActivityUnitySelectBanksBinding>(this,
                R.layout.activity_unity_select_banks)
        lianDong = Parcels.unwrap<LianDong>(intent.getParcelableExtra<Parcelable>("LianDong"))
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityUnitySelectBanksBinding!!.toolbar)
        supportActionBar!!.title = "选择银行"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityUnitySelectBanksBinding!!.listViewSelectBanks.layoutManager = LinearLayoutManager(this)
        adapter = UnitySelectBanksAdapter(this, 1)
        activityUnitySelectBanksBinding!!.listViewSelectBanks.adapter = adapter
        adapter!!.updateList(BanksUtils.getBanksUtils().banksDebitCard)
        activityUnitySelectBanksBinding!!.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radioButton_debit_card) {
                adapter!!.updateListType(1)
                adapter!!.updateList(BanksUtils.getBanksUtils().banksDebitCard)
            } else if (checkedId == R.id.radioButton_credit_card) {
                adapter!!.updateListType(2)
                adapter!!.updateList(BanksUtils.getBanksUtils().banksCreditCard)
            }
        }
        adapter!!.onRecyclerViewItemClickListener = this
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    override fun onItemClick(view: View, position: Int) {
        if (adapter!!.type == 1) {
            if (position == BanksUtils.getBanksUtils().banksDebitCard!!.size - 1)
                getPayData(lianDong!!.orderid)
            else
                getPayData(lianDong!!.orderid, "DEBITCARD", BanksUtils.getBanksUtils().banksDebitCard!![position].code)
        } else {
            getPayData(lianDong!!.orderid, "CREDITCARD", BanksUtils.getBanksUtils().banksCreditCard!![position].code)
        }
    }

    fun getPayData(ordernb: String, pay_type: String, gate_id: String) {
        val body = HttpRequestParams.getParamsForGetPayData(ordernb, pay_type, gate_id, "", "", "", "",
                "", "", "", "", "", "11")
        post(HttpURLS.getPayDataForLDYS, true, "GetPayData", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    val lianDong1 = JSONObject.parseObject(ResponseFields, LianDong::class.java)
                    Logger.d(lianDong1.toString())
                    UnityNormalPayActivity.startActivity(this@UnitySelectBanksActivity, lianDong1)
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    fun getPayData(orderNb: String) {
        val PayChannel: String? = "01"
        val body = HttpRequestParams.getParamsForGetPayData(orderNb, PayChannel, HttpURLS.BgPayUrl)
        post(HttpURLS.getPayData, true, "GetPayData", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    kuaiQian = JSONObject.parseObject(ResponseFields, KuaiQian::class.java)
                    Logger.d(kuaiQian.toString())
                    KuaiQianPayActivity.startActivity(this@UnitySelectBanksActivity, kuaiQian)
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
            }
        })
    }

    companion object {
        fun startActivity(context: Context, lianDong: LianDong?) {
            val intent = Intent(context, UnitySelectBanksActivity::class.java)
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            context.startActivity(intent)
        }
    }

}
