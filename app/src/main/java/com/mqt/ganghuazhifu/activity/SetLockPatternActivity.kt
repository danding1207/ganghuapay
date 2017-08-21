package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.FuntionsListAdapter
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.bean.Funtions
import com.mqt.ganghuazhifu.databinding.ActivitySetLockPatternBinding
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils

import java.util.ArrayList


/**
 * 手势密码设置

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class SetLockPatternActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var funtionsList: ArrayList<Funtions>? = null
    private var adapter: FuntionsListAdapter? = null
    private var time: Long = 0
    private var activitySetLockPatternBinding: ActivitySetLockPatternBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySetLockPatternBinding = DataBindingUtil.setContentView<ActivitySetLockPatternBinding>(this, R.layout.activity_set_lock_pattern)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activitySetLockPatternBinding!!.toolbar)
        supportActionBar!!.title = "手势密码设置"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        activitySetLockPatternBinding!!.listLockFuntions.layoutManager = LinearLayoutManager(this)
        activitySetLockPatternBinding!!.listLockFuntions.setHasFixedSize(true)
        funtionsList = ArrayList<Funtions>()
        funtionsList!!.add(Funtions("用密码修改", null, null, 0, 1))

        val user = EncryptedPreferencesUtils.getUser()
        if (user.GesturePwd != null && user.GesturePwd != "9DD4E461268C8034F5C8564E155C67A6")
            funtionsList!!.add(Funtions("用原手势密码修改", null, null, 0, 1))

        adapter = FuntionsListAdapter(this, 1)

        adapter!!.updateList(funtionsList)
        adapter!!.onRecyclerViewItemClickListener = this
        activitySetLockPatternBinding!!.listLockFuntions.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun OnViewClick(v: View) {

    }

    override fun onItemClick(view: View, position: Int) {
        if (time == 0L || System.currentTimeMillis() - time > 1000) {
            val intent: Intent
            when (position) {
                0 -> VerifyPasswordActivity.startActivity(this@SetLockPatternActivity, 3)
                1 -> {
                    intent = Intent(this@SetLockPatternActivity, LockPatternActivity::class.java)
                    intent.putExtra("TYPE", 1)
                    startActivity(intent)
                }
            }
            time = System.currentTimeMillis()
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
