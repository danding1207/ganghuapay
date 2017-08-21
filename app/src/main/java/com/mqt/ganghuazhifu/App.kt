package com.mqt.ganghuazhifu

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDex
import android.view.inputmethod.InputMethodManager
import com.mqt.ganghuazhifu.utils.BanksUtils
import com.mqt.ganghuazhifu.utils.PhoneActiveInfo
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport

class App : Application() {

    override fun onCreate() {

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity?) {
//                appCount++
//                Logger.i("App--->"+activity!!.javaClass.name+":onActivityStarted")
//                RxBus.get().post(RunningInfoEvent())
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityStopped(activity: Activity?) {
//                appCount--
//                Logger.i("App--->"+activity!!.javaClass.name+":onActivityStopped")
//                RxBus.get().post(RunningInfoEvent())
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }
        })
        super.onCreate()
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
//        LeakCanary.install(this)
//        QbSdk.initX5Environment(applicationContext, null)

//        ActivitySwitcher.getInstance().init(this)
//        try {
//            // Kefu sdk 初始化简写方式：
//            ChatClient.getInstance().init(this, ChatClient.Options()
//                    .setAppkey(Constant.Appkey).setTenantId(Constant.TenantId))
//            // 设置为true后，将打印日志到logcat, 发布APP时应关闭该选项
//            if (ChatClient.getInstance() != null)
//                ChatClient.getInstance().setDebugMode(true)
//            // Kefu EaseUI的初始化
//            if (UIProvider.getInstance() != null)
//                UIProvider.getInstance().init(this)
//        } catch (nullPointerException: NullPointerException) {
//
//        }
        manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        phoneInfo = PhoneActiveInfo(applicationContext)


        CrashReport.initCrashReport(applicationContext, "900027303", true)

//        val strategy = CrashReport.UserStrategy(context)
//        strategy.setAppChannel(PackerNg.getMarket(this).equals("")?"ganghua":PackerNg.getMarket(this));//设置渠道
//        strategy.appVersion = BuildConfig.VERSION_NAME//App的版本
//        Bugly.init(context, "900027303", false, strategy)
//        Bugly.setIsDevelopmentDevice(context, true)

        Logger.init("ganghuazhifu")
        BanksUtils.getBanksUtils().initBanks()
        context = applicationContext
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
         var context: Context? = null
         val instance: Context by lazy {
            context!!
        }
        var phoneInfo: PhoneActiveInfo? = null
        var manager: InputMethodManager? = null
        var appCount = 0

        /**
         * 方法3：通过ActivityLifecycleCallbacks来批量统计Activity的生命周期，来做判断，此方法在API 14以上均有效，但是需要在Application中注册此回调接口
         * 必须：
         * 1. 自定义Application并且注册ActivityLifecycleCallbacks接口
         * 2. AndroidManifest.xml中更改默认的Application为自定义
         * 3. 当Application因为内存不足而被Kill掉时，这个方法仍然能正常使用。虽然全局变量的值会因此丢失，但是再次进入App时候会重新统计一次的
         * @param
         * *
         * @return
         */
        val isRunningForeground: Boolean
            get() = appCount > 0
    }

}
