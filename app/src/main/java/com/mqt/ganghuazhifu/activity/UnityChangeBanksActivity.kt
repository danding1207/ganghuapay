package com.mqt.ganghuazhifu.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View

import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSONObject
import com.mqt.ganghuazhifu.App
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.adapter.UnityChangeBanksAdapter
import com.mqt.ganghuazhifu.adapter.UnityChangeBanksAdapter.OnBanksSettingClickListener
import com.mqt.ganghuazhifu.bean.BindedBanks
import com.mqt.ganghuazhifu.bean.LianDong
import com.mqt.ganghuazhifu.bean.User
import com.mqt.ganghuazhifu.databinding.ActivityUnityChangeBanksBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.CusFormBody
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger

import org.parceler.Parcels

import java.util.ArrayList

/**
 * 联动优势支付——编辑签约银行

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class UnityChangeBanksActivity : BaseActivity(), OnRecyclerViewItemClickListener, OnBanksSettingClickListener {

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
    }

    private var adapter: UnityChangeBanksAdapter? = null
    private var lianDong: LianDong? = null
    private var list: ArrayList<BindedBanks>? = ArrayList()
    private var isChangMode: Boolean = false
    private var activityUnityChangeBanksBinding: ActivityUnityChangeBanksBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUnityChangeBanksBinding = DataBindingUtil.setContentView<ActivityUnityChangeBanksBinding>(this, R.layout.activity_unity_change_banks)
        lianDong = Parcels.unwrap<LianDong>(intent.getParcelableExtra<Parcelable>("LianDong"))
        initView()
        initdata()
    }

    private fun initView() {
        setSupportActionBar(activityUnityChangeBanksBinding!!.toolbar)
        supportActionBar!!.title = "支付"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityUnityChangeBanksBinding!!.listBindedBanks.layoutManager = LinearLayoutManager(this)
        adapter = UnityChangeBanksAdapter(this, 1, lianDong!!.gateid)
        adapter!!.onRecyclerViewItemClickListener = this
        adapter!!.onBanksSettingClickListener = this
        activityUnityChangeBanksBinding!!.listBindedBanks.adapter = adapter
        activityUnityChangeBanksBinding!!.tvEdit.setOnClickListener(this)
        activityUnityChangeBanksBinding!!.tvChangeBank.setOnClickListener(this)
        activityUnityChangeBanksBinding!!.tvBack.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.tv_edit -> {
                if (isChangMode) {
                    activityUnityChangeBanksBinding!!.tvEdit.text = "编辑"
                    adapter!!.updateType(1)
                } else {
                    activityUnityChangeBanksBinding!!.tvEdit.text = "完成"
                    adapter!!.updateType(2)
                }
                isChangMode = !isChangMode
            }
            R.id.tv_change_bank -> UnitySelectBanksActivity.startActivity(this, lianDong!!)
            R.id.tv_back -> {
                backToResult()
            }
            else -> {
            }
        }
    }

    fun backToResult() {
        val bank = adapter!!.getBindedBank()
        if (bank!=null) {
            lianDong!!.gateid = bank.gateid
            lianDong!!.cardid = bank.cardid
            lianDong!!.mediaid = bank.mediaid
            lianDong!!.paytype = bank.paytype
            val intent = Intent()
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            setResult(Activity.RESULT_OK, intent)
        } else {
            ScreenManager.getScreenManager().popAllActivityJumpOverOneExceptOne(UnitySelectBanksActivity::class.java, PayActivity::class.java)
            UnitySelectBanksActivity.startActivity(this, lianDong)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.finishAfterTransition()
        } else {
            this.finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backToResult()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun initdata() {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForGetLDYSBanks(user.LoginAccount)
        post(HttpURLS.getLDYSBanks, true, "getLDYSBanks", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")

                when (type) {
                    0 -> {
                    }
                    1 -> {
                        val ResponseFields = response.getString("ResponseFields")
                        if (ProcessCode == "0000") {
                            list = JSONObject.parseArray(ResponseFields, BindedBanks::class.java) as ArrayList<BindedBanks>
                        }
                    }
                    2 -> {
                        val ResponseFields1 = response.getJSONObject("ResponseFields")
                        if (ProcessCode == "0000") {
                            val QryResults = ResponseFields1.getString("BankDetail")
                            list!!.add(JSONObject.parseObject(QryResults, BindedBanks::class.java))
                        }
                    }
                }
                if (ProcessCode != "0000") {
                    ToastUtil.Companion.toastError(ProcessDes)
                }

                if (list != null) {
                    adapter!!.updateList(list)
                }
            }
        })
    }

    override fun onItemClick(view: View, position: Int) {
        adapter!!.updateGateid(list!![position].gateid)
        lianDong!!.gateid = list!![position].gateid
        lianDong!!.cardid = list!![position].cardid
        lianDong!!.mediaid = list!![position].mediaid
        lianDong!!.paytype = list!![position].paytype
    }

    override fun onBanksSettingClick(view: View, position: Int) {
        MaterialDialog.Builder(this)
                .title("解除签约卡").content("解除签约卡后将不能使用该卡的快捷支付功能。确定解除吗？")
                .onPositive { dialog, which -> cancleBanks(position) }
                .positiveText("确定").negativeText("取消").show()
    }

    protected fun cancleBanks(position: Int) {
        val user = EncryptedPreferencesUtils.getUser()
        val body = HttpRequestParams.getParamsForlDYSUnbundlingBank(user.LoginAccount,
                list!![position].paytype, list!![position].gateid, list!![position].cardid)
        post(HttpURLS.lDYSUnbundlingBank, true, "lDYSUnbundlingBank", body, OnHttpRequestListener { isError, response, type, error ->
            if (isError) {
                Logger.e(error.toString())
            } else {
                Logger.d(response.toString())
                val ResponseHead = response.getJSONObject("ResponseHead")
                val ResponseFields = response.getString("ResponseFields")
                val ProcessCode = ResponseHead.getString("ProcessCode")
                val ProcessDes = ResponseHead.getString("ProcessDes")
                if (ProcessCode == "0000") {
                    list!!.removeAt(position)
                } else {
                    ToastUtil.Companion.toastError(ProcessDes)
                }
                if (list != null) {
                    adapter!!.updateList(list)
                }
            }
        })
    }

    companion object {
        fun startActivity(context: Context, lianDong: LianDong?) {
            val intent = Intent(context, UnityChangeBanksActivity::class.java)
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            context.startActivity(intent)
        }
        fun startActivityForResult(context: Activity, lianDong: LianDong?, requestCode:Int) {
            val intent = Intent(context, UnityChangeBanksActivity::class.java)
            intent.putExtra("LianDong", Parcels.wrap(lianDong))
            context.startActivityForResult(intent, requestCode)
        }
    }

}
