package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.MainActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.adapter.MessageAdapter
import com.mqt.ganghuazhifu.bean.Message
import com.mqt.ganghuazhifu.dao.MessageDaoImpl
import com.mqt.ganghuazhifu.databinding.ActivityMessageCenterBinding
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.utils.ToastUtil
import java.sql.SQLException
import java.util.*

/**
 * 消息中心

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class MessageCenterActivity : BaseActivity(), OnRecyclerViewItemClickListener {

    private var adapter: MessageAdapter? = null
    private var list: ArrayList<Message>? = null
    private var activityMessageCenterBinding: ActivityMessageCenterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMessageCenterBinding = DataBindingUtil.setContentView<ActivityMessageCenterBinding>(this, R.layout.activity_message_center)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityMessageCenterBinding!!.toolbar)
        supportActionBar!!.title = "推送通知"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activityMessageCenterBinding!!.ibPicRight.setOnClickListener(this)
        activityMessageCenterBinding!!.listMessage.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(this)
        adapter!!.onAttachedToRecyclerView(activityMessageCenterBinding!!.listMessage)
        activityMessageCenterBinding!!.listMessage.adapter = adapter
        adapter!!.onRecyclerViewItemClickListener = this
    }

    override fun onResume() {
        initData()
        super.onResume()
    }

    private fun cancleAllMessage() {
        val messageDao = MessageDaoImpl(this)
        try {
            messageDao.deleteAll()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        initData()
    }

    fun initData() {
        val messageDao = MessageDaoImpl(this)
        try {
            list = messageDao.getAllMessage()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        adapter!!.updateList(list)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!ScreenManager.getScreenManager().isContainActivity(MainActivity::class.java) && !ScreenManager.getScreenManager().isContainActivity(LoginActivity::class.java)) {
            // 通过包名获取要跳转的app，创建intent对象
            val intent = packageManager.getLaunchIntentForPackage("com.mqt.ganghuazhifu")
            // 这里如果intent为空，就说名没有安装要跳转的应用嘛
            if (intent != null) {
                // 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
                startActivity(intent)
            }
        }
    }

    override fun OnViewClick(v: View) {
        when (v.id) {
            R.id.ib_pic_right -> {
                list = adapter!!.list
                if (list != null && list!!.size > 0) {
                    MaterialDialog.Builder(this@MessageCenterActivity)
                            .title("提醒")
                            .content("是否确定删除所有通知？")
                            .onPositive { dialog, which -> cancleAllMessage() }
                            .positiveText("确定")
                            .negativeText("取消")
                            .show()
                } else {
                    ToastUtil.toastWarning("没有通知！")
                }
            }
        }
    }

    override fun onItemClick(view: View, position: Int) {
        if (list != null) {
            val message = list!![position]
            val intent = Intent(this@MessageCenterActivity, MessageActivity::class.java)
            intent.putExtra(MessageActivity.MESSAGEID, message.getId())
            startActivity(intent)
        }
    }

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
