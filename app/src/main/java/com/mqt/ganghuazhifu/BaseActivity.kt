package com.mqt.ganghuazhifu

import android.app.Activity
import android.content.Context
import android.content.DialogInterface.OnKeyListener
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
//import com.hitomi.aslibrary.ActivitySwitcher
import com.hwangjr.rxbus.RxBus
import com.mqt.ganghuazhifu.http.HttpRequest
import com.mqt.ganghuazhifu.utils.ScreenManager
import com.mqt.ganghuazhifu.view.LoadingDialog
import com.orhanobut.logger.Logger
import android.view.ViewGroup

abstract class BaseActivity : AppCompatActivity(), OnClickListener {

    val TAG = this.javaClass.simpleName
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colorChange(this, Color.parseColor("#44000000"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        ScreenManager.getScreenManager().pushActivity(this)
        RxBus.get().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ScreenManager.getScreenManager().popActivity(this)
        RxBus.get().unregister(this)
        HttpRequest.instance.onDetachActivity()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        onActivitySaveInstanceState(savedInstanceState)
        Logger.i(localClassName+":onSaveInstanceState")
        super.onSaveInstanceState(savedInstanceState)
    }

    abstract fun onActivitySaveInstanceState(savedInstanceState: Bundle)

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Logger.i(localClassName+":onRestoreInstanceState")
        onActivityRestoreInstanceState(savedInstanceState)
    }

    abstract fun onActivityRestoreInstanceState(savedInstanceState: Bundle)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        ActivitySwitcher.getInstance().processTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (currentFocus != null && currentFocus!!.windowToken != null) {
                return (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 控件的点击事件
     * @param v
     */
    abstract fun OnViewClick(v: View)

    override fun onClick(v: View) {
        OnViewClick(v)
    }

    fun showRoundProcessDialog() {
        val keyListener = OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK) {
                dismissRoundProcessDialog()
                return@OnKeyListener true
            }
            false
        }
        if (loadingDialog == null)
            loadingDialog = LoadingDialog(this)
        loadingDialog!!.setOnKeyListener(keyListener)
        loadingDialog!!.setCanceledOnTouchOutside(false)
        loadingDialog!!.show()
    }

    fun dismissRoundProcessDialog() {
        if (loadingDialog == null)
            loadingDialog = LoadingDialog(this)
        if (loadingDialog!!.isShowing)
            loadingDialog!!.dismiss()
    }

//    /**
//     * 是否全屏和显示标题，true为全屏和无标题，false为无标题，请在setContentView()方法前调用
//     * @param fullScreen
//     */
//    fun setFullScreen(fullScreen: Boolean) {
//        if (fullScreen) {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        } else {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
//        }
//    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
//        val view = window.decorView as ViewGroup
//        view.removeAllViews()
        if (currentFocus != null) {
            App.manager!!.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        // 第一个参数为启动时动画效果，第二个参数为退出时动画效果
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        super.finish()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.finishAfterTransition()
                } else {
                    this.finish()
                }
        }
        return super.onOptionsItemSelected(item)
    }

    //查找布局的底层
    protected fun getRootView(context: Activity): ViewGroup {
        return context.findViewById(android.R.id.content) as ViewGroup
    }

    companion object {
        /**
         * 设置手机状态栏背景色（Android L以上有效）
         */
        fun colorChange(activity: Activity, RGBValues: Int) {
            if (Build.VERSION.SDK_INT >= 21) {
                val window = activity.window
                window.statusBarColor = RGBValues
                window.navigationBarColor = RGBValues
            }
        }
    }

}
