package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mittsu.markedview.MarkedView

open class BaseViewHolder (private val context: Context, private val convertView: View) : RecyclerView.ViewHolder(convertView) {

    private val views: SparseArray<View>

    init {
        this.views = SparseArray<View>()
    }

    protected fun <T : View> retrieveView(viewId: Int): T? {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = convertView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T?
    }

    fun setText(viewId: Int, value: CharSequence?): BaseViewHolder {
        val view = retrieveView<TextView>(viewId)
        if (view != null)
            view.text = value
        return this
    }

    fun setMDText(viewId: Int, value: String?): BaseViewHolder {
        val view = retrieveView<MarkedView>(viewId)
        view?.setMDText(value)
        return this
    }

    fun setImageResource(viewId: Int, resId: Int): BaseViewHolder {
        val view = retrieveView<ImageView>(viewId)
        view!!.setImageResource(resId)
        return this
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap?): BaseViewHolder {
        val view = retrieveView<ImageView>(viewId)
        view!!.setImageBitmap(bitmap)
        return this
    }

    fun setImageUrl(viewId: Int, imageUrl: String?): BaseViewHolder {
        val view = retrieveView<ImageView>(viewId)
        //        Picasso.with(context).load(imageUrl).into(view);
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): BaseViewHolder {
        val view = retrieveView<View>(viewId)
        view!!.setBackgroundColor(color)
        return this
    }

    fun setVisible(viewId: Int, visible: Int): BaseViewHolder {
        val view = retrieveView<View>(viewId)
        view!!.visibility = visible
//        if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun removeAllViews(viewId: Int): BaseViewHolder {
        val view = retrieveView<View>(viewId) as ViewGroup
        view.removeAllViews()
        return this
    }

    fun addView(viewId: Int, viewChildren: View, params:android.view.ViewGroup.LayoutParams): BaseViewHolder {
        val view:ViewGroup = retrieveView<View>(viewId) as ViewGroup
        view.addView(viewChildren, params)
        return this
    }

    fun linkify(viewId: Int): BaseViewHolder {
        val view = retrieveView<TextView>(viewId)
        Linkify.addLinks(view!!, Linkify.ALL)
        return this
    }

    //此处省略若干常用赋值常用方法
    fun setCompoundDrawables(viewId: Int, left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?): BaseViewHolder {
        val view = retrieveView<TextView>(viewId)
        view!!.setCompoundDrawables(left, top, right, bottom)
        return this
    }

    fun setOnClickListener(viewId: Int, listener: OnClickListener?): BaseViewHolder {
        val view = retrieveView<View>(viewId)
        if (listener != null) {
            view!!.setOnClickListener(listener)
        }
        return this
    }

    fun setTextColor(viewId: Int, resID: Int): BaseViewHolder {
        val view = retrieveView<TextView>(viewId)
        view!!.setTextColor(resID)
        return this
    }

}
