package com.mqt.ganghuazhifu.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mqt.ganghuazhifu.listener.OnRecyclerViewItemClickListener
import java.util.ArrayList

abstract class BaseAdapter<T : Any> : RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        var NORMALVIEWHOLDER = 8
        val FOOTERVIEWHOLDER = 9
        val HEADERVIEWHOLDER = 10
    }
    var list: ArrayList<T>? = null
    var layoutResId: Int = 0
    var footerLayoutResId: Int = 0
    var headerLayoutResId: Int = 0
    var onRecyclerViewItemClickListener: OnRecyclerViewItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            NORMALVIEWHOLDER -> {
                val item = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
                val holder = BaseViewHolder(parent.context, item)
                item.setOnClickListener { v ->
                    if (onRecyclerViewItemClickListener != null && holder.layoutPosition >= 0)
                        onRecyclerViewItemClickListener!!.onItemClick(item, holder.layoutPosition)
                }
                return holder
            }
            FOOTERVIEWHOLDER -> {
                val item = LayoutInflater.from(parent.context).inflate(footerLayoutResId, parent, false)
                val holder = BaseViewHolder(parent.context, item)
                return holder
            }
            HEADERVIEWHOLDER -> {
                val item = LayoutInflater.from(parent.context).inflate(headerLayoutResId, parent, false)
                val holder = BaseViewHolder(parent.context, item)
                return holder
            }
        }
        return null!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (headerLayoutResId != 0 && position == 0) {
            convertHeader(holder, position)
        } else if (footerLayoutResId != 0 && position == itemCount - 1) {
            convertFooter(holder, position)
        } else if (headerLayoutResId != 0 && position != 0) {
            convert(holder, position - 1, list!![position - 1])
        } else {
            convert(holder, position, list!![position])
        }
    }

    override fun getItemCount(): Int {
        var i: Int = 0
        if (footerLayoutResId != 0)
            i++
        if (headerLayoutResId != 0)
            i++
        return if (list == null) i else (list!!.size + i)
    }

    override fun getItemViewType(position: Int): Int {
        if (footerLayoutResId != 0 && position == itemCount - 1) {
            return FOOTERVIEWHOLDER
        } else if (headerLayoutResId != 0 && position == 0) {
            return HEADERVIEWHOLDER
        } else {
            return NORMALVIEWHOLDER
        }
    }

    protected abstract fun convert(helper: BaseViewHolder, position: Int, item: T)
    protected abstract fun convertHeader(helper: BaseViewHolder, position: Int)
    protected abstract fun convertFooter(helper: BaseViewHolder, position: Int)

    open fun updateList(list: ArrayList<T>?) {
        this.list!!.clear()
        this.list = ArrayList<T>()
        if (list != null)
            this.list!!.addAll(list)
        this.notifyDataSetChanged()
    }

}
