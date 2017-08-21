package com.mqt.ganghuazhifu.adapter

import java.util.ArrayList

import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.Unit
import com.mqt.ganghuazhifu.listener.OnTapListener
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SectionIndexer
import android.widget.TextView

import android.R.attr.data

class SelectUnitySortAdapter(private val mContext: Context) : BaseAdapter<Unit>(), StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    override fun convertFooter(helper: BaseViewHolder, position: Int) {
    }

    override fun convertHeader(helper: BaseViewHolder, position: Int) {
    }
    init {
        list = ArrayList<Unit>()
        layoutResId = R.layout.unitys_item
    }

    override fun convert(helper: BaseViewHolder, position: Int, item: Unit) {
        helper.setText(R.id.title, item.PayeeNm).setText(R.id.num, item.PayeeCode).setBackgroundColor(R.id.linearLayout_item, if (item.flag)
            ContextCompat.getColor(mContext, R.color.gray)
        else
            ContextCompat.getColor(mContext, R.color.white))
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    override fun getHeaderId(position: Int): Long {
        return list!![position].Capital.subSequence(0, 1)[0].toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.unitys_item_header, parent, false)
        return object : RecyclerView.ViewHolder(view) {
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tvLetter = holder.itemView.findViewById(R.id.catalog) as TextView
        tvLetter.text = list!![position].Capital.toUpperCase().substring(0, 1)
    }

}