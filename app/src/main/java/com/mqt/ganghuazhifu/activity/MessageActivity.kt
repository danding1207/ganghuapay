package com.mqt.ganghuazhifu.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.dao.MessageDaoImpl
import com.mqt.ganghuazhifu.databinding.ActivityMessageBinding
import java.sql.SQLException
import me.leolin.shortcutbadger.ShortcutBadger

/**
 * 消息详情

 * @author yang.lei
 * *
 * @date 2014-12-24
 */
class MessageActivity : BaseActivity() {
    private var messageId: Int = 0
    private var activityMessageBinding: ActivityMessageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMessageBinding = DataBindingUtil.setContentView<ActivityMessageBinding>(this, R.layout.activity_message)
        messageId = intent.getIntExtra(MESSAGEID, -1)
        initView()
    }

    private fun initView() {
        setSupportActionBar(activityMessageBinding!!.toolbar)
        supportActionBar!!.title = "通知"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //数据库操作
        try {
            val messageDao = MessageDaoImpl(this)
            val message = messageDao.getMessageById(messageId)
            if (message == null) {
                activityMessageBinding!!.scrollView.visibility = View.GONE
                activityMessageBinding!!.tvNull.visibility = View.VISIBLE
            } else {
                activityMessageBinding!!.scrollView.visibility = View.VISIBLE
                activityMessageBinding!!.tvNull.visibility = View.GONE
                activityMessageBinding!!.tvTopic.text = message.topic
                activityMessageBinding!!.tvMsg.text = "\t\t\t\t" + message.msg
                activityMessageBinding!!.tvTime.text = message.time

                if (!message.isreaded) {
                    messageDao.updateMessageIsreadedById(messageId, true)
                    ShortcutBadger.applyCount(this, messageDao.getUnreadMessageCount())
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        startActivity(Intent(this, MessageCenterActivity::class.java))
    }

    override fun OnViewClick(v: View) {}

    override fun onActivitySaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onActivityRestoreInstanceState(savedInstanceState: Bundle) {

    }

    companion object {

        val MESSAGEID = "MessageId"
    }
}
