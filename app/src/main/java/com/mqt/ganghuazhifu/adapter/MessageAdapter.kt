package com.mqt.ganghuazhifu.adapter

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat

import com.mqt.ganghuazhifu.listener.ItemSlideHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.afollestad.materialdialogs.MaterialDialog
import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.Message
import com.mqt.ganghuazhifu.dao.MessageDao
import com.mqt.ganghuazhifu.dao.MessageDaoImpl

import java.sql.SQLException
import java.util.ArrayList

class MessageAdapter(private val mContext: BaseActivity) : BaseAdapter<Message>(), ItemSlideHelper.Callback {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    private var mRecyclerView: RecyclerView? = null

    init {
        list = ArrayList<Message>()
        layoutResId = R.layout.messages_list_item
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        mRecyclerView!!.addOnItemTouchListener(ItemSlideHelper(mRecyclerView!!.context, this))
    }

    private fun cancleMessage(i: Int) {
        val messageDao = MessageDaoImpl(mContext)
        try {
            messageDao.deleteMessage(list!![i].id)
            list!!.removeAt(i)
            notifyItemRemoved(i)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    override fun getHorizontalRange(holder: RecyclerView.ViewHolder): Int {
        if (holder.itemView is LinearLayout) {
            val viewGroup = holder.itemView as ViewGroup
            if (viewGroup.childCount == 2) {
                return viewGroup.getChildAt(1).layoutParams.width + 16
            }
        }
        return 0
    }

    override fun getChildViewHolder(childView: View): RecyclerView.ViewHolder? {
        return mRecyclerView!!.getChildViewHolder(childView)
    }

    override fun findTargetView(x: Float, y: Float): View? {
        return mRecyclerView!!.findChildViewUnder(x, y)
    }

    @SuppressLint("ResourceAsColor")
    override fun convert(helper: BaseViewHolder, position: Int, item: Message) {
        helper.setText(R.id.tv_message_topic, item.getTopic())
                .setText(R.id.tv_message_msg, item.getMsg())
                .setTextColor(R.id.tv_message_topic, if (item.isreaded()) ContextCompat.getColor(mContext, R.color.gray) else ContextCompat.getColor(mContext, R.color.black))
                .setOnClickListener(R.id.tv_message_cancle, View.OnClickListener {
                    MaterialDialog.Builder(mContext)
                            .title("提醒")
                            .content("是否确定删除通知？")
                            .onPositive { dialog, which -> cancleMessage(position) }
                            .positiveText("确定").negativeText("取消")
                            .show()
                })
    }

}
