package com.mqt.ganghuazhifu.adapter

import java.util.ArrayList

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.Funtions
import com.mqt.ganghuazhifu.bean.QueryHistory
import com.mqt.ganghuazhifu.listener.OnTapListener

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class FuntionsListAdapter(private val mContext: Context, private val type: Int// 1:不带图标;2:带图标;
) : BaseAdapter<Funtions>() {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    init {
        list = ArrayList<Funtions>()
        layoutResId = R.layout.funtions_list_item
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: Funtions) {
        var img_off: Drawable? = null
        if (type == 2) {
            img_off = ContextCompat.getDrawable(mContext, item.leftResID)
            img_off!!.setBounds(0, 0, img_off.minimumWidth, img_off.minimumHeight)
        }
        helper.setVisible(R.id.tv_funtions_tip, if(item.tip != null) View.VISIBLE else View.GONE).setText(R.id.tv_funtions_tip, if (item.tip != null) item.tip else null).setText(R.id.tv_funtions_name, item.name).setText(R.id.tv_funtions_status, if (TextUtils.isEmpty(item.status)) null else item.status)//				.setVisible(R.id.tv_funtions_status, TextUtils.isEmpty(item.status) ? false : true)
                .setCompoundDrawables(R.id.tv_funtions_name, if (type == 2) img_off else null, null, null, null)
                .setVisible(R.id.iv_filed, if(item.rightResID == 1) View.VISIBLE else View.GONE)
    }

}
