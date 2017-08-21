package com.mqt.ganghuazhifu.view

import com.mqt.ganghuazhifu.R

import android.content.Context
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.Button

class ValidateButton : Button {

    private var timer: CountDownTimer? = null
    private var count: Int = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initTimer()
    }

    constructor(context: Context) : super(context) {
        initTimer()
    }

    private fun initTimer() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                --count
                this@ValidateButton.setText(count.toString() + "秒后重新获取")
            }

            override fun onFinish() {
                this@ValidateButton.setBackgroundResource(R.drawable.unity_green_button)
                this@ValidateButton.setTextColor(ContextCompat.getColor(context, R.color.white))
                this@ValidateButton.isClickable = true
                this@ValidateButton.text = "获取验证码"
            }
        }
    }


    fun startTimer() {
        this@ValidateButton.setBackgroundResource(R.drawable.unity_gray_button)
        this@ValidateButton.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
        this@ValidateButton.isClickable = false
        count = 60
        timer!!.start()
    }

}
