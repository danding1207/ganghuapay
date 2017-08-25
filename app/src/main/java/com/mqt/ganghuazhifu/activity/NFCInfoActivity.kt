package com.mqt.ganghuazhifu.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.hwangjr.rxbus.RxBus
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.RecordChangedEvent
import com.mqt.ganghuazhifu.databinding.ActivityNfcInfoBinding
import com.mqt.ganghuazhifu.ext.post
import com.mqt.ganghuazhifu.http.HttpRequestParams
import com.mqt.ganghuazhifu.http.HttpURLS
import com.mqt.ganghuazhifu.listener.OnHttpRequestListener
import com.mqt.ganghuazhifu.utils.ToastUtil
import com.orhanobut.logger.Logger
import com.xtkj.nfcjar.bean.PayResultBean
import org.parceler.Parcels
import java.math.BigDecimal

/**
 * NFC燃气表读表数据显示

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class NFCInfoActivity : BaseActivity() {

    private var ordernb: String? = null
    private var bean: PayResultBean? = null
    private var activityNfcInfoBinding: ActivityNfcInfoBinding? = null
    private var shebeiType: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNfcInfoBinding = DataBindingUtil.setContentView<ActivityNfcInfoBinding>(this,
                R.layout.activity_nfc_info)
        bean = Parcels.unwrap<PayResultBean>(intent.getParcelableExtra<Parcelable>("PayResultBean"))
        ordernb = intent.getStringExtra("Ordernb")
        shebeiType = intent.getIntExtra("ShebeiType", 1)
        initView()
        initData()
    }

    private fun initData() {
        if (isActiveNetwork) {
            val body = HttpRequestParams.getParamsForUpdateNFCPayStatus(ordernb, "11", bean!!.adjustBottomNum,
                    bean!!.payBottomNum)
            post(HttpURLS.updateNFCPayStatus, true, "NFCSignMsg", body, OnHttpRequestListener { isError, response, type, error ->
                if (isError) {
                    Logger.e(error.toString())
                    MaterialDialog.Builder(this@NFCInfoActivity)
                            .title("提醒")
                            .content("充值结果回抄需要连接网络，请确保您已连接网络再试")
                            .onPositive { dialog, which -> initData() }
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .positiveText("重试")
                            .show()
                } else {
                    Logger.i(response.toString())
                    val ResponseHead = response.getJSONObject("ResponseHead")
                    val ProcessCode = ResponseHead.getString("ProcessCode")
                    val ProcessDes = ResponseHead.getString("ProcessDes")
                    if (ProcessCode == "0000") {
                        Logger.i("EventBus.getDefault().post")
                        RxBus.get().post(RecordChangedEvent())
                        MaterialDialog.Builder(this@NFCInfoActivity)
                                .title("提醒")
                                .content("回抄成功，感谢您的使用，如有问题请联系我们")
                                .positiveText("确定")
                                .show()
                    } else {
                        ToastUtil.toastError(ProcessDes)
                    }
                }
            })
        } else {
            MaterialDialog.Builder(this@NFCInfoActivity)
                    .title("提醒")
                    .content("充值结果回抄需要连接网络，请确保您已连接网络再试")
                    .onPositive { dialog, which -> initData() }
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText("重试")
                    .show()
        }
    }

    private val isActiveNetwork: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            return isConnected
        }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    private fun initView() {
        setSupportActionBar(activityNfcInfoBinding!!.toolbar)
        supportActionBar!!.title = "NFC燃气表"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (bean != null) {
            val tv_result = StringBuffer()
            val toatalUseGas = BigDecimal(bean!!.totalUse)
            val remainMoney = BigDecimal(bean!!.remainMoney)
            val totalPay = BigDecimal(bean!!.totalPay)
            val f = BigDecimal(1)
//            val f = BigDecimal(100)
            val ten = BigDecimal(10)


            var toatalUseGasString: String? = null
            var remainMoneyString: String? = null
            var totalPayString: String? = null

            when (shebeiType) {
                1 -> {
                    toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()
                    remainMoneyString = remainMoney.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()
                    totalPayString = totalPay.divide(f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()
                    tv_result.append("剩余金额（元）：" + remainMoneyString + "\n")
                    tv_result.append("累计购气金额（元）：" + totalPayString + "\n")
                }
                2 -> {
                    toatalUseGasString = toatalUseGas.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()
                    remainMoneyString = remainMoney.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()
                    totalPayString = totalPay.divide(ten).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()
                    tv_result.append("剩余气量（m³）：" + remainMoneyString + "\n")
                    tv_result.append("累计购气气量（m³）：" + totalPayString + "\n")
                }
            }

            tv_result.append("充值结果：" + "\n\n")
            tv_result.append("充值次数：" + bean!!.payTimes + "\n")
            tv_result.append("累计用气量（m3）：" + toatalUseGasString + "\n")
            tv_result.append("表状态：" + bean!!.meterState)
            activityNfcInfoBinding!!.tvNfcInfo.text = tv_result
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {}

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
