package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.FuntionsListAdapter
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.bean.Funtions
import com.mqt.ganghuazhifu.databinding.ActivityAccountManagementBinding
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils
import org.parceler.Parcels

import java.util.ArrayList

/**
 * 账户管理
 * @author yang.lei
 * @date 2014-12-24
 */
class AccountManagementActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var funtionsList: ArrayList<Funtions>? = null
    private var adapter: FuntionsListAdapter? = null
    private var activityAccountManagementBinding: ActivityAccountManagementBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAccountManagementBinding = DataBindingUtil.setContentView<ActivityAccountManagementBinding>(this,
                        R.layout.activity_account_management)
        initView()
    }

    override fun onResume() {
        initData()
        super.onResume()
    }

    private fun initView() {
        setSupportActionBar(activityAccountManagementBinding!!.toolbar)
        supportActionBar!!.title = "个人资料"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityAccountManagementBinding!!.listAccountManagementFuntions.layoutManager = LinearLayoutManager(this)
        activityAccountManagementBinding!!.listAccountManagementFuntions.setHasFixedSize(true)
        adapter = FuntionsListAdapter(this, 1)
        adapter!!.onRecyclerViewItemClickListener = this
        activityAccountManagementBinding!!.listAccountManagementFuntions.adapter = adapter
    }

    private fun initData() {
        funtionsList = ArrayList<Funtions>()
        val idCard = EncryptedPreferencesUtils.getUser().IdcardNb!!
        var realname = EncryptedPreferencesUtils.getUser().RealName
        val sb = StringBuilder()
        if (!TextUtils.isEmpty(idCard) && idCard != "[]"
                && idCard.length > 8) {
            sb.append(idCard.substring(0, idCard.length - 8))
            sb.append("****")
            sb.append(idCard.substring(idCard.length - 4, idCard.length))
        } else {
            sb.append("未设置")
        }
        funtionsList!!.add(Funtions("身份证号", null, sb.toString(), 0, 1))
        if (TextUtils.isEmpty(realname) || realname == "[]") {
            realname = "未设置"
        }
        funtionsList!!.add(Funtions("真实姓名", null, realname, 0, 1))
        adapter!!.updateList(funtionsList)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {
    }

    override fun onItemClick(view: View, position: Int) {
        val intent: Intent
        when (position) {
            0 -> {
                intent = Intent(this@AccountManagementActivity,
                        SetEmailActivity::class.java)
                intent.putExtra("TYPE", 2)
                startActivity(intent)
            }
            1 -> {
                intent = Intent(this@AccountManagementActivity,
                        SetEmailActivity::class.java)
                intent.putExtra("TYPE", 3)
                startActivity(intent)
            }
        }
    }

    val FUNTIONLIST = "funtionsList"

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {
        funtionsList = Parcels.unwrap(savedInstanceState.getParcelable(FUNTIONLIST))
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(FUNTIONLIST, Parcels.wrap(funtionsList))
    }

}
