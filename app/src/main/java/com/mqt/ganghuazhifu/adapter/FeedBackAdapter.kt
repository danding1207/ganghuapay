package com.mqt.ganghuazhifu.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View

import com.mqt.ganghuazhifu.BaseActivity
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.FeedBack
import com.mqt.ganghuazhifu.fragment.AccountFragment
import com.mqt.ganghuazhifu.utils.EncryptedPreferencesUtils

import java.util.ArrayList

class FeedBackAdapter(private val mContext: BaseActivity) : BaseAdapter<FeedBack>() {

    var userName:String? = ""

    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }

    init {
        list = ArrayList<FeedBack>()
        layoutResId = R.layout.activity_feed_back_center_item
        val user = EncryptedPreferencesUtils.getUser()
        userName = user.LoginAccount
    }

    @SuppressLint("ResourceAsColor")
    override fun convert(helper: BaseViewHolder, position: Int, item: FeedBack) {
        helper.setText(R.id.tv_feed_back_title, item.title)
                .setImageBitmap(R.id.iv_head_pic, AccountFragment.headerpic)
                .setText(R.id.tv_feed_back_content, item.feekbackquestion)
                .setVisible(R.id.ll_feed_back_answer, if (!(TextUtils.isEmpty(item.replycontent) || item.replycontent == "[]")) View.VISIBLE else View.GONE)
                .setText(R.id.tv_feed_back_time, item.operationtime)
                .setText(R.id.tv_feed_back_answer_time, if (TextUtils.isEmpty(item.replycontent) || item.replycontent == "[]") "" else item.replytime)
                .setText(R.id.tv_feed_back_answer_content, item.replycontent)
                .setText(R.id.tv_feed_back_user_name, userName)
    }

}
