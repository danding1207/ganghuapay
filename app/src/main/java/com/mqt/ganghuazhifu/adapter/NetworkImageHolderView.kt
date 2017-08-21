package com.mqt.ganghuazhifu.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView

import com.bigkoo.convenientbanner.holder.Holder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mqt.ganghuazhifu.R
import com.mqt.ganghuazhifu.bean.BeanBinnal

/**
 * Created by msc on 2016/9/9.
 */

class NetworkImageHolderView : Holder<BeanBinnal> {

    private var imageView: ImageView? = null

    override fun createView(context: Context): View {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = ImageView(context)
        //            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView!!
    }

    override fun UpdateUI(context: Context, position: Int, data: BeanBinnal) {
        Glide.with(context)
                .load(data.comval)
                .centerCrop().dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.advertisement_placeholder)
                .crossFade().into(imageView!!)
    }
}