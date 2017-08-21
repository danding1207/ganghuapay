package com.mqt.ganghuazhifu.utils

import android.widget.Toast
import com.mqt.ganghuazhifu.App
import es.dmoral.toasty.Toasty


class ToastUtil {

    companion object {
        fun toastError(msg: String?) {
            if (msg != null)
                Toasty.error(App.instance, msg, Toast.LENGTH_SHORT, true).show()
        }

        fun toastSuccess(msg: String?) {
            if (msg != null)
                Toasty.success(App.instance, msg, Toast.LENGTH_SHORT, true).show()
        }

        fun toastInfo(msg: String?) {
            if (msg != null)
                Toasty.info(App.instance, msg, Toast.LENGTH_SHORT, true).show()
        }

        fun toastWarning(msg: String?) {
            if (msg != null)
                Toasty.warning(App.instance, msg, Toast.LENGTH_SHORT, true).show()
        }

        fun toastErrorLong(msg: String?) {
            if (msg != null)
                Toasty.error(App.instance, msg, Toast.LENGTH_LONG, true).show()
        }

        fun toastSuccessLong(msg: String?) {
            if (msg != null)
                Toasty.success(App.instance, msg, Toast.LENGTH_LONG, true).show()
        }

        fun toastInfoLong(msg: String?) {
            if (msg != null)
                Toasty.info(App.instance, msg, Toast.LENGTH_LONG, true).show()
        }

        fun toastWarningLong(msg: String?) {
            if (msg != null)
                Toasty.warning(App.instance, msg, Toast.LENGTH_LONG, true).show()
        }

    }


//    fun toastNormal(msg: String) {
//        Toasty.normal(context, msg, Toast.LENGTH_SHORT, true).show()
//    }

}
